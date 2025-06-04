# Báo cáo đề tài: Hệ thống quản lý người dùng có phân quyền"

## Mục tiêu đề tài

Xây dựng hệ thống quản lý người dùng cho phép:

- Đăng ký, đăng nhập, đăng xuất tài khoản

- Gán quyền (permission) và vai trò (role) cho người dùng.

- Phân quyền người cho người dùng truy cập vào các API phù hợp.

- Hiển thị thông tin cá nhân sau khi đăng nhập.

- Khoá tài khoản người nếu nhập sai mật khẩu quá 5 lần.

- Cho phép người dùng xem lại lịch sử đăng nhập và đăng xuất khỏi các
  thiết bị.

## Công nghệ sử dụng

Bảng 1: Công nghệ sử dụng trong dự án

| Công nghệ / Thư viện                 | Vai trò                                                           |
|--------------------------------------|-------------------------------------------------------------------|
| Spring Boot                          | Framework chính để xây dựng ứng dụng backend.                     |
| Spring Web (spring-boot-starter-web) | Xây dựng RESTful API.                                             |
| Spring Security                      | Xác thực và phân quyền người dùng.                                |
| JWT *(tích hợp qua Spring Security)* | Xác thực không trạng thái thông qua token.                        |
| Spring Data JPA                      | ORM giúp thao tác với database thông qua entity.                  |
| Hibernate                            | ORM (Object-Relational Mapping) framework hỗ trợ JPA              |
| Spring Validation                    | Kiểm tra tính hợp lệ của dữ liệu đầu vào từ client.               |
| Oracle JDBC Driver                   | Kết nối với cơ sở dữ liệu Oracle.                                 |
| Spring OAuth2 Resource Server        | Hỗ trợ xác thực tài nguyên với token (OAuth2).                    |
| MapStruct                            | Tự động ánh xạ giữa DTO và entity, giảm code boilerplate.         |
| Lombok                               | Tạo tự động getter/setter, constructor, builder, v.v.             |
| Maven                                | Quản lý phụ thuộc và build project.                               |
| Spring Boot Devtools                 | Tự động reload khi code thay đổi, hỗ trợ phát triển nhanh hơn.    |
| Các công cụ phát triển               | IDE (IntelliJ), Postman để kiểm thử API, Git để quản lý mã nguồn. |

## Cơ sở lý thuyết

### Spring Boot Framework

- **Lý thuyết**: Spring Boot là một framework giúp xây dựng ứng dụng
  Java dễ dàng, cung cấp auto-configuration, cấu trúc rõ ràng và tích
  hợp sẵn nhiều công nghệ như JPA, Security, Web, Validation,\...

- **Ứng dụng trong dự án**: Là nền tảng chính của toàn bộ hệ thống, quản
  lý các lớp controller, service, repository, cấu hình bảo mật\...

- **Tham khảo**: [Spring Boot :: Spring
  Boot](https://docs.spring.io/spring-boot/)

###  RESTful API {#restful-api}

- **Lý thuyết**: REST (Representational State Transfer) là phong cách
  kiến trúc cho phép các hệ thống giao tiếp với nhau qua HTTP với các
  phương thức như GET, POST, PUT, DELETE.

- **Ứng dụng trong dự án**: Giao tiếp giữa client và server thông qua
  các endpoint như /api/users, /api/auth/login,\...

- **Tham khảo**: [What is REST?: REST API
  Tutorial](https://restfulapi.net/)

### Spring Data JPA

- **Lý thuyết**: Là phần mở rộng của JPA giúp truy vấn cơ sở dữ liệu đơn
  giản hơn bằng cách định nghĩa interface thay vì viết SQL.

- **Ứng dụng trong dự án**: Repository interface như UserRepository,
  RoleRepository,\... với các phương thức truy vấn tự động.

- **Tham khảo**: [Spring Data JPA :: Spring Data
  JPA](https://docs.spring.io/spring-data/jpa/reference/index.html)

## JWT (JSON Web Token)

- **Lý thuyết**: JWT là chuẩn mã hóa thông tin giữa client và server,
  cho phép xác thực không trạng thái (stateless).

- **Ứng dụng trong dự án**: Dùng để xác thực và phân quyền người dùng
  khi đăng nhập, đi kèm các token blacklist để quản lý đăng xuất.

- **Tham khảo**: [JSON Web Token Introduction -
  jwt.io](https://jwt.io/introduction)

### Spring Security

- **Lý thuyết**: Framework bảo mật mạnh mẽ của Spring, cung cấp xác
  thực, phân quyền, mã hóa mật khẩu,\...

- **Ứng dụng trong dự án**: Cấu hình xác thực bằng JWT, phân quyền theo
  vai trò (User, Admin), kiểm soát truy cập endpoint.

- **Tham khảo**: [Spring Security :: Spring
  Security](https://docs.spring.io/spring-security/reference/index.html)

### Validation (Bean Validation - Jakarta)

- **Lý thuyết**: Dựa trên các annotation như @NotBlank, @Email,
  @Size,\... để kiểm tra dữ liệu đầu vào.

- **Ứng dụng trong dự án**: Áp dụng trong các lớp DTO để kiểm tra dữ
  liệu người dùng gửi lên và tạo những custom validation.

- **Tham khảo**: [Jakarta Bean Validation
  specification](https://beanvalidation.org/2.0/spec/)

### DTO & Mapper (MapStruct) {#dto-mapper-mapstruct}

- **Lý thuyết**: DTO (Data Transfer Object) dùng để vận chuyển dữ liệu
  giữa các tầng. MapStruct là thư viện để ánh xạ tự động giữa Entity và
  DTO.

- **Ứng dụng trong dự án**: Các lớp UserDTO, UserMapper,\... giúp tách
  biệt logic entity và dữ liệu truyền ra ngoài.

- **Tham khảo**: [MapStruct 1.6.3 Reference
  Guide](https://mapstruct.org/documentation/stable/reference/html/)

### Auditing (Spring Data Auditing)

- **Lý thuyết**: Cho phép tự động ghi nhận người tạo, người sửa, thời
  điểm tạo và sửa cho entity.

- **Ứng dụng trong dự án**: Qua @EnableJpaAuditing, @CreatedBy,
  @CreatedDate,\...

- **Tham khảo**: [Auditing :: Spring Data
  JPA](https://docs.spring.io/spring-data/jpa/reference/auditing.html)

### Exception Handling (Global)

- **Lý thuyết**: Xử lý lỗi tập trung thông qua @ControllerAdvice và
  @ExceptionHandler, giúp API nhất quán và dễ debug.

- **Ứng dụng trong dự án**: GlobalExceptionHandler xử lý các lỗi
  validation, token không hợp lệ, entity không tồn tại,\...

- Tham khảo: [Error Handling for REST with Spring \|
  Baeldung](https://www.baeldung.com/exception-handling-for-rest-with-spring)

### Specification (Dynamic Filtering)

- **Lý thuyết**: Specification trong JPA giúp xây dựng truy vấn linh
  hoạt theo điều kiện động.

- **Ứng dụng trong dự án**: UserSpecification giúp lọc user theo tên,
  email, vai trò,\...

- **Tham khảo**: [Specifications :: Spring Data
  JPA](https://docs.spring.io/spring-data/jpa/reference/jpa/specifications.html)

## Kiến thức lập trình áp dụng

### REST API

sử dụng các annotation của Spring Boot như @RestController, @GetMapping,
@PostMapping để xử lý các yêu cầu HTTP. Dữ liệu trao đổi giữa client và
server ở định dạng JSON.

### JWT (JSON Web Token)

Sử dụng JWT để thực hiện xác thực không trạng thái. Khi người dùng đăng
nhập thành công, hệ thống cấp một JWT (chứa thông tin người dùng và vai
trò) để client lưu trữ. Các yêu cầu tiếp theo của client sẽ kèm token
này trong header, cho phép server xác minh danh tính mà không cần lưu
session.

### Spring Security và Middleware

Spring Security được sử dụng để bảo vệ các API. Một loạt các filter (ví
dụ filter xác thực JWT) hoạt động như middleware ngăn chặn các request
không hợp lệ trước khi vào Controller. Cấu hình bảo mật (SecurityConfig)
quy định rule phân quyền theo vai trò, đảm bảo chỉ những người dùng có
quyền mới được truy cập tài nguyên tương ứng.

### Validation

Dữ liệu đầu vào từ client được kiểm tra tính hợp lệ với các annotation
của Spring Validation (như @Valid, @NotBlank, @Email, v.v.). Điều này
giúp ngăn ngừa dữ liệu sai định dạng hoặc thiếu trường bắt buộc.

### ORM - Spring Data JPA và Hibernate

Các thực thể (entity) Java được ánh xạ tới bảng trong cơ sở dữ liệu
thông qua JPA. Sử dụng Spring Data JPA (extends JpaRepository) để thực
hiện các phép CRUD tự động. Hibernate làm lớp triển khai để tương tác cơ
sở dữ liệu, giúp giảm bớt mã SQL thuần.

### MapStruct và Lombok

Dự án sử dụng MapStruct để tự động chuyển đổi giữa đối tượng thực thể và
DTO, giảm thiểu code viết tay khi ánh xạ dữ liệu. Lombok được dùng để tự
động sinh getters/setters, constructor, builder, giúp giảm boilerplate
cho các class.

### Khác

Ngoài ra, một số kiến thức bổ trợ khác như xử lý ngoại lệ (ví dụ dùng
@ControllerAdvice xử lý lỗi toàn cục) và logging cũng được áp dụng để
hoàn thiện chức năng và thuận tiện phát triển.

# Thiết kế hệ thống

Luồng xử lý dữ liệu cơ bản như sau: client gửi yêu cầu HTTP -\> **Filter
bảo mật** (kiểm tra token, CORS, v.v.) -\> **Controller** nhận request
-\> **Service** thực hiện logic nghiệp vụ -\> **Repository** tương tác
với cơ sở dữ liệu -\> kết quả được trả ngược lại client.

### Controller Layer

> Bao gồm các controller xử lý các endpoind. Ví dụ, UserController quản
> lý thao tác CRUD người dùng (tạo tài khoản, lấy danh sách người dùng,
> cập nhật hoặc xóa người dùng), RoleController trả về danh sách vai trò
> hiện có, ProfileController quản lý thông tin cá nhân của người dùng
> (xem profile, đổi mật khẩu, xóa tài khoản), và
> AuthenticationController xử lý đăng ký, đăng nhập, đăng xuất và cấp
> mới JWT. Các controller sử dụng annotation của Spring như
> @RestController và các phương thức @GetMapping, @PostMapping\... để
> định nghĩa API.

### Service Layer

> Chứa các lớp Service thực thi nghiệp vụ cụ thể. Ví dụ, UserService
> thực hiện các thao tác liên quan đến quản lý người dùng (gọi
> repository, xử lý mật khẩu, gán role), RoleService quản lý nghiệp vụ
> liên quan đến vai trò, ProfileService xử lý chức năng liên quan đến
> thông tin cá nhân, và AuthService thực hiện xác thực (kiểm tra thông
> tin đăng nhập, tạo JWT, xử lý refresh token). Service có nhiệm vụ gọi
> đến các phương thức của Repository để truy vấn hoặc cập nhật dữ liệu
> và áp dụng các quy tắc nghiệp vụ. Các phương thức trong lớp service có
> thể được bảo vệ bởi annotation @PreAuthorize để kiểm soát người dùng
> có quyền thao tác hay không.

### Repository Layer

> Gồm các interface kế thừa từ JpaRepository. Ví dụ, UserRepository,
> RoleRepository, PermissionRepository, TokenBlacklistRepository cho
> phép thao tác với các thực thể tương ứng. Các repository này cung cấp
> sẵn các phương thức CRUD (save, findById, findAll, delete, v.v.), giúp
> xử lý dữ liệu nhanh chóng mà không phải viết SQL thủ công. Dữ liệu
> được lưu trữ trong cơ sở dữ liệu quan hệ (trong dự án này là Oracle)
> thông qua JDBC driver của Oracle.

## Bảo mật và xác thực

### Xác thực JWT

Khi đăng nhập, hệ thống cấp JWT chứa thông tin người dùng và vai trò.
JWT được ký bằng khóa bí mật và gửi về client. Mỗi request cần truy cập
API bảo vệ phải kèm token này trong header Authorization. Hệ thống có
một JwtAuthenticationEntryPoint để xử lý lỗi khi token không hợp lệ.

### Lọc và giải mã token

Một lớp CustomJwtDecoder (hoặc filter) được dùng để giải mã và kiểm tra
tính hợp lệ của JWT trên mỗi request. Nếu token đã hết hạn hoặc bị sửa
đổi, request sẽ bị chặn và trả về lỗi 401.

### Token Blacklist (Danh sách thu hồi)

Dữ liệu các JWT bị thu hồi (ví dụ khi người dùng đăng xuất) được lưu
trong TokenBlacklist. Nếu một token nằm trong danh sách đen, hệ thống sẽ
từ chối cho phép sử dụng lại token đó, ngăn hành vi đánh cắp token cũ.

### Mã hóa mật khẩu

Mật khẩu người dùng được lưu trong cơ sở dữ liệu ở dạng mã hóa (sử dụng
BCryptPasswordEncoder). Khi đăng nhập, mật khẩu nhập vào được mã hóa và
so sánh với bản mã lưu trữ, đảm bảo bảo mật nếu cơ sở dữ liệu bị lộ.

### Phân quyền dựa trên vai trò

Spring Security cấu hình phân quyền chi tiết, chỉ cho phép người dùng có
vai trò (ROLE_ADMIN, ROLE_USER, v.v.) và quyền hạn tương ứng truy cập
các endpoint nhất định. Kết hợp annotation như @PreAuthorize hoặc cấu
hình httpSecurity để đảm bảo chỉ các vai trò được phép mới được phép
thực thi một số chức năng (ví dụ chỉ Admin mới có thể xóa người dùng
khác).

### Kiểm tra dữ liệu đầu vào

Validation được sử dụng để ngăn ngừa các cuộc tấn công như SQL Injection
hay XSS từ dữ liệu đầu vào. Ngoài ra có thể cấu hình CORS nếu frontend
chạy trên miền khác, hạn chế truy cập từ các nguồn không tin cậy.

## Chi tiết thiết kế

### Mô hình thực thể

Bảng 2: Thiết kế BaseEntity

| Trường    | Kiểu dữ liệu  | Ràng buộc             | Mô tả                          |
|-----------|---------------|-----------------------|--------------------------------|
| id        | String (UUID) | Not null, Primary key | Định danh duy nhất của bản ghi |
| createdAt | LocalDateTime | Auto-generated        | Thời điểm tạo bản ghi          |
| createdBy | String        | Auto-generated        | Người tạo bản ghi              |
| updatedAt | LocalDateTime | Auto-generated        | Thời điểm cập nhật bản ghi     |
| updatedBy | String        | Auto-generated        | Người cập nhật bản ghi         |
| isDeleted | Boolean       | Default = false       | Đánh dấu bản ghi đã bị xóa mềm |
| deletedAt | LocalDateTime |                       | Thời điểm xóa bản ghi (nếu có) |
| deletedBy | String        |                       | Người xóa bản ghi (nếu có)     |

Bảng 3: Thiết kế User

| Trường             | Kiểu dữ liệu      | Ràng buộc        | Mô tả                           |
|--------------------|-------------------|------------------|---------------------------------|
| username           | String (50)       | Not null, Unique | Tên đăng nhập                   |
| password           | String (100)      | Not null         | Mật khẩu đã mã hóa              |
| firstName          | String (50)       | Not null         | Tên người dùng                  |
| lastName           | String (50)       | Not null         | Họ người dùng                   |
| email              | String (100)      | Not null, Unique | Email                           |
| phone              | String (20)       | Not null, Unique | Số điện thoại                   |
| gender             | Boolean           | Not null         | Giới tính (true: Nam)           |
| birthday           | LocalDate         | Not null         | Ngày sinh                       |
| isLocked           | Boolean           |                  | Trạng thái khóa (true: Bị khóa) |
| LockedAt           | LocalDateTime     |                  | Thời gian bị khóa               |
| address            | String            | Not null         | Địa chỉ                         |
| roles              | Set\<Role\>       | ManyToMany       | Danh sách vai trò               |
| revokedPermissions | Set\<Permission\> | ManyToMany       | Danh sách quyền bị thu hồi      |

Bảng 4: Thiết kế Role

| Trường      | Kiểu dữ liệu      | Ràng buộc                   | Mô tả                        |
|-------------|-------------------|-----------------------------|------------------------------|
| name        | UserRole          | Not null, Primary key, Enum | Tên vai trò                  |
| description | String            |                             | Mô tả vai trò                |
| permissions | Set\<Permission\> | ManyToMany                  | Danh sách quyền ứng với role |

Bảng 5: Thiết kế Permission

| Trường      | Kiểu dữ liệu   | Ràng buộc                   | Mô tả       |
|-------------|----------------|-----------------------------|-------------|
| name        | UserPermission | Not null, Primary key, Enum | Tên quyền   |
| description | String         |                             | Mô tả quyền |

Bảng 6: Thiết kế RefreshToken

| Trường    | Kiểu dữ liệu  | Ràng buộc             | Mô tả                            |
|-----------|---------------|-----------------------|----------------------------------|
| id        | String (UUID) | Not null, Primary key | Mã định danh của refresh token   |
| token     | String        | Not null, unique      | Refresh token                    |
| user      | User          | Not null, ManyToOne   | Tham chiếu đến người dùng        |
| loginLog  | LoginLog      | Not null, OneToOne    | Tham chiếu đến lịch sử đăng nhập |
| createdAt | LocalDateTime | Not null              | Ngày tạo                         |
| expiredAt | LocalDateTime | Not null              | Ngày hết hạn                     |
| revoked   | Boolean       | Not null              | Trạng thái (true: vô hiệu hóa)   |

Bảng 7: Thiết kế TokenBlacklist

| Trường    | Kiểu dữ liệu  | Ràng buộc             | Mô tả             |
|-----------|---------------|-----------------------|-------------------|
| tokenId   | String        | Not null, Primary key | Id token          |
| expiredAt | LocalDateTime | Not null              | Thời gian hết hạn |

Bảng 8: Thiết kế LoginLog

| Trường         | Kiểu dữ liệu  | Ràng buộc             | Mô tả                           |
|----------------|---------------|-----------------------|---------------------------------|
| id             | String (UUID) | Not null, Primary key | Định danh duy nhất của bản ghi  |
| user           | User          | Not null, ManyToOne   | Tham chiếu đến người dùng       |
| RefreshToken   | RefreshToken  | OneToOne              | Tham chiếu đến refresh token    |
| jti            | String        |                       | Id access token                 |
| logout         | Boolean       |                       | Trạng thái đã đăng xuất         |
| createdAt      | LocalDateTime |                       | Ngày tạo                        |
| success        | Boolean       | Not null              | Trạng thái đăng nhập thành công |
| userAgent      | String        | Not null              | Chuỗi tác nhân người dùng       |
| ipAddress      | Boolean       | Not null              | Địa chỉ ip người dùng           |
| device         | String        | Not null              | Loại thiết bị                   |
| browser        | String        | Not null              | Trình duyệt                     |
| browserVersion | String        | Not null              | Phiên bản trình duyệt           |
| Os             | String        | Not null              | Hệ điều hành                    |
| OsVersion      | String        | Not null              | Phiên bản hệ điều hành          |

### Thiết kế API

| Phương thức | Endpoint                       | Vai trò/Quyền được phép | Vai trò/Quyền bị cấm | Mô tả                                                            |
|-------------|--------------------------------|-------------------------|----------------------|------------------------------------------------------------------|
| POST        | /auth/register                 |                         |                      | Đăng ký tài khoản mới                                            |
| POST        | /auth/login                    |                         |                      | Đăng nhập, nhận token để xác thực cho private endpoint           |
| POST        | /auth/refresh-token            |                         |                      | Lấy access token mới bằng refresh token                          |
| POST        | /auth/logout                   |                         |                      | Đăng xuất, vô hiệu hóa token                                     |
| GET         | /profile                       | AUTHENTICATE            |                      | Lấy thông tin cá nhân                                            |
| PUT         | /profile                       | AUTHENTICATE            |                      | Cập nhật thông tin tài khoản                                     |
| PUT         | /profile/password              | AUTHENTICATE            |                      | Đổi mật khẩu                                                     |
| DELETE      | /profile                       | AUTHENTICATE            | SUPER_ADMIN          | Xóa tài khoản hiện tại                                           |
| GET         | /profile/login-history         | AUTHENTICATE            |                      | Lấy danh sách lịch sử đăng nhập của cá nhân                      |
| POST        | /profile/login-history         | AUTHENTICATE            |                      | Lấy danh sách lịch sử đăng nhập của cá nhân + lọc theo điều kiện |
| POST        | /profile/ /revoke/{loginLogId} | AUTHENTICATE            |                      | Đăng xuất một phiên làm việc                                     |
| GET         | /roles                         | ADMIN, SUPER_ADMIN      |                      | Lấy danh sách quyền trong hệ thống                               |
| GET         | /users                         | VIEW_USER               |                      | Lấy danh sách người dùng trong hệ thống                          |
| POST        | /users                         | VIEW_USER               |                      | Lấy danh sách người dùng trong hệ thống  + lọc theo điều kiện    |
| GET         | /users/login-history           | VIEW_USER               |                      | Lấy danh sách lịch sử đăng nhập người dùng                       |
| POST        | /users/login-history           | VIEW_USER               |                      | Lấy danh sách lịch sử đăng nhập người dùng + lọc theo điều kiện  |
| GET         | /users/{id}                    | VIEW_USER               |                      | Xem thông tin người dùng theo id                                 |
| POST        | /users/create                  | ADD_USER                |                      | Tạo một người dùng mới                                           |
| PUT         | /users/{id}                    | EDIT_USER               |                      | Sửa thông tin người dùng theo id                                 |
| DELETE      | /users/{id}                    | DELETE_USER             |                      | Xóa một người dùng theo id                                       |
| POST        | /users/restore/{id}            | SUPER_ADMIN             |                      | Khôi phục người dùng bị xóa                                      |

### Kết quả
![image](https://github.com/user-attachments/assets/77079edf-031f-4dff-a634-c557a56b6c73)
Hình 1: Trang đăng nhập

 ![image](https://github.com/user-attachments/assets/73249f29-bcd4-480d-9a60-01710fbe831f)
Hình 2: Trang đăng ký

 ![image](https://github.com/user-attachments/assets/c3012160-a054-4b3e-9c8c-e664eadbecf2)
Hình 3: Trang thông tin cá nhân

 ![image](https://github.com/user-attachments/assets/95cbf092-2fc8-4b97-a828-957a73c39ce2)
Hình 4: Cập nhật thông tin cá nhân

 ![image](https://github.com/user-attachments/assets/5d4d4032-8e4b-41c3-a86d-70090ece86ee)
Hình 5: Cập nhật mật khẩu

 ![image](https://github.com/user-attachments/assets/46e3be7a-8ed3-41b2-bb08-c434d965a997)
Hình 6: Xem Danh sách người dùng

 ![image](https://github.com/user-attachments/assets/2bb5a405-18d6-4e06-9173-4f1bfa0d4338)
Hình 7: Lọc và phân Trang danh sách người dùng

 ![image](https://github.com/user-attachments/assets/41c9355f-c96e-4590-ba4c-f09ffe9906da)
Hình 8: Chỉnh sửa thông tin người dùng

 ![image](https://github.com/user-attachments/assets/0df50970-9005-470b-96ec-b9040ad841f0)
Hình 9: Chi tiết thông tin người dùng

 ![image](https://github.com/user-attachments/assets/b2b8cc59-67d9-4ae8-bec2-5a76ca7addb1)
Hình 10: Lịch sử đăng nhập của người dùng

 ![image](https://github.com/user-attachments/assets/2b9844b7-222c-489b-83fb-5448a9419a26)
Hình 11: Lịch sử đăng nhập cá nhân

 ![image](https://github.com/user-attachments/assets/484a33e7-1c80-4cbf-a6f1-46f22391bcd0)
Hình 12: Đăng xuất khỏi một phiên
