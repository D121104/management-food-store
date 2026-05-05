package com.example.food.service;

import com.example.food.common.CardBrand;
import com.example.food.configuration.StripeConfig;
import com.example.food.dto.request.payment_method.CreatePaymentMethodRequest;
import com.example.food.dto.request.payment_method.UpdatePaymentMethodRequest;
import com.example.food.dto.response.payment_method.PaymentMethodResponse;
import com.example.food.entity.PaymentMethodEntity;
import com.example.food.entity.UserEntity;
import com.example.food.exception.AppException;
import com.example.food.exception.ErrorCode;
import com.example.food.mapper.PaymentMethodMapper;
import com.example.food.repository.PaymentMethodRepository;
import com.example.food.repository.UserRepository;
import com.example.food.security.TokenHelper;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.PaymentMethodCreateParams;
import com.stripe.param.PaymentMethodDetachParams;
import com.stripe.param.PaymentMethodUpdateParams;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;
    private final PaymentMethodMapper paymentMethodMapper;
    private final StripeConfig stripeConfig;

    @Transactional
    public PaymentMethodResponse createPaymentMethod(String accessToken, CreatePaymentMethodRequest createPaymentMethodRequest) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        PaymentMethod paymentMethod;
//        phần này là code tạo payment method bằng token của stripe
//        Map<String, Object> card = new HashMap<>();
//        card.put("token", "tok_visa");
//
//        Map<String, Object> billingDetails = new HashMap<>();
//        billingDetails.put("name", "holderName");
//
//        Map<String, Object> params = new HashMap<>();
//        params.put("type", "card");
//        params.put("card", card);
//        params.put("billing_details", billingDetails);
        try {
//            paymentMethod = PaymentMethod.create(params);
            paymentMethod = PaymentMethod.retrieve(createPaymentMethodRequest.getPaymentMethodId());
        } catch (StripeException e) {
            throw new AppException(ErrorCode.RETRIEVE_PAYMENT_METHOD_FAILED);
        }

        attachPaymentMethodToCustomer(user, paymentMethod);

        PaymentMethodEntity paymentMethodEntity = PaymentMethodEntity.builder()
                .userId(userId)
                .id(paymentMethod.getId())
                .holderName(paymentMethod.getBillingDetails().getName())
                .cardBrand(CardBrand.VISA)
                .last4(paymentMethod.getCard().getLast4())
                .exp_month(paymentMethod.getCard().getExpMonth())
                .exp_year(paymentMethod.getCard().getExpYear())
                .build();
        paymentMethodRepository.save(paymentMethodEntity);
        return paymentMethodMapper.toPaymentMethodResponse(paymentMethodEntity);
    }

    @Transactional
    public void attachPaymentMethodToCustomer(UserEntity user, PaymentMethod paymentMethod) {
        PaymentMethod resource;
        try {
            resource = PaymentMethod.retrieve(paymentMethod.getId());
        } catch (StripeException e) {
            throw new AppException(ErrorCode.RETRIEVE_PAYMENT_METHOD_FAILED);
        }

        PaymentMethodAttachParams params = PaymentMethodAttachParams.builder()
                .setCustomer(user.getCustomerId())
                .build();
        try {
            resource.attach(params);
        } catch (StripeException e) {
            throw new AppException(ErrorCode.ATTACH_PAYMENT_METHOD_FAILED);
        }

    }

    @Transactional
    public void detachPaymentMethodToCustomer(PaymentMethod paymentMethod) {
        PaymentMethod resource;
        try {
            resource = PaymentMethod.retrieve(paymentMethod.getId());
        } catch (StripeException e) {
            throw new AppException(ErrorCode.RETRIEVE_PAYMENT_METHOD_FAILED);
        }

        PaymentMethodDetachParams params = PaymentMethodDetachParams.builder().build();

        try {
            resource.detach(params);
        } catch (StripeException e) {
            throw new AppException(ErrorCode.DETACH_PAYMENT_METHOD_FAILED);
        }
    }

    // chỉ update holder, không được sửa thông tin như card number, cvc, khi cần sửa
    // thông tin này cần tạo thẻ mới để stripe verify
    @Transactional
    public PaymentMethodResponse updatePaymentMethodBillingDetails(
            String accessToken,
            UpdatePaymentMethodRequest  updatePaymentMethodRequest) {

        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        PaymentMethodEntity paymentMethodEntity = paymentMethodRepository.findById(updatePaymentMethodRequest.getPaymentMethodId())
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_METHOD_NOT_EXISTED));

        PaymentMethod resource;
        try {
            resource = PaymentMethod.retrieve(updatePaymentMethodRequest.getPaymentMethodId());
        } catch (StripeException e) {
            throw new AppException(ErrorCode.RETRIEVE_PAYMENT_METHOD_FAILED);
        }

        PaymentMethodUpdateParams params = PaymentMethodUpdateParams.builder()
                .setBillingDetails(
                        PaymentMethodUpdateParams.BillingDetails.builder()
                                .setName(updatePaymentMethodRequest.getHolderName())
                                .build()
                ).build();

        try {
            resource.update(params);
        }  catch (StripeException e) {
            throw new AppException(ErrorCode.UPDATE_PAYMENT_METHOD_FAILED);
        }

        paymentMethodEntity.setHolderName(updatePaymentMethodRequest.getHolderName());
        paymentMethodRepository.save(paymentMethodEntity);
        return paymentMethodMapper.toPaymentMethodResponse(paymentMethodEntity);
    }

    public PaymentMethodResponse getPaymentMethodDetailById (String accessToken, String pmID) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        PaymentMethodEntity paymentMethodEntity = paymentMethodRepository.findById(pmID)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_METHOD_NOT_EXISTED));
        return paymentMethodMapper.toPaymentMethodResponse(paymentMethodEntity);
    }

    @Transactional(readOnly = true)
    public List<PaymentMethodResponse> findAllPaymentMethods(String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        List<PaymentMethodEntity> paymentMethodList = paymentMethodRepository.findAllByUserId(userId);
        return paymentMethodList.stream()
                .map(paymentMethodMapper::toPaymentMethodResponse)
                .collect(Collectors.toList());
    }

    @Transactional()
    public PaymentMethodResponse deletePaymentMethod(String accessToken, String paymentMethodId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        PaymentMethodEntity paymentMethodEntity = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_METHOD_NOT_EXISTED));

        PaymentMethod paymentMethod;
        try {
            paymentMethod = PaymentMethod.retrieve(paymentMethodEntity.getId());
        } catch (StripeException e) {
            throw new AppException(ErrorCode.RETRIEVE_PAYMENT_METHOD_FAILED);
        }

        detachPaymentMethodToCustomer(paymentMethod);

        paymentMethodRepository.delete(paymentMethodEntity);
        return paymentMethodMapper.toPaymentMethodResponse(paymentMethodEntity);
    }
}
