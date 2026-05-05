# Management-food-store

## 1) Tổng quan kỹ thuật

### Mục tiêu nghiệp vụ
- Quản lý người dùng và xác thực JWT.
- Duyệt danh mục món ăn, tìm kiếm/lọc, like/unlike.
- Quản lý giỏ hàng + topping.
- Đặt đơn, hủy đơn, theo dõi trạng thái đơn.
- Thanh toán qua Stripe (Payment Intent + Webhook).
- Quản lý phương thức thanh toán.
- Upload ảnh qua Cloudinary.
- OTP quên mật khẩu qua email + Redis TTL.

### Stack chính
- Java 21
- Spring Boot 3.2.5
- Spring Web, Spring Data JPA, Spring Security
- PostgreSQL
- Redis (cache OTP)
- Stripe Java SDK
- Cloudinary SDK
- Spring Mail
- MapStruct + Lombok
- OpenAPI/Swagger (springdoc)

## 2) Kiến trúc dự án

### Kiến trúc phân lớp
- `controller`: nhận request/response HTTP.
- `service`: chứa business logic.
- `repository`: truy vấn DB với Spring Data JPA.
- `entity`: ánh xạ bảng dữ liệu.
- `dto`: request/response contract cho API.
- `security`: JWT filter + Spring Security config.
- `configuration/config`: cấu hình Stripe, Cloudinary, OpenAPI, Redis, dotenv.

### Cấu trúc thư mục quan trọng
```text
src/main/java/com/example/food
├─ common/                 # enum trạng thái, currency, card brand
├─ config/                 # Dotenv, RedisTemplate
├─ configuration/          # Stripe, Cloudinary, OpenAPI
├─ controller/             # REST API endpoints
├─ dto/                    # request/response wrappers
├─ entity/                 # JPA entities
├─ exception/              # error code + global handler
├─ mapper/                 # MapStruct mappers
├─ repository/             # JPA repositories + custom queries
├─ security/               # JWT filter + Security chain
├─ service/                # business services
│  ├─ async/               # OTP async event/listener
│  ├─ cloudinary/          # upload image service
│  └─ redis/               # OTP presence service
└─ FoodApplication.java
```

## 3) Domain model (bảng dữ liệu)

Hệ thống hiện dùng các bảng chính sau (tạo/cập nhật bằng `hibernate.ddl-auto=update`):

- `tbl_user`: người dùng, role, avatar, `customerId` Stripe.
- `tbl_address`: địa chỉ giao hàng theo user.
- `tbl_food`: món ăn (giá, tồn kho, rating, likes, số lượng đã bán).
- `tbl_category`, `tbl_category_detail`: danh mục và danh mục con.
- `tbl_food_category_detail`: mapping food <-> category detail.
- `tbl_ingredient`: topping/ingredient.
- `tbl_ingredient_food`: mapping food <-> ingredient.
- `tbl_cart_item`: item trong giỏ hàng.
- `tbl_cart_item_ingredient`: topping theo từng cart item.
- `tbl_order`: đơn hàng (địa chỉ snapshot, tổng tiền, trạng thái đơn/thanh toán).
- `tbl_order_detail`: item của đơn hàng.
- `tbl_order_detail_ingredient`: topping theo từng order detail.
- `tbl_payment_method`: phương thức thanh toán của user.
- `tbl_comment`: đánh giá món ăn.
- `tbl_user_food_like`: món yêu thích của user.

## 4) Xác thực và phân quyền

### JWT
- Access/Refresh token được phát trong `AuthService`.
- `JwtAuthenticationFilter` đọc `Authorization: Bearer <token>` và set SecurityContext.
- Role được map thành `ROLE_<role>` để dùng `@PreAuthorize`.

### Endpoint public trong SecurityConfig
- `POST /api/v1/auth/log-in`
- `POST /api/v1/auth/sign-up`
- `POST /api/v1/auth/biometric`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/OTP/**`
- `POST /api/v1/auth/forgot-password`
- `/api/v1/webhooks/**`
- `/api/v1/orders/**` (đang để public ở config)
- `/swagger-ui/**`, `/v3/api-docs/**`

Các endpoint còn lại yêu cầu JWT.

## 5) API response convention

Hầu hết API trả về format chung:

```json
{
  "code": 200,
  "message": "...",
  "result": {}
}
```

- Lỗi nghiệp vụ được map từ `ErrorCode` trong `GlobalExceptionHandler`.
- Ví dụ: `USER_NOT_EXISTED`, `ORDER_NOT_FOUND`, `OTP_NOT_MATCH`, `UPLOAD_IMAGE_FAILED`...

## 6) Danh sách API theo module

## Auth (`/api/v1/auth`)
- `POST /log-in`
- `POST /sign-up`
- `POST /biometric`
- `POST /refresh`
- `POST /change-password`
- `POST /OTP/{email}`
- `POST /forgot-password`

## User (`/api/v1/users`)
- `GET /{id}`
- `POST /{id}` (multipart/form-data, update profile)
- `DELETE /{id}`

## Food (`/api/v1/foods`)
- `POST /filter` (nhận filter qua request body)
- `GET /{name}`
- `GET /likes`
- `POST /{foodId}/likes`
- `DELETE /{userFoodLikeId}/unlikes`
- `GET /details/{foodId}`
- `GET /category/{categoryId}`
- `GET /best-seller`
- `GET /recommend`
- `DELETE /{foodId}/likes/remove`

## Category
- `GET /api/v1/category`
- `GET /api/v1/category/details/{categoryId}`

## Cart (`/api/v1/cart-items`)
- `GET /`
- `POST /add-item`
- `POST /update-cart-item`
- `DELETE /delete-cart-item/{cartId}`

## Address (`/api/v1/addresses`)
- `POST /`
- `PUT /{addressId}`
- `GET /{addressId}`
- `GET /user`
- `DELETE /{addressId}`

## Comment (`/api/v1/comments`)
- `POST /`
- `GET /{foodId}`
- `PUT /{commentId}`
- `DELETE /{commentId}`

## Order (`/api/v1/orders`)
- `POST /create-order`
- `POST /cancel-order`
- `GET /get-orders?status=...`
- `POST /update-order-status`
- `GET /{orderId}`

## Payment
- `POST /api/v1/payments/create-intent`
- `GET|POST|DELETE /api/v1/payment-method/...`
- `POST /api/v1/webhooks/stripe`

## Image
- `POST /api/v1/images/upload` (multipart file)

Swagger UI: `http://localhost:8888/swagger-ui/index.html`

