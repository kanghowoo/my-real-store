package com.myrealstore.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myrealstore.point.domain.Point;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
}
