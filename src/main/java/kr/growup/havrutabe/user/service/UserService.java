package kr.growup.havrutabe.user.service;

import kr.growup.havrutabe.common.exception.BusinessException;
import kr.growup.havrutabe.common.exception.ErrorCode;
import kr.growup.havrutabe.user.domain.User;
import kr.growup.havrutabe.user.service.dto.NicknameDuplicationCheckCommand;
import kr.growup.havrutabe.user.service.dto.UserSignupCommand;
import kr.growup.havrutabe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signUp(UserSignupCommand command) {
        validateEmail(command.email());
        //TODO 이메일 인증 검증
        try {
            User user = command.toEntity();
            user.encodePassword(passwordEncoder);
            return userRepository.save(user).getId();
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }
    }

    public void checkNicknameDuplication(NicknameDuplicationCheckCommand command) {
        if (userRepository.existsByNickname(command.nickname())) {
            throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }
}
