package com.megamart.orderpaymentserver.repository;

import com.megamart.orderpaymentserver.entity.ProcessingLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessingLocationRepository extends JpaRepository<ProcessingLocation, Long> {
    
    List<ProcessingLocation> findByActiveTrue();
}
