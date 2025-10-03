package kr.growup.havrutabe.user.service;

import kr.growup.havrutabe.auth.RefreshToken;
import kr.growup.havrutabe.auth.repository.RefreshTokenRepository;
import kr.growup.havrutabe.common.exception.BusinessException;
import kr.growup.havrutabe.common.exception.ErrorCode;
import kr.growup.havrutabe.common.security.CustomUserDetail;
import kr.growup.havrutabe.common.security.JwtTokenProvider;
import kr.growup.havrutabe.user.controller.dto.response.AuthResponse;
import kr.growup.havrutabe.user.domain.User;
import kr.growup.havrutabe.user.service.dto.LoginCommand;
import kr.growup.havrutabe.user.service.dto.NicknameDuplicationCheckCommand;
import kr.growup.havrutabe.user.service.dto.UserSignupCommand;
import kr.growup.havrutabe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;


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

    @Transactional
    public AuthResponse login(LoginCommand command) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(command.email(), command.password())
            );

            CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();

            String accessToken = jwtTokenProvider.generateToken(userDetail);
            String refreshTokenValue = jwtTokenProvider.generateRefreshToken(userDetail);

            RefreshToken refreshToken = RefreshToken.builder()
                    .token(refreshTokenValue)
                    .userId(userDetail.getId())
                    .expiryDate(LocalDateTime.now().plusDays(7))
                    .build();
            refreshTokenRepository.save(refreshToken);


            return new AuthResponse(
                    accessToken,
                    refreshTokenValue,
                    userDetail.getId(),
                    userDetail.getUsername()
            );

        } catch (AuthenticationException e) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED);
        }
    }

    @Transactional
    public AuthResponse refresh(String refreshTokenValue) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (!refreshToken.isValid()) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        User user = userRepository.findByIdOrThrow(refreshToken.getUserId());

        CustomUserDetail userDetail = CustomUserDetail.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();

        String newAccessToken = jwtTokenProvider.generateToken(userDetail);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetail);

        refreshToken.revoke();

        RefreshToken newToken = RefreshToken.builder()
                .token(newRefreshToken)
                .userId(user.getId())
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();
        refreshTokenRepository.save(newToken);


        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                user.getId(),
                user.getEmail()
        );
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    public void checkNicknameDuplication(NicknameDuplicationCheckCommand command) {
        if (Boolean.TRUE.equals(userRepository.existsByNickname(command.nickname()))) {
            throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
    }

    private void validateEmail(String email) {
        if (Boolean.TRUE.equals(userRepository.existsByEmail(email))) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }
}
