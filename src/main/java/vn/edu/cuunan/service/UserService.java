package vn.edu.cuunan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Cập nhật thông tin hồ sơ người dùng.
     */
    @Transactional
    public User updateProfile(Long userId, String fullName, String phone, String email, String address, String avatarUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId));

        if (!StringUtils.hasText(fullName)) {
            throw new IllegalArgumentException("Họ và tên không được để trống.");
        }
        if (!StringUtils.hasText(phone)) {
            throw new IllegalArgumentException("Số điện thoại không được để trống.");
        }

        String trimmedPhone = phone.trim();
        if (!trimmedPhone.equals(user.getPhone())) {
            Optional<User> existingUser = userRepository.findByPhone(trimmedPhone);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
                throw new IllegalArgumentException("Số điện thoại này đã được sử dụng bởi một tài khoản khác.");
            }
        }

        if (StringUtils.hasText(email)) {
            String trimmedEmail = email.trim().toLowerCase();
            if (!trimmedEmail.equals(user.getEmail() != null ? user.getEmail().toLowerCase() : "")) {
                Optional<User> existingUser = userRepository.findByEmail(trimmedEmail);
                if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
                    throw new IllegalArgumentException("Email này đã được sử dụng bởi một tài khoản khác.");
                }
            }
            user.setEmail(trimmedEmail);
        } else {
            user.setEmail(null);
        }

        user.setFullName(fullName.trim());
        user.setPhone(trimmedPhone);
        user.setAddress(StringUtils.hasText(address) ? address.trim() : null);
        if (StringUtils.hasText(avatarUrl)) {
            user.setAvatarUrl(avatarUrl.trim());
        }

        User updatedUser = userRepository.save(user);
        log.info(">>> Đã cập nhật thông tin hồ sơ cho tài khoản [{}], ID={}", user.getUsername(), user.getId());
        return updatedUser;
    }

    /**
     * Đổi mật khẩu tài khoản.
     */
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword, String confirmPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId));

        if (!StringUtils.hasText(currentPassword)) {
            throw new IllegalArgumentException("Vui lòng nhập mật khẩu hiện tại.");
        }
        if (!StringUtils.hasText(newPassword)) {
            throw new IllegalArgumentException("Vui lòng nhập mật khẩu mới.");
        }
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu mới phải có độ dài từ 6 ký tự trở lên.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Mật khẩu mới và xác nhận mật khẩu không trùng khớp.");
        }
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không chính xác.");
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới không được trùng với mật khẩu hiện tại.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info(">>> Đã đổi mật khẩu thành công cho tài khoản [{}], ID={}", user.getUsername(), user.getId());
    }
}
