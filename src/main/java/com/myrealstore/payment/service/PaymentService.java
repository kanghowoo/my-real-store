package com.myrealstore.payment.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myrealstore.membercoupon.service.MemberCouponService;
import com.myrealstore.payment.exception.PaymentProcessingException;
import com.myrealstore.payment.exception.PointChargeException;
import com.myrealstore.payment.client.PaymentClient;
import com.myrealstore.payment.service.request.PaymentApprovalServiceRequest;
import com.myrealstore.payment.service.request.PaymentCancelServiceRequest;
import com.myrealstore.payment.service.response.PaymentApprovalResponse;
import com.myrealstore.point.service.PointService;
import com.myrealstore.point.service.request.PointEventServiceRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {
    private final Map<String, PaymentClient> paymentClientMap;
    private final PointService pointService;
    private final MemberCouponService memberCouponService;
    private final PaymentRetryQueue paymentRetryQueue;

    public PaymentApprovalResponse requestApproval(PaymentApprovalServiceRequest approvalRequest) {
        PaymentClient client = getPaymentClient(approvalRequest.getProvider());

        int finalAmount = getFinalAmount(approvalRequest);

        PaymentApprovalServiceRequest actualRequest = approvalRequest.withAmount(finalAmount);
        PaymentApprovalResponse paymentResponse = requestExternalPaymentApproval(actualRequest);

        try {
            chargePoint(actualRequest);
        } catch (PointChargeException e) {
            log.error("포인트 적립 실패, 결제 취소 시도");
            handlePointChargeFailure(client, approvalRequest.toPaymentCancelServiceRequest());
            throw new PaymentProcessingException("포인트 충전 중 오류가 발생하여 결제를 취소했습니다.");
        } catch (Exception e) {
            handlePointChargeFailure(client, approvalRequest.toPaymentCancelServiceRequest());
            throw new PaymentProcessingException("포인트 충전 중 예기치 못한 오류");
        }

        return paymentResponse;
    }

    private int getFinalAmount(PaymentApprovalServiceRequest approvalRequest) {
        int finalAmount = approvalRequest.getAmount();
        Long couponId = approvalRequest.getMemberCouponId();

        if (couponId != null) {
            finalAmount = memberCouponService.applyCoupon(couponId, finalAmount);
        }
        return finalAmount;
    }

    private void chargePoint(PaymentApprovalServiceRequest request) {
        PointEventServiceRequest pointChargeRequest = PointEventServiceRequest.builder()
                                                                              .memberId(request.getMemberId())
                                                                              .amount(request.getAmount())
                                                                              .reason(request.getProvider())
                                                                              .build();

        try {
            pointService.chargePoint(pointChargeRequest);
        } catch (Exception e) {
            if (e instanceof PointChargeException) {
                throw (PointChargeException) e;
            }
        }
    }

    private PaymentApprovalResponse requestExternalPaymentApproval(PaymentApprovalServiceRequest request) {
        PaymentClient client = getPaymentClient(request.getProvider());

        try {
            PaymentApprovalResponse response = client.requestPaymentApproval(request);

            if (response.isPaymentDisApproved()) {
                throw new PaymentProcessingException("결제 승인 실패");
            }

            return response;
        } catch (PaymentProcessingException e) {
            throw e;
        } catch (Exception e) {
            throw new PaymentProcessingException("결제사 승인 요청 중 오류 발생");
        }

    }

    private void handlePointChargeFailure(PaymentClient client, PaymentCancelServiceRequest cancelRequest) {

        try {
            client.requestPaymentCancel(cancelRequest);
            log.info("결제 [{}] 환불 성공", cancelRequest.getPaymentKey());
        } catch (Exception refundError) {
            log.error("결제 환불 실패: {}",refundError.getMessage(), refundError);
            paymentRetryQueue.retryPaymentCancel(cancelRequest, 60_000);
        }
    }

    private PaymentClient getPaymentClient(String provider) {
        PaymentClient client = paymentClientMap.get(provider);

        if (client == null) {
            throw new UnsupportedOperationException("지원하지 않는 결제사 입니다.");
        }

        return client;
    }

}
