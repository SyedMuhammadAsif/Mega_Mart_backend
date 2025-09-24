package com.megamart.orderpaymentserver.repository;

import com.megamart.orderpaymentserver.entity.OrderTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderTrackingRepository extends JpaRepository<OrderTracking, Long> {
    
    List<OrderTracking> findByOrderIdOrderByCreatedAtAsc(Long orderId);
}
