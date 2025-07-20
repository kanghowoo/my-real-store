package com.myrealstore.payment.service;

import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myrealstore.global.common.error.ErrorCode;
import com.myrealstore.global.common.exception.BusinessException;
import com.myrealstore.global.common.exception.EntityNotFoundException;
import com.myrealstore.membercoupon.service.MemberCouponService;
import com.myrealstore.membercoupon.service.request.ApplyCouponServiceRequest;
import com.myrealstore.membercoupon.service.response.ApplyCouponResponse;
import com.myrealstore.payment.client.PaymentClient;
import com.myrealstore.payment.domain.Payment;
import com.myrealstore.payment.exception.PaymentProcessingException;
import com.myrealstore.payment.repository.PaymentRepository;
import com.myrealstore.payment.service.request.PaymentConfirmServiceRequest;
import com.myrealstore.payment.service.request.PaymentCreateServiceRequest;
import com.myrealstore.payment.service.response.PaymentConfirmResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final Map<String, PaymentClient> paymentClientMap;
    private final MemberCouponService memberCouponService;
    private final PaymentRepository paymentRepository;
    private final PaymentRetryQueue paymentRetryQueue;

    public void preparePayment(PaymentConfirmServiceRequest request) {
        ApplyCouponServiceRequest couponRequest =
                ApplyCouponServiceRequest.builder()
                                         .memberId(request.getMemberId())
                                         .memberCouponId(request.getMemberCouponId())
                                         .originalAmount(request.getAmount())
                                         .build();

        ApplyCouponResponse couponResponse = memberCouponService.applyCoupon(couponRequest);

        PaymentCreateServiceRequest paymentCreateServiceRequest =
                PaymentCreateServiceRequest.builder()
                                           .memberId(request.getMemberId())
                                           .orderId(request.getOrderId())
                                           .paymentKey(request.getPaymentKey())
                                           .memberCouponId(couponResponse.getMemberCouponId())
                                           .finalAmount(couponResponse.getFinalAmount())
                                           .discountAmount(couponResponse.getDiscountAmount())
                                           .build();

        savePayment(paymentCreateServiceRequest);
    }

    private void savePayment(PaymentCreateServiceRequest request) {
        try {
            Payment payment = Payment.createReadyPayment(request);
            paymentRepository.save(payment);
        } catch (DataIntegrityViolationException e) {
            log.warn("중복 결제 저장 시도 - orderId={}, paymentKey={}", request.getOrderId(), request.getPaymentKey());
            throw new BusinessException(ErrorCode.DUPLICATED_PAYMENT_REQUEST);
        }

    }

    @Transactional
    public PaymentConfirmResponse confirmPayment(PaymentConfirmServiceRequest request) {
        /**
         * 클라이언트가 결제 버튼 눌렀을 때 호출되어 결제 정보가 먼저 저장됨. 따로 호출하는게 맞지만 클라이언트가 수행했다고 가정.
         */
        preparePayment(request);

        PaymentClient client = getPaymentClient(request.getProvider());

        try {
            PaymentConfirmResponse response = client.requestConfirm(request);
            Payment payment = paymentRepository.findByOrderId(request.getOrderId()).orElseThrow(
                    () -> new EntityNotFoundException("결제 정보 없음"));

            payment.confirm(response.getPaymentKey());

            return PaymentConfirmResponse.of(payment);
        } catch (PaymentProcessingException e) {
            paymentRetryQueue.retryConfirmPayment(request, 60_000);
            throw new PaymentProcessingException("결제사 확인 요청 실패, 재시도");
        } catch (Exception e) {
            log.error("결제사 확인 요청 중 오류 발생", e);
            throw e;
        }
    }

    @Transactional
    public PaymentConfirmResponse confirmPaymentAlwaysReturnOk(PaymentConfirmServiceRequest request) {
        preparePayment(request);

        Payment payment = paymentRepository.findByOrderId(request.getOrderId()).orElseThrow(
                () -> new EntityNotFoundException("결제 정보 없음"));

        PaymentClient client = getPaymentClient(request.getProvider());
        PaymentConfirmResponse response = client.requestConfirmAlwaysReturnOk(request);

        payment.confirm(response.getPaymentKey());
        return PaymentConfirmResponse.of(payment);
    }

    private PaymentClient getPaymentClient(String provider) {
        PaymentClient client = paymentClientMap.get(provider);

        if (client == null) {
            throw new UnsupportedOperationException("지원하지 않는 결제사 입니다.");
        }

        return client;
    }

}
