package com.example.food.service;

import com.example.food.entity.UserEntity;
import com.example.food.exception.AppException;
import com.example.food.exception.ErrorCode;
import com.example.food.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CustomerService {
    private final UserService userService;
    private final UserRepository userRepository;

    @Transactional
    public String createCustomer(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        CustomerCreateParams params =
                CustomerCreateParams.builder()
                        .setName(user.getFullName())
                        .setEmail(user.getEmail())
                        .setPhone(user.getPhoneNumber())
                        .build();

        Customer customer;
        try {
            customer = Customer.create(params);
        }  catch (StripeException e) {
            throw new AppException(ErrorCode.CREATE_CUSTOMER_FAILED);
        }

        return  customer.getId();
    }
}
