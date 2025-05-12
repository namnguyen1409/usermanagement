# **HỆ THỐNG QUẢN LÝ NGƯỜI DÙNG CÓ PHÂN QUYỀN**

---
### **1. Mục tiêu đề tài**
Xây dựng hệ thống quản lý người dùng cho phép:
- Đăng ký, đăng nhập, đăng xuất tài khoản.
- Gán quyền (permission) và vai trò (role) cho người dùng.
- Phân quyền người cho người dùng truy cập vào các API phù hợp.
- Hiển thị thông tin cá nhân sau khi đăng nhập.
- Khoá tài khoản người nếu nhập sai mật khẩu quá 5 lần.

---
### **2. Công nghệ sử dụng**
| **Công nghệ / Thư viện**                   | **Vai trò**                                                    |
|--------------------------------------------|----------------------------------------------------------------|
| **Spring Boot**                            | Framework chính để xây dựng ứng dụng backend.                  |
| **Spring Web** (`spring-boot-starter-web`) | Xây dựng RESTful API.                                          |
| **Spring Security**                        | Xác thực và phân quyền người dùng.                             |
| **JWT** *(tích hợp qua Spring Security)*   | Xác thực không trạng thái thông qua token.                     |
| **Spring Data JPA**                        | ORM giúp thao tác với database thông qua entity.               |
| **Hibernate**                              | ORM (Object-Relational Mapping) framework hỗ trợ JPA           |
| **Spring Validation**                      | Kiểm tra tính hợp lệ của dữ liệu đầu vào từ client.            |
| **Oracle JDBC Driver**                     | Kết nối với cơ sở dữ liệu Oracle.                              |
| **Spring OAuth2 Resource Server**          | Hỗ trợ xác thực tài nguyên với token (OAuth2).                 |
| **MapStruct**                              | Tự động ánh xạ giữa DTO và entity, giảm code boilerplate.      |
| **Lombok**                                 | Tạo tự động getter/setter, constructor, builder, v.v.          |
| **Maven**                                  | Quản lý phụ thuộc và build project.                            |
| **Spring Boot Devtools**                   | Tự động reload khi code thay đổi, hỗ trợ phát triển nhanh hơn. |

---
### **3. Kiến trúc hệ thống**
Kiến trúc hệ thống của dự án "Hệ thống quản lý người dùng có phân quyền" được thiết kế theo **mô hình 3 lớp (Three-Layered Architecture)**, bao gồm:
- **Lớp Presentation (Controller)**
- **Lớp Business Logic (Service)**
- **Lớp Data Access (Repository)**

#### **3.1. Lớp Presentation (Controller Layer)**

Lớp Controller xử lý các yêu cầu HTTP từ người dùng (client). Các controller này chịu trách nhiệm nhận các yêu cầu từ phía client, gọi các service cần thiết, và trả lại kết quả cho người dùng.

Các lớp controller trong dự án bao gồm:
- **UserController**: Quản lý các thao tác với người dùng, như tạo, sửa, xóa, lấy thông tin người dùng.
- **RoleController**: Quản lý các thao tác với vai trò người dùng như lấy danh sách các vai trò được lưu trong hệ thống.
- **ProfileController**: Quản lý thông tin hồ sơ của người dùng hiện tại như xem, sửa, đổi mật khẩu, xóa tài khoản.
- **AuthenticationController**: Quản lý các yêu cầu đăng nhập, đăng ký, đăng xuất, lấy jwt mới.

Các controller này sử dụng **@RestController** và các annotation của Spring như **@GetMapping**, **@PostMapping**, **@PutMapping**, **@DeleteMapping** để xử lý các yêu cầu HTTP.

#### **3.2. Lớp Business Logic (Service Layer)**

Lớp Service chứa các logic nghiệp vụ, thực hiện các công việc xử lý chính của hệ thống. Lớp này tương tác với lớp Repository để truy vấn dữ liệu từ cơ sở dữ liệu và thực hiện các thao tác nghiệp vụ.

Các lớp trong service layer bao gồm:
- **UserService**: Quản lý nghiệp vụ liên quan đến người dùng, như tạo, sửa, xóa người dùng, và quản lý quyền hạn.
- **RoleService**: Quản lý nghiệp vụ liên quan đến vai trò người dùng.
- **ProfileService**: Quản lý nghiệp vụ thông tin cá nhân của người dùng.
- **AuthenticationService**: Quản lý các nghiệp vụ xác thực người dùng, bao gồm đăng nhập, tạo JWT token, và xác thực token.
- **SetupService**: Cung cấp các dịch vụ khởi tạo cấu hình ban đầu cho hệ thống.

Mỗi service sẽ thực hiện các tác vụ gọi tới các repository và thực thi các logic nghiệp vụ cần thiết.

---

#### **3.3. Lớp Data Access (Repository Layer)**

Lớp Repository truy xuất dữ liệu từ cơ sở dữ liệu. Đây là nơi tương tác trực tiếp với JPA để thực hiện các thao tác CRUD (Create, Read, Update, Delete) trên các thực thể của hệ thống.

Các lớp Repository trong dự án bao gồm:
- **UserRepository**: Quản lý các thao tác với thực thể User.
- **RoleRepository**: Quản lý các thao tác với thực thể Role.
- **PermissionRepository**: Quản lý các thao tác với thực thể Permission.
- **TokenBlacklistRepository**: Quản lý các thao tác với danh sách token bị thu hồi.

Các repository này mở rộng từ **JpaRepository** của Spring Data JPA và cho phép truy vấn dữ liệu nhanh chóng và dễ dàng.


#### **3.4. Cấu trúc bảo mật và phân quyền**

Hệ thống sử dụng **Spring Security** để quản lý bảo mật và phân quyền. Các thành phần chính trong bảo mật bao gồm:
- **SecurityConfig**: Cấu hình bảo mật của Spring Security, xác định các rules cho các yêu cầu HTTP, phân quyền và xác thực.
- **JwtAuthenticationEntryPoint**: Xử lý các lỗi khi xác thực JWT.
- **CustomJwtDecoder**: Giải mã và xác thực JWT token.
- **UserRole** và **UserPermission**: Định nghĩa các vai trò và quyền của người dùng, sử dụng để phân quyền trong hệ thống.

Cấu hình phân quyền chi tiết giúp đảm bảo chỉ những người dùng có quyền mới có thể truy cập vào các API nhất định.

#### **3.5. Mô hình dữ liệu**

Hệ thống quản lý người dùng sử dụng cơ sở dữ liệu quan hệ với các thực thể chính sau:
- **User**: Lưu thông tin người dùng, bao gồm các thuộc tính như username, password, email, vai trò, v.v.
- **Role**: Lưu thông tin về các vai trò mà người dùng có thể có (SUPER_ADMIN, ADMIN, USER).
- **Permission**: Lưu thông tin về quyền truy cập của từng vai trò.
- **TokenBlacklist**: Lưu các token JWT bị thu hồi.

Hệ thống sử dụng **JPA** kết hợp với **Hibernate** để tương tác với cơ sở dữ liệu Oracle, với các repository hỗ trợ các thao tác CRUD tự động.

---

### **4. Cơ sở lý thuyết**

#### 4.1. **Xác thực và phân quyền (Authentication and Authorization)**

- **Xác thực (Authentication)**: Đây là quá trình kiểm tra và xác nhận danh tính của người dùng khi họ đăng nhập vào hệ thống. 
Hệ thống sử dụng mã JWT (JSON Web Token) để thực hiện xác thực.
JWT là một dạng token được mã hóa và chứa thông tin người dùng như tên, vai trò (role), quyền hạn (permissions), 
giúp xác minh rằng yêu cầu gửi đến server là từ một người dùng hợp lệ.

- **Phân quyền (Authorization)**: Sau khi xác thực, phân quyền đảm bảo rằng người dùng chỉ có thể truy cập vào những tài
nguyên mà họ có quyền. Ví dụ, một người dùng với vai trò `ROLE_ADMIN` sẽ có quyền truy cập các tài nguyên nâng cao trong hệ thống, 
trong khi một người dùng với vai trò `ROLE_USER` chỉ có thể truy cập các tài nguyên giới hạn. 
Trong Spring Security, phân quyền được thực hiện thông qua các annotations như `@PreAuthorize` hoặc trong các cấu hình bảo mật như `httpSecurity`.

#### 4.2. **JWT (JSON Web Token)**

JWT là một phương pháp xác thực phổ biến trong các ứng dụng web hiện đại. JWT cho phép việc truyền tải thông tin giữa các hệ thống dưới dạng một token nhỏ gọn và an toàn. JWT bao gồm ba phần:
- **Header**: Chứa thông tin về loại token và thuật toán mã hóa (thường là `HS256` hoặc `RS256`).
- **Payload**: Chứa các tuyên bố (claims), tức là các thông tin về người dùng như ID, tên, quyền, vai trò.
- **Signature**: Phần mã hóa của token, được tạo ra từ header và payload, sử dụng một khóa bí mật (secret key).

Token JWT giúp bảo mật các API RESTful bằng cách yêu cầu người dùng phải cung cấp một token hợp lệ trong mỗi yêu cầu HTTP. Đây là một hình thức xác thực stateless, tức là không cần phải lưu trữ session trên server, giúp giảm tải cho hệ thống.

#### 4.3. **OAuth2 và OAuth2 Resource Server**

- **OAuth2**: Là một giao thức ủy quyền (authorization protocol) cho phép các ứng dụng bên thứ ba truy cập tài nguyên 
của người dùng mà không cần phải biết mật khẩu của người dùng. OAuth2 được sử dụng rộng rãi trong các ứng dụng web 
để cung cấp khả năng đăng nhập qua các dịch vụ bên ngoài như Google, Facebook, v.v. OAuth2 cung cấp ba loại ủy quyền chính:
    - **Authorization Code Grant**: Dùng cho ứng dụng web.
    - **Implicit Grant**: Dùng cho ứng dụng client-side.
    - **Password Grant**: Dùng khi ứng dụng có quyền truy cập mật khẩu người dùng.

- **OAuth2 Resource Server**: Hệ thống sẽ đóng vai trò là OAuth2 Resource Server, nơi mà các yêu cầu truy cập tài nguyên 
(API) chỉ được phép khi có một token JWT hợp lệ. Bằng cách này, bảo mật cho API sẽ được quản lý qua token OAuth2.

#### 4.4. **CORS (Cross-Origin Resource Sharing)**

CORS là một cơ chế cho phép các yêu cầu HTTP được thực hiện từ một nguồn (domain) khác với nguồn của tài nguyên (origin). 
Trong các ứng dụng frontend-backend phân tán, CORS là một vấn đề quan trọng cần được cấu hình để đảm bảo rằng chỉ những 
nguồn hợp lệ mới có thể truy cập vào tài nguyên của server.

Trong hệ thống, CORS đã được cấu hình để cho phép các yêu cầu từ một frontend đang chạy ở địa chỉ `http://localhost:3001`. 
Điều này giúp đảm bảo rằng frontend và backend có thể giao tiếp với nhau một cách an toàn mà không bị các vấn đề về bảo mật.

#### 4.5. **Spring Security**

Spring Security là một framework bảo mật mạnh mẽ dành cho các ứng dụng Java. 
Nó cung cấp các tính năng bảo mật như xác thực, phân quyền, bảo vệ CSRF, 
bảo vệ chống các cuộc tấn công từ phía client (ví dụ, XSS, SQL Injection) và nhiều tính năng bảo mật khác.
Spring Security được tích hợp chặt chẽ với Spring Boot, giúp việc cấu hình bảo mật trở nên dễ dàng và linh hoạt.

- **Các thành phần chính trong Spring Security**:
    - **AuthenticationManager**: Quản lý quá trình xác thực người dùng.
    - **SecurityContextHolder**: Lưu trữ thông tin xác thực trong suốt quá trình xử lý yêu cầu.
    - **AuthenticationEntryPoint**: Được sử dụng để xử lý các lỗi xác thực (ví dụ: khi người dùng cung cấp token không hợp lệ).
    - **AuthorizationManager**: Quản lý phân quyền và quyết định liệu người dùng có quyền truy cập vào tài nguyên hay không.

#### 4.6. **Bảo mật trong REST API**

REST API cần được bảo vệ để ngăn ngừa các cuộc tấn công như tấn công SQL Injection, Cross-Site Scripting (XSS), 
và các lỗ hổng bảo mật khác. Các API cần phải:
- **Xác thực** người dùng qua các phương thức như Basic Auth, JWT hoặc OAuth2.
- **Phân quyền** người dùng dựa trên vai trò và quyền hạn của họ.
- **Kiểm tra và xử lý dữ liệu đầu vào** để ngăn ngừa các cuộc tấn công XSS và SQL Injection.

#### 4.7. **Bảo mật**

Một trong những vấn đề quan trọng trong bảo mật hệ thống là đảm bảo mã nguồn của hệ thống được bảo 
vệ khỏi các nguy cơ khai thác lỗ hổng. Việc sử dụng các công cụ như **BCrypt** để mã hóa mật khẩu và 
xác thực các JWT token hợp lệ sẽ giúp bảo vệ hệ thống khỏi các cuộc tấn công bằng cách giả mạo 
hoặc đánh cắp thông tin đăng nhập.

---

### 5. **Chi tiết thiết kế**

#### 5.1. **Mô hình thực thể**

##### 5.1.1. **Thiết kế thực thể `BaseEntity`**

| Trường      | Kiểu dữ liệu  | Ràng buộc             | Mô tả                          |
|-------------|---------------|-----------------------|--------------------------------|
| `id`        | String (UUID) | Not null, Primary key | Định danh duy nhất của bản ghi |
| `createdAt` | LocalDateTime | Auto-generated        | Thời điểm tạo bản ghi          |
| `createdBy` | String        | Auto-generated        | Người tạo bản ghi              |
| `updatedAt` | LocalDateTime | Auto-generated        | Thời điểm cập nhật bản ghi     |
| `updatedBy` | String        | Auto-generated        | Người cập nhật bản ghi         |
| `isDeleted` | Boolean       | Default = `false`     | Đánh dấu bản ghi đã bị xóa mềm |
| `deletedAt` | LocalDateTime |                       | Thời điểm xóa bản ghi (nếu có) |
| `deletedBy` | String        |                       | Người xóa bản ghi (nếu có)     |

##### 5.1.2. **Thiết kế thực thể `User`**

| Trường               | Kiểu dữ liệu      | Ràng buộc        | Mô tả                      |
|----------------------|-------------------|------------------|----------------------------|
| `username`           | String (50)       | Not null, Unique | Tên đăng nhập              |
| `password`           | String (100)      | Not null         | Mật khẩu đã mã hóa         |
| `firstName`          | String (50)       | Not null         | Tên người dùng             |
| `lastName`           | String (50)       | Not null         | Họ người dùng              |
| `email`              | String (100)      | Not null, Unique | Email                      |
| `phone`              | String (20)       | Not null, Unique | Số điện thoại              |
| `gender`             | Boolean           | Not null         | Giới tính (`true`: Nam)    |
| `birthday`           | LocalDate         | Not null         | Ngày sinh                  |
| `address`            | String            | Not null         | Địa chỉ                    |
| `roles`              | `Set<Role>`       | ManyToMany       | Danh sách vai trò          |
| `revokedPermissions` | `Set<Permission>` | ManyToMany       | Danh sách quyền bị thu hồi |

#### 5.1.3. **Thiết kế thực thể `Role`**

| Trường        | Kiểu dữ liệu      | Ràng buộc   | Mô tả                                             |
|---------------|-------------------|-------------|---------------------------------------------------|
| `name`        | Enum `UserRole`   | Primary Key | Tên vai trò (ví dụ: `ADMIN`, `STAFF`, `CUSTOMER`) |
| `description` | String            | —           | Mô tả chi tiết về vai trò                         |
| `permissions` | Set\<Permission\> | Quan hệ N-N | Danh sách quyền của vai trò                       |

#### 5.1.4. **Thiết kế thực thể `Permission`**

| Trường        | Kiểu dữ liệu          | Ràng buộc   | Mô tả                                          |
|---------------|-----------------------|-------------|------------------------------------------------|
| `name`        | Enum `UserPermission` | Primary Key | Tên quyền (ví dụ: `READ_USER`, `CREATE_ORDER`) |
| `description` | String                | —           | Mô tả chi tiết về quyền                        |


####  5.1.5. **Thiết kế thực thể `TokenBlacklist`**

| Trường      | Kiểu dữ liệu  | Ràng buộc    | Mô tả                                |
|-------------|---------------|--------------|--------------------------------------|
| `tokenId`   | String        | Primary Key  | ID định danh của token đã bị thu hồi |
| `createdAt` | LocalDateTime | Tự động sinh | Thời điểm token bị đưa vào blacklist |

####  5.1.5. **Thiết kế thực thể `TokenBl`**

#### 5.2. **Luồng xử lý xác thực**

1. **Người dùng gửi yêu cầu đăng nhập (POST /auth/login)** với username/password.
2. **Hệ thống kiểm tra thông tin đăng nhập**, nếu hợp lệ:
    - Sinh access token (JWT) và refresh token.
    - Gửi token về client.
3. **Client dùng access token để gọi các API khác.**
4. Khi token hết hạn, client có thể gửi refresh token tới **/auth/refresh-token** để nhận token mới.
5. Khi người dùng đăng xuất, token được thêm vào bảng `TokenBlacklist`.

#### 5.3. **Cấu hình bảo mật**

Trong `SecurityConfig`, cấu hình `SecurityFilterChain` như sau:

- Các endpoint `/auth/login`, `/auth/register`, `/auth/refresh-token`,... được phép truy cập công khai (`permitAll()`).
- Các endpoint khác yêu cầu phải có token JWT hợp lệ.
- Token được giải mã bằng `CustomJwtDecoder` và quyền được ánh xạ qua `JwtAuthenticationConverter`.
- CORS được cấu hình để cho phép frontend ở `localhost:3001` giao tiếp với backend.

#### 5.4. **Luồng xử lý phân quyền**

- Vai trò người dùng được ánh xạ vào token JWT khi sinh token.
- Khi người dùng gửi yêu cầu có token JWT, hệ thống giải mã token để lấy `roles/permissions`.
- Spring Security kết hợp với annotation `@PreAuthorize` trong controller/service để kiểm soát truy cập.

```java
@PreAuthorize("hasAnyAuthority('VIEW_USER')")
@Override
public Page<UserResponse> filterUsers(FilterUserRequest filterUserRequest) {
    
}
```

#### 5.5. **Thiết kế các API**

| **Phương thức** | **Endpoint**         | **Vai trò/quyền** | **Mô tả**                                                        |
|-----------------|----------------------|-------------------|------------------------------------------------------------------|
| `POST`          | /auth/register       | `PUBLIC`          | Đăng ký tài khoản mới                                            |
| `POST`          | /auth/login          | `PUBLIC`          | Đăng nhập, nhận jwt                                              |
| `POST`          | /auth/refresh-token  | `PUBLIC`          | Nhận token cũ và trả về token mới                                |
| `POST`          | /auth/logout         | `PUBLIC`          | Đăng xuất, vô hiệu hóa token                                     |
| `GET`           | /profile             | `ALL ROLE`        | Lấy thông tin tài khoản hiện tại                                 |
| `PUT`           | /profile             | `ALL ROLE`        | Cập nhật thông tin tài khoản                                     |
| `PUT`           | /profile/password    | `ALL ROLE`        | Đổi mật khẩu                                                     |
| `DELETE`        | /profile             | `ALL ROLE`        | Xóa tài khoản cá nhân                                            |
| `GET`           | /login-history       | `ALL ROLE`        | Lấy danh sách lịch sử đăng nhập của cá nhân                      |
| `POST`          | /login-history       | `ALL ROLE`        | Lấy danh sách lịch sử đăng nhập của cá nhân + lọc theo điều kiện |
| `GET`           | /roles               | `ADMIN`           | Lấy danh sách quyền trong hệ thống                               |
| `GET`           | /users               | `VIEW_USER`       | Lấy danh sách người dùng trong hệ thống                          |
| `POST`          | /users               | `VIEW_USER`       | Lấy danh sách người dùng trong hệ thống + lọc theo điều kiện     |
| `GET`           | /users/login-history | `VIEW_USER`       | Lấy danh sách lịch sử đăng nhập người dùng                       |
| `POST`          | /users/login-history | `VIEW_USER`       | Lấy danh sách lịch sử đăng nhập người dùng + lọc theo điều kiện  |
| `GET`           | /users/{id}          | `VIEW_USER`       | Xem thông tin người dùng theo id                                 |
| `POST`          | /users/create        | `ADD_USER`        | Tạo một người dùng mới                                           |
| `PUT`           | /users/{id}          | `EDIT_USER`       | Sửa thông tin người dùng theo id                                 |
| `DELETE`        | /users/{id}          | `DELETE_USER`     | Xóa một người dùng theo id                                       |
| `POST`          | /users/restore/{id}  | `SUPER_ADMIN`     | Khôi phục người dùng bị xóa                                      |


#### 5.7. **Xử lý lỗi**

- Mỗi lớp đều xử lý ngoại lệ rõ ràng bằng custom exception và `@ControllerAdvice`.
- JWT không hợp lệ hoặc hết hạn sẽ trả mã lỗi 401 với thông báo cụ thể.
- Validation dữ liệu đầu vào sử dụng `@Valid`, `@NotBlank`,... trong DTO.

