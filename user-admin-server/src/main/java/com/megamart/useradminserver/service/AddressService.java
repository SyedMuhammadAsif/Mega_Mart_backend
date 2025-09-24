package com.megamart.useradminserver.service;

import com.megamart.useradminserver.dto.AddressDto;
import com.megamart.useradminserver.entity.UserAddress;

import java.util.List;

public interface AddressService {
    List<UserAddress> getUserAddresses(String userId);
    UserAddress getAddressById(String userId, Long addressId);
    UserAddress addAddress(String userId, AddressDto addressDto);
    UserAddress updateAddress(String userId, Long addressId, AddressDto addressDto);
    void deleteAddress(String userId, Long addressId);
    void setDefaultAddress(String userId, Long addressId);
}