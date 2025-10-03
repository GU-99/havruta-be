package kr.growup.havrutabe.user;

import kr.growup.havrutabe.auth.RefreshToken;
import kr.growup.havrutabe.auth.repository.RefreshTokenRepository;
import kr.growup.havrutabe.common.exception.BusinessException;
import kr.growup.havrutabe.common.exception.ErrorCode;
import kr.growup.havrutabe.common.security.CustomUserDetail;
import kr.growup.havrutabe.common.security.JwtTokenProvider;
import kr.growup.havrutabe.test.annotation.AutoKoreanDisplayName;
import kr.growup.havrutabe.user.controller.dto.response.AuthResponse;
import kr.growup.havrutabe.user.domain.User;
import kr.growup.havrutabe.user.repository.UserRepository;
import kr.growup.havrutabe.user.service.UserService;
import kr.growup.havrutabe.user.service.dto.LoginCommand;
import kr.growup.havrutabe.user.service.dto.NicknameDuplicationCheckCommand;
import kr.growup.havrutabe.user.service.dto.UserSignupCommand;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static kr.growup.havrutabe.test.fixture.user.builder.NicknameDuplicationCheckRequestTestBuilder.닉네임_중복_검사는;
import static kr.growup.havrutabe.test.fixture.user.builder.UserSignupRequestTestBuilder.가입하는_사용자는;
import static kr.growup.havrutabe.test.fixture.user.builder.UserTestBuilder.사용자는;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@AutoKoreanDisplayName
@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    UserService userService;

    @Captor
    ArgumentCaptor<User> userCaptor;

    @Captor
    ArgumentCaptor<RefreshToken> refreshTokenCaptor;

    @Captor
    ArgumentCaptor<String> emailCaptor;

    @Captor
    ArgumentCaptor<String> nicknameCaptor;

    @Captor
    ArgumentCaptor<Long> userIdCaptor;

    @Nested
    class 사용자가_회원가입_시에 {

        @Test
        void 성공적으로_계정을_생성한다() {
            // given
            Long 예상_새_사용자_ID = 1L;
            User 새_사용자 = 사용자는().식별자가(예상_새_사용자_ID).이다();
            UserSignupCommand 사용자_생성_요청 = 가입하는_사용자는(새_사용자).이다().toCommand();

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(새_사용자);

            // when
            Long 실제_새_사용자_ID = userService.signUp(사용자_생성_요청);

            // then
            assertThat(실제_새_사용자_ID).isEqualTo(예상_새_사용자_ID);

            verify(userRepository).existsByEmail(emailCaptor.capture());
            assertThat(emailCaptor.getValue()).isEqualTo(사용자_생성_요청.email());

            verify(userRepository).save(userCaptor.capture());
            User 저장된_사용자 = userCaptor.getValue();
            assertThat(저장된_사용자.getEmail()).isEqualTo(사용자_생성_요청.email());
            assertThat(저장된_사용자.getNickname()).isEqualTo(사용자_생성_요청.nickname());
        }

        @Test
        void 중복된_이메일을_사용하면_예외가_발생한다() {
            // given
            User 새_사용자 = 사용자는().이메일이("duplicate@test.com").이다();
            UserSignupCommand 사용자_생성_요청 = 가입하는_사용자는(새_사용자).이다().toCommand();

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            doThrow(DataIntegrityViolationException.class).when(userRepository).save(any(User.class));

            // when & then
            assertThatThrownBy(() -> userService.signUp(사용자_생성_요청))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_ALREADY_EXISTS);
        }

        @Test
        void 이메일이_이미_존재하면_예외가_발생한다() {
            // given
            User 새_사용자 = 사용자는().이메일이("existing@test.com").이다();
            UserSignupCommand 사용자_생성_요청 = 가입하는_사용자는(새_사용자).이다().toCommand();

            when(userRepository.existsByEmail(사용자_생성_요청.email())).thenReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.signUp(사용자_생성_요청))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    @Nested
    class 사용자가_로그인_시에 {

        @Test
        void 성공적으로_로그인한다() {
            // given
            String 이메일 = "test@test.com";
            String 비밀번호 = "password123";
            Long 사용자_ID = 1L;
            String 예상_액세스_토큰 = "access-token";
            String 예상_리프레시_토큰 = "refresh-token";
            LoginCommand 로그인_요청 = new LoginCommand(이메일, 비밀번호);

            CustomUserDetail userDetail = CustomUserDetail.builder()
                    .id(사용자_ID)
                    .email(이메일)
                    .password(비밀번호)
                    .build();

            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal()).thenReturn(userDetail);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(jwtTokenProvider.generateToken(any(CustomUserDetail.class))).thenReturn(예상_액세스_토큰);
            when(jwtTokenProvider.generateRefreshToken(any(CustomUserDetail.class))).thenReturn(예상_리프레시_토큰);
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(mock(RefreshToken.class));

            // when
            AuthResponse response = userService.login(로그인_요청);

            // then
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEqualTo(예상_액세스_토큰);
            assertThat(response.refreshToken()).isEqualTo(예상_리프레시_토큰);
            assertThat(response.userId()).isEqualTo(사용자_ID);
            assertThat(response.email()).isEqualTo(이메일);

            verify(refreshTokenRepository).save(refreshTokenCaptor.capture());
            RefreshToken 저장된_리프레시_토큰 = refreshTokenCaptor.getValue();
            assertThat(저장된_리프레시_토큰.getToken()).isEqualTo(예상_리프레시_토큰);
            assertThat(저장된_리프레시_토큰.getUserId()).isEqualTo(사용자_ID);
            assertThat(저장된_리프레시_토큰.getExpiryDate()).isAfter(LocalDateTime.now());
        }

        @Test
        void 잘못된_비밀번호로_로그인하면_예외가_발생한다() {
            // given
            LoginCommand 로그인_요청 = new LoginCommand("test@test.com", "wrong-password");

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            // when & then
            assertThatThrownBy(() -> userService.login(로그인_요청))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.AUTHENTICATION_FAILED);
        }
    }

    @Nested
    class 토큰_갱신_시에 {

        @Test
        void 유효한_리프레시_토큰으로_새_토큰을_발급한다() {
            // given
            String refreshTokenValue = "valid-refresh-token";
            Long 사용자_ID = 1L;
            String 이메일 = "test@test.com";
            String 예상_새_액세스_토큰 = "new-access-token";
            String 예상_새_리프레시_토큰 = "new-refresh-token";

            RefreshToken refreshToken = RefreshToken.builder()
                    .token(refreshTokenValue)
                    .userId(사용자_ID)
                    .expiryDate(LocalDateTime.now().plusDays(7))
                    .build();

            User user = 사용자는().식별자가(사용자_ID).이메일이(이메일).이다();

            when(refreshTokenRepository.findByToken(refreshTokenValue)).thenReturn(Optional.of(refreshToken));
            when(userRepository.findByIdOrThrow(사용자_ID)).thenReturn(user);
            when(jwtTokenProvider.generateToken(any(CustomUserDetail.class))).thenReturn(예상_새_액세스_토큰);
            when(jwtTokenProvider.generateRefreshToken(any(CustomUserDetail.class))).thenReturn(예상_새_리프레시_토큰);
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(mock(RefreshToken.class));

            // when
            AuthResponse response = userService.refresh(refreshTokenValue);

            // then
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEqualTo(예상_새_액세스_토큰);
            assertThat(response.refreshToken()).isEqualTo(예상_새_리프레시_토큰);
            assertThat(response.userId()).isEqualTo(사용자_ID);
            assertThat(response.email()).isEqualTo(이메일);

            assertThat(refreshToken.isRevoked()).isTrue();

            verify(refreshTokenRepository).save(refreshTokenCaptor.capture());
            RefreshToken 저장된_새_리프레시_토큰 = refreshTokenCaptor.getValue();
            assertThat(저장된_새_리프레시_토큰.getToken()).isEqualTo(예상_새_리프레시_토큰);
            assertThat(저장된_새_리프레시_토큰.getUserId()).isEqualTo(사용자_ID);
            assertThat(저장된_새_리프레시_토큰.getExpiryDate()).isAfter(LocalDateTime.now());
        }

        @Test
        void 존재하지_않는_리프레시_토큰으로_요청하면_예외가_발생한다() {
            // given
            String refreshTokenValue = "invalid-refresh-token";

            when(refreshTokenRepository.findByToken(refreshTokenValue)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.refresh(refreshTokenValue))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_REFRESH_TOKEN);
        }

        @Test
        void 만료된_리프레시_토큰으로_요청하면_예외가_발생한다() {
            // given
            String refreshTokenValue = "expired-refresh-token";

            RefreshToken expiredToken = RefreshToken.builder()
                    .token(refreshTokenValue)
                    .userId(1L)
                    .expiryDate(LocalDateTime.now().minusDays(1))
                    .build();

            when(refreshTokenRepository.findByToken(refreshTokenValue)).thenReturn(Optional.of(expiredToken));

            // when & then
            assertThatThrownBy(() -> userService.refresh(refreshTokenValue))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.REFRESH_TOKEN_NOT_FOUND);

            assertThat(expiredToken.isValid()).isFalse();
            assertThat(expiredToken.isExpired()).isTrue();
        }

        @Test
        void 폐기된_리프레시_토큰으로_요청하면_예외가_발생한다() {
            // given
            String refreshTokenValue = "revoked-refresh-token";

            RefreshToken revokedToken = RefreshToken.builder()
                    .token(refreshTokenValue)
                    .userId(1L)
                    .expiryDate(LocalDateTime.now().plusDays(7))
                    .build();
            revokedToken.revoke();

            when(refreshTokenRepository.findByToken(refreshTokenValue)).thenReturn(Optional.of(revokedToken));

            // when & then
            assertThatThrownBy(() -> userService.refresh(refreshTokenValue))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.REFRESH_TOKEN_NOT_FOUND);

            assertThat(revokedToken.isValid()).isFalse();
            assertThat(revokedToken.isRevoked()).isTrue();
        }
    }

    @Nested
    class 로그아웃_시에 {

        @Test
        void 사용자의_모든_리프레시_토큰을_폐기한다() {
            // given
            Long 사용자_ID = 1L;

            // when
            userService.logout(사용자_ID);

            // then
            verify(refreshTokenRepository).revokeAllByUserId(userIdCaptor.capture());
            assertThat(userIdCaptor.getValue()).isEqualTo(사용자_ID);
        }
    }

    @Nested
    class 닉네임_중복_검사_시 {

        @Test
        void 사용_가능한_닉네임이면_성공한다() {
            // given
            String 새로운_닉네임 = "그루업";
            NicknameDuplicationCheckCommand 닉네임_중복_검사_요청 =
                    닉네임_중복_검사는().닉네임이(새로운_닉네임).이다().toCommand();

            when(userRepository.existsByNickname(새로운_닉네임)).thenReturn(false);

            // when
            userService.checkNicknameDuplication(닉네임_중복_검사_요청);

            // then
            verify(userRepository).existsByNickname(nicknameCaptor.capture());
            assertThat(nicknameCaptor.getValue()).isEqualTo(새로운_닉네임);
        }

        @Test
        void 닉네임이_중복되면_예외가_발생한다() {
            // given
            String 중복된_닉네임 = "그루업";
            NicknameDuplicationCheckCommand 닉네임_중복_검사_요청 =
                    닉네임_중복_검사는().닉네임이(중복된_닉네임).이다().toCommand();

            when(userRepository.existsByNickname(중복된_닉네임)).thenReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.checkNicknameDuplication(닉네임_중복_검사_요청))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
    }
}
