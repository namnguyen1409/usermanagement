package com.namnguyen1409.usermanagement.service;

import com.namnguyen1409.usermanagement.dto.request.*;
import com.namnguyen1409.usermanagement.dto.response.CreateUserResponse;
import com.namnguyen1409.usermanagement.dto.response.IntrospectResponse;
import com.namnguyen1409.usermanagement.dto.response.LoginResponse;
import com.namnguyen1409.usermanagement.dto.response.RefreshTokenResponse;
import com.namnguyen1409.usermanagement.exception.AppException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Giao diện {@code AuthenticationService} cung cấp các phương thức để quản lý
 * và thực hiện các thao tác liên quan đến xác thực người dùng.
 * Các chức năng chính bao gồm kiểm tra tính hợp lệ của token, đăng ký, đăng nhập,
 * đăng xuất và làm mới token xác thực.
 */
public interface AuthenticationService {


    /**
     * Phương thức này thực hiện đăng ký một tài khoản người dùng mới với thông tin được cung cấp
     * trong yêu cầu. Nếu thông tin hợp lệ và xử lý thành công, tài khoản sẽ được tạo và trả về
     * thông tin xác nhận kết quả.
     *
     * @param request Đối tượng {@link CreateUserRequest} chứa các thông tin cần thiết để đăng ký người dùng,
     *                bao gồm username, mật khẩu, thông tin cá nhân, email, số điện thoại, danh sách vai trò, v.v.
     * @return Đối tượng {@link CreateUserResponse} chứa kết quả của quá trình đăng ký, với thuộc tính:
     *         <ul>
     *             <li>{@code success}: {@code true} nếu đăng ký thành công, ngược lại là {@code false}.</li>
     *         </ul>
     */
    CreateUserResponse register(CreateUserRequest request);

    /**
     * Phương thức này thực hiện chức năng đăng nhập cho người dùng.
     * Dựa trên thông tin xác thực được cung cấp trong yêu cầu, hệ thống sẽ kiểm tra và xác thực người dùng.
     * Nếu thông tin hợp lệ, một token xác thực sẽ được tạo và trả về.
     *
     * @param request Đối tượng {@link LoginRequest} chứa thông tin đăng nhập của người dùng,
     *                bao gồm tên đăng nhập và mật khẩu.
     * @return Đối tượng {@link LoginResponse} chứa token xác thực và thông tin trạng thái đăng nhập:
     *         <ul>
     *             <li>{@code token}: Chuỗi token được tạo nếu đăng nhập thành công.</li>
     *             <li>{@code isAuthenticated}: {@code true} nếu người dùng đã được xác thực thành công,
     *             ngược lại là {@code false}.</li>
     *         </ul>
     */
    LoginResponse login(LoginRequest request, HttpServletRequest httpServletRequest);

    /**
     * Phương thức này thực hiện chức năng đăng xuất cho người dùng.
     * Dựa trên thông tin trong yêu cầu, hệ thống sẽ xử lý việc
     * vô hiệu hóa token của người dùng.
     *
     * @param token Đối tượng {@link LogoutRequest} chứa thông tin token
     *              cần được xử lý để thực hiện việc đăng xuất.
     */
    void logout(LogoutRequest token);

    /**
     * Phương thức này thực hiện làm mới token xác thực.
     * Dựa trên token cũ được truyền vào, hệ thống sẽ kiểm tra và tạo ra một token mới nếu hợp lệ.
     *
     * @param token Đối tượng {@link RefreshTokenRequest} chứa thông tin token hiện tại cần làm mới.
     *              Bao gồm thuộc tính:
     *              <ul>
     *                  <li>{@code token}: Chuỗi token hiện tại cần xác nhận và làm mới.</li>
     *              </ul>
     * @return Đối tượng {@link RefreshTokenResponse} mô tả kết quả của việc làm mới token, bao gồm:
     *         <ul>
     *             <li>{@code token}: Chuỗi token đã được làm mới nếu thành công.</li>
     *             <li>{@code isAuthenticated}: {@code true} nếu token hợp lệ và đã được cấp phát lại,
     *             ngược lại là {@code false}.</li>
     *         </ul>
     */
    RefreshTokenResponse refreshToken(RefreshTokenRequest token);

    void introspect(IntrospectRequest introspectRequest);
}
