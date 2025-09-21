package kr.growup.havrutabe.user;

import kr.growup.havrutabe.common.exception.BusinessException;
import kr.growup.havrutabe.common.exception.ErrorCode;
import kr.growup.havrutabe.test.annotation.AutoKoreanDisplayName;
import kr.growup.havrutabe.user.controller.dto.request.UserSignupRequest;
import kr.growup.havrutabe.user.domain.User;
import kr.growup.havrutabe.user.repository.UserRepository;
import kr.growup.havrutabe.user.service.UserService;
import kr.growup.havrutabe.user.service.dto.NicknameDuplicationCheckCommand;
import kr.growup.havrutabe.user.service.dto.UserSignupCommand;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static kr.growup.havrutabe.test.fixture.user.builder.NicknameDuplicationCheckRequestTestBuilder.닉네임_중복_검사는;
import static kr.growup.havrutabe.test.fixture.user.builder.UserTestBuilder.사용자는;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static kr.growup.havrutabe.test.fixture.user.builder.UserSignupRequestTestBuilder.가입하는_사용자는;


@AutoKoreanDisplayName
@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Mock
    PasswordEncoder passwordEncoder;


    @Nested
    class 사용자가_회원가입_시에 {

        @Test
        void 성공적으로_계정을_생성한다() {
            // given
            Long 예상_새_사용자_ID = 1L;
            User 새_사용자 = 사용자는().식별자가(예상_새_사용자_ID).이다();
            UserSignupCommand 사용자_생성_요청 = 가입하는_사용자는(새_사용자).이다().toCommand();

            //TODO 이메일 검증 테스트 추가
            when(userRepository.save(any(User.class))).thenReturn(새_사용자);

            // when
            Long 실제_새_사용자_ID = userService.signUp(사용자_생성_요청);

            // then
            assertThat(실제_새_사용자_ID).isEqualTo(예상_새_사용자_ID);
        }

        @Test
        void 중복된_아이디를_사용하면_예외가_발생한다() {
            // given
            User 새_사용자 = 사용자는().이메일이("중복된 이메일").이다();
            UserSignupCommand 사용자_생성_요청 = 가입하는_사용자는(새_사용자).이다().toCommand();
            ////TODO 이메일 검증 테스트 추가
            doThrow(DataIntegrityViolationException.class).when(userRepository).save(any(User.class));

            // when & then
            assertThatThrownBy(() -> userService.signUp(사용자_생성_요청))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    class 닉네임_중복_검사_시 {

        @Test
        void 성공한다() {
            // given
            String 새로운_닉네임 = "그루업";
            NicknameDuplicationCheckCommand 닉네임_중복_검사_요청 = 닉네임_중복_검사는().닉네임이(새로운_닉네임).이다().toCommand();

            when(userRepository.existsByNickname(새로운_닉네임)).thenReturn(false);

            // when
            userService.checkNicknameDuplication(닉네임_중복_검사_요청);

            // then
            verify(userRepository, times(1)).existsByNickname(새로운_닉네임);
        }

        @Test
        void 닉네임_중복_시_예외가_발생한다() {
            // given
            String 새로운_닉네임 = "그루업";
            NicknameDuplicationCheckCommand 닉네임_중복_검사_요청 = 닉네임_중복_검사는().닉네임이(새로운_닉네임).이다().toCommand();

            when(userRepository.existsByNickname(새로운_닉네임)).thenReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.checkNicknameDuplication(닉네임_중복_검사_요청))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
    }
}
