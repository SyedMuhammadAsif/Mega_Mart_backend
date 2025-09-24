package com.megamart.orderpaymentserver.controller;

import com.megamart.orderpaymentserver.entity.ProcessingLocation;
import com.megamart.orderpaymentserver.repository.ProcessingLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/processing-locations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProcessingLocationController {
    
    private final ProcessingLocationRepository locationRepository;
    
    @GetMapping
    public ResponseEntity<List<ProcessingLocation>> getAllLocations() {
        List<ProcessingLocation> locations = locationRepository.findByActiveTrue();
        return ResponseEntity.ok(locations);
    }
}
