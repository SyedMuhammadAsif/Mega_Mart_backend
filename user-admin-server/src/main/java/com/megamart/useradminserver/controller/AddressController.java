package com.megamart.useradminserver.controller;

import com.megamart.useradminserver.dto.AddressDto;
import com.megamart.useradminserver.dto.MessageDto;
import com.megamart.useradminserver.entity.UserAddress;
import com.megamart.useradminserver.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/addresses")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@Tag(name = "Address Management", description = "APIs for managing user addresses")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    @Operation(summary = "Get user addresses", description = "Get all addresses for a specific user")
    public ResponseEntity<List<UserAddress>> getUserAddresses(@PathVariable String userId) {
        return ResponseEntity.ok(addressService.getUserAddresses(userId));
    }

    @GetMapping("/{addressId}")
    @Operation(summary = "Get address by ID", description = "Get a specific address by address ID for a user")
    public ResponseEntity<UserAddress> getAddressById(@PathVariable String userId, @PathVariable Long addressId) {
        UserAddress address = addressService.getAddressById(userId, addressId);
        return ResponseEntity.ok(address);
    }

    @PostMapping
    @Operation(summary = "Add new address", description = "Add a new address for a user")
    public ResponseEntity<UserAddress> addAddress(@PathVariable String userId, @Valid @RequestBody AddressDto addressDto) {
        UserAddress address = addressService.addAddress(userId, addressDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(address);
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "Update address", description = "Update an existing address")
    public ResponseEntity<UserAddress> updateAddress(@PathVariable String userId, @PathVariable Long addressId, @Valid @RequestBody AddressDto addressDto) {
        UserAddress address = addressService.updateAddress(userId, addressId, addressDto);
        return ResponseEntity.ok(address);
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "Delete address", description = "Delete a user address")
    public ResponseEntity<MessageDto> deleteAddress(@PathVariable String userId, @PathVariable Long addressId) {
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.ok(new MessageDto("Address deleted successfully"));
    }

    @PutMapping("/{addressId}/default")
    @Operation(summary = "Set default address", description = "Set an address as the default address for a user")
    public ResponseEntity<MessageDto> setDefaultAddress(@PathVariable String userId, @PathVariable Long addressId) {
        addressService.setDefaultAddress(userId, addressId);
        return ResponseEntity.ok(new MessageDto("Default address set successfully"));
    }
}