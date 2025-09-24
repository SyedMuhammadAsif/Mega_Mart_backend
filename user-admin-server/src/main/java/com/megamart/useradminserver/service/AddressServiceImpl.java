package com.megamart.useradminserver.service;

import com.megamart.useradminserver.dto.AddressDto;
import com.megamart.useradminserver.entity.UserAddress;
import com.megamart.useradminserver.exception.ResourceNotFoundException;
import com.megamart.useradminserver.repository.UserAddressRepository;
import com.megamart.useradminserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {
    
    private final UserAddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public List<UserAddress> getUserAddresses(String userId) {
        validateUserExists(userId);
        return addressRepository.findByUserId(userId);
    }

    @Override
    public UserAddress getAddressById(String userId, Long addressId) {
        validateUserExists(userId);
        
        UserAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));
        
        if (!address.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Address not found for user: " + userId);
        }
        
        return address;
    }

    @Override
    public UserAddress addAddress(String userId, AddressDto addressDto) {
        validateUserExists(userId);
        
        UserAddress address = new UserAddress();
        address.setUserId(userId);
        mapDtoToEntity(addressDto, address);
        
        return addressRepository.save(address);
    }

    @Override
    public UserAddress updateAddress(String userId, Long addressId, AddressDto addressDto) {
        validateUserExists(userId);
        
        UserAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));
        
        if (!address.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Address not found for user: " + userId);
        }
        
        mapDtoToEntity(addressDto, address);
        return addressRepository.save(address);
    }

    @Override
    public void deleteAddress(String userId, Long addressId) {
        validateUserExists(userId);
        
        UserAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));
        
        if (!address.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Address not found for user: " + userId);
        }
        
        addressRepository.delete(address);
    }

    @Override
    public void setDefaultAddress(String userId, Long addressId) {
        validateUserExists(userId);
        
        UserAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));
        
        if (!address.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Address not found for user: " + userId);
        }
        
        // Reset all addresses to non-default
        List<UserAddress> userAddresses = addressRepository.findByUserId(userId);
        userAddresses.forEach(addr -> addr.setIsDefault(false));
        addressRepository.saveAll(userAddresses);
        
        // Set the selected address as default
        address.setIsDefault(true);
        addressRepository.save(address);
    }

    private void validateUserExists(String userId) {
        if (!userRepository.existsByUserId(userId)) {
            throw new ResourceNotFoundException("User not found with userId: " + userId);
        }
    }

    private void mapDtoToEntity(AddressDto dto, UserAddress entity) {
        entity.setFullName(dto.getFullName());
        entity.setAddressLine1(dto.getAddressLine1());
        entity.setAddressLine2(dto.getAddressLine2());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setPostalCode(dto.getPostalCode());
        entity.setCountry(dto.getCountry());
        entity.setPhone(dto.getPhone());
        entity.setIsDefault(dto.getIsDefault());
    }
}