package com.example.food.service;

import com.example.food.dto.request.address.AddressRequest;
import com.example.food.dto.response.address.AddressResponse;
import com.example.food.entity.AddressEntity;
import com.example.food.entity.UserEntity;
import com.example.food.exception.AppException;
import com.example.food.exception.ErrorCode;
import com.example.food.mapper.AddressMapper;
import com.example.food.repository.AddressRepository;
import com.example.food.repository.UserRepository;
import com.example.food.security.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Transactional
    public void createAddress(AddressRequest addressRequest, String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        AddressEntity addressEntity = AddressEntity.builder()
                .address(addressRequest.getAddress())
                .longitude(addressRequest.getLongitude())
                .latitude(addressRequest.getLatitude())
                .userId(userId)
                .build();
        addressRepository.save(addressEntity);
    }

    @Transactional
    public void updateAddress(AddressRequest addressRequest, String accessToken, Long addressId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        AddressEntity addressEntity = addressRepository.findById(addressId).orElseThrow(
                () -> new AppException(ErrorCode.ADDRESS_NOT_FOUND)
        );

        if (addressEntity.getUserId() != userId) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        addressMapper.updateEntityFromRequest(addressRequest, addressEntity);
        addressRepository.save(addressEntity);
    }

    @Transactional(readOnly = true)
    public AddressResponse getAddress(Long addressId, String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        AddressEntity addressEntity = addressRepository.findByUserIdAndId(userId, addressId);

        if (addressEntity == null) {
            throw new AppException(ErrorCode.ADDRESS_NOT_FOUND);
        }

        return  addressMapper.toAddressResponse(addressEntity);
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getAddressByUser(String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        List<AddressEntity> addressEntities = addressRepository.findAllByUserId(userId);

        List<AddressResponse> addressResponses = new ArrayList<>();

        for (AddressEntity addressEntity : addressEntities) {
            addressResponses.add(addressMapper.toAddressResponse(addressEntity));
        }
        return addressResponses;
    }

    @Transactional
    public void deleteAddress(Long addressId, String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        AddressEntity addressEntity = addressRepository.findByUserIdAndId(userId, addressId);
        if (addressEntity == null) {
            throw new AppException(ErrorCode.ADDRESS_NOT_FOUND);
        }

        addressRepository.delete(addressEntity);
    }
}


