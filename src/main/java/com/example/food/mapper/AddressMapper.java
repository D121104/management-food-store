package com.example.food.mapper;

import com.example.food.dto.request.address.AddressRequest;
import com.example.food.dto.response.address.AddressResponse;
import com.example.food.entity.AddressEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface AddressMapper {
    void updateEntityFromRequest(AddressRequest request, @MappingTarget AddressEntity entity);
    AddressResponse toAddressResponse(AddressEntity addressEntity);
}
