package com.namnguyen1409.usermanagement.utils;

import com.namnguyen1409.usermanagement.dto.request.UpdateUserRequest;
import com.namnguyen1409.usermanagement.entity.User;
import com.namnguyen1409.usermanagement.exception.AppException;
import com.namnguyen1409.usermanagement.exception.ErrorCode;
import com.namnguyen1409.usermanagement.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityUtils {

    UserRepository userRepository;

    /**
     * Lấy id người dùng hiện tại từ ngữ cảnh bảo mật.
     * <p>
     * Phương thức này truy cập vào {@link SecurityContextHolder} để lấy thông tin xác thực
     * và trả về id của người dùng hiện tại.
     *
     * @return Id người dùng hiện tại dưới dạng {@link String}.
     */
    public String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


    /**
     * Lấy thông tin người dùng hiện tại từ ngữ cảnh bảo mật.
     * <p>
     * Phương thức này sẽ sử dụng id người dùng hiện tại từ SecurityContext để truy vấn thông tin người dùng
     * trong cơ sở dữ liệu. Nếu không tìm thấy người dùng nào khớp với id người dùng,
     * phương thức sẽ ném ngoại lệ {@code AppException} với mã lỗi {@link ErrorCode#USER_NOT_FOUND}.
     *
     * @return Đối tượng {@link User} đại diện cho thông tin người dùng hiện tại.
     * @throws AppException Nếu không tìm thấy người dùng trong hệ thống.
     */
    public User getCurrentUser() {
        String userId = getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * Lấy thông tin người dùng dựa trên ID.
     * <p>
     * Phương thức này tìm kiếm người dùng trong cơ sở dữ liệu dựa trên ID được cung cấp.
     * Nếu không tìm thấy người dùng nào khớp với ID này, ngoại lệ {@code AppException} sẽ được ném ra
     * với mã lỗi {@link ErrorCode#USER_NOT_FOUND}.
     *
     * @param id ID của người dùng cần tìm kiếm.
     * @return Đối tượng {@link User} tương ứng với ID được cung cấp.
     * @throws AppException Nếu không tìm thấy người dùng trong cơ sở dữ liệu.
     */
    public User getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }


    /**
     * Kiểm tra xung đột thông tin người dùng theo các trường username, email và số điện thoại.
     * <p>
     * Phương thức này thực hiện kiểm tra xem tên đăng nhập (username), email hoặc số điện thoại
     * có gây ra xung đột với bất kỳ tài khoản người dùng nào khác trong hệ thống hay không.
     * Nếu có xung đột, một ngoại lệ {@code AppException} với mã lỗi {@link ErrorCode#USER_EXISTED}
     * sẽ được ném ra.
     *
     * @param request {@link UpdateUserRequest} chứa thông tin người dùng cần kiểm tra.
     * @param userId ID của người dùng hiện tại để loại trừ khỏi kiểm tra xung đột.
     * @throws AppException Nếu phát hiện thông tin gây xung đột với người dùng khác.
     */
    public void checkUserConflict(UpdateUserRequest request, String userId) {
        if (request.getUsername() != null && userRepository.existsByUsernameAndIdNot(request.getUsername(), userId)) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (request.getEmail() != null && userRepository.existsByEmailAndIdNot(request.getEmail(), userId)) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (request.getPhone() != null && userRepository.existsByPhoneAndIdNot(request.getPhone(), userId)) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
    }

    /**
     * Kiểm tra xem người dùng có bị đánh dấu là đã xóa hay không.
     * Nếu người dùng đã bị xóa, phương thức sẽ ném ra một ngoại lệ {@code AppException} với mã lỗi {@link ErrorCode#USER_DELETED}.
     *
     * @param user Đối tượng {@link User} cần kiểm tra trạng thái xóa.
     * @throws AppException Nếu người dùng đã bị xóa (trường isDeleted là {@code true}).
     */
    public void checkUserDeleted(User user) {
        if (user.getIsDeleted()) {
            throw new AppException(ErrorCode.USER_DELETED);
        }
    }

    /**
     * Kiểm tra xem người dùng hiện tại có quyền Super Admin hay không.
     *
     * @return {@code true} nếu người dùng hiện tại có quyền Super Admin, ngược lại trả về {@code false}.
     */
    public boolean isSuperAdmin() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_SUPER_ADMIN"));
    }

    /**
     * Kiểm tra quyền của người dùng hiện tại với tư cách Super Admin hoặc Admin đối với một người dùng mục tiêu.
     * <p>
     * Phương thức này đảm bảo rằng:
     * - Không ai có thể cập nhật thông tin của Super Admin.
     * - Chỉ Super Admin mới có quyền cập nhật thông tin của Admin khác.
     *
     * @param targetUser Người dùng mục tiêu (đối tượng {@link User}) cần kiểm tra quyền truy cập.
     * @throws AppException Nếu người dùng mục tiêu là Super Admin hoặc nếu người gọi không phải Super Admin và cố gắng cập nhật Admin.
     */
    public void checkAdminPrivileges(User targetUser) {
        boolean isTargetAdmin = targetUser.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("ADMIN"));

        boolean isTargetSuperAdmin = targetUser.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("SUPER_ADMIN"));

        if (isTargetSuperAdmin) {
            throw new AppException(ErrorCode.CANNOT_UPDATE_SUPER_ADMIN);
        }

        if (isTargetAdmin && !isSuperAdmin()) {
            throw new AppException(ErrorCode.CANNOT_UPDATE_OTHER_ADMIN);
        }
    }

}
