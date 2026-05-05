package com.example.food.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    INVALID_USERNAME(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.UNAUTHORIZED),
    EMAIL_EXISTED(1008, "Email existed, please choose another one", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1009, "Username existed, please choose another one", HttpStatus.BAD_REQUEST),
    USERNAME_IS_MISSING(1010, "Please enter username", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1011, "User not existed", HttpStatus.BAD_REQUEST),
    INCORRECT_PASSWORD(1012, "Incorrect password", HttpStatus.BAD_REQUEST),
    ACCESS_TOKEN_EXPIRED(1013, "Access token expired", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED(1014, "Refresh token expired", HttpStatus.UNAUTHORIZED),
    NOT_FOUND_ORDER(1015, "Order not found", HttpStatus.NOT_FOUND),
    FOOD_NOT_IN_ORDER(1016, "Food not in order", HttpStatus.NOT_FOUND),
    FOOD_NOT_FOUND(1017, "Food not found", HttpStatus.NOT_FOUND),
    ADDRESS_NOT_FOUND(1018, "Address not found", HttpStatus.BAD_REQUEST),
    ORDER_NOT_COMPLETED(1019, "Order not completed", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(1020, "Order not found", HttpStatus.BAD_REQUEST),
    CANCEL_ORDER_FAILED(1021, "Orders can only be canceled by the person placing the order", HttpStatus.BAD_REQUEST),
    ORDER_CANT_BE_CANCELED(1022, "Only orders in active status can be canceled", HttpStatus.BAD_REQUEST),
    PAYMENT_METHOD_NOT_EXISTED(1023, "Payment method not existed", HttpStatus.BAD_REQUEST),
    CREATE_CUSTOMER_FAILED(1024, "Create customer failed", HttpStatus.BAD_REQUEST),
    CREATE_PAYMENT_METHOD_FAILED(1025, "Create payment method failed", HttpStatus.BAD_REQUEST),
    UPDATE_PAYMENT_METHOD_FAILED(1026, "Update payment method failed", HttpStatus.BAD_REQUEST),
    DELETE_PAYMENT_METHOD_FAILED(1027, "Delete payment method failed", HttpStatus.BAD_REQUEST),
    RETRIEVE_PAYMENT_METHOD_FAILED(1028, "Retrieve payment method failed", HttpStatus.BAD_REQUEST),
    ATTACH_PAYMENT_METHOD_FAILED(1029, "Attach payment method failed", HttpStatus.BAD_REQUEST),
    DETACH_PAYMENT_METHOD_FAILED(1030, "Detach payment method failed", HttpStatus.BAD_REQUEST),
    ORDER_CANT_BE_PAID(1031, "Only the order owner can pay", HttpStatus.BAD_REQUEST),
    CREATE_PAYMENT_INTENT_FAILED(1032, "Create payment intent failed", HttpStatus.BAD_REQUEST),
    INVALID_WEBHOOK_SIGNATURE(1033, "Invalid webhook signature", HttpStatus.BAD_REQUEST),
    HANDLE_WEBHOOK_FAILED(1034, "Handle webhook failed", HttpStatus.BAD_REQUEST),
    ORDER_HAS_BEEN_PAID(1035, "Order has been paid", HttpStatus.BAD_REQUEST),
    UPDATE_ORDER_PAYMENT_STATUS_FAILED(1036, "Update order payment status failed", HttpStatus.BAD_REQUEST),
    PASSWORD_CONFIRM_NOT_MATCH(1037, "Password and confirm password do not match",  HttpStatus.BAD_REQUEST),
    EMAIL_FAIL(1038, "Send email fail", HttpStatus.BAD_REQUEST),
    OTP_NOT_MATCH(1039, "OTP not match", HttpStatus.BAD_REQUEST),
    UPLOAD_IMAGE_FAILED(1040, "Upload image failed", HttpStatus.BAD_REQUEST),
    COMMENT_NOT_FOUND(1041, "Comment not found", HttpStatus.NOT_FOUND),
    INGREDIENT_FOOD_NOT_FOUND(1042, "Ingredient not found", HttpStatus.NOT_FOUND),
    INGREDIENT_NOT_FOUND(1043, "Ingredient not found", HttpStatus.NOT_FOUND),
    NOT_PERMISSION_UPDATE_CART(1045, "Not permission update cart", HttpStatus.BAD_REQUEST),
    CART_ITEM_NOT_FOUND(1046, "Cart item not found", HttpStatus.NOT_FOUND),
    QUANTITY_NOT_ENOUGH(1047, "Quantity not enough", HttpStatus.BAD_REQUEST),
    FOOD_ALREADY_LIKED(1048, "Food already liked", HttpStatus.BAD_REQUEST),
    FOOD_NOT_LIKE(1049, "Food not liked", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final HttpStatusCode statusCode;
    private final String message;
}
