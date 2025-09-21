package kr.growup.havrutabe.test.fixture.user.builder;

import kr.growup.havrutabe.user.controller.dto.request.UserSignupRequest;
import kr.growup.havrutabe.user.domain.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@SuppressWarnings("NonAsciiCharacters")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSignupRequestTestBuilder {

    private String nickname = "havruta";
    private String password = "havruta1234!@";
    private String email = "brown@example.com";
    private String verificationCode = "123456";

    public static UserSignupRequestTestBuilder 가입하는_사용자는() {
        return new UserSignupRequestTestBuilder();
    }

    public static UserSignupRequestTestBuilder 가입하는_사용자는(User 사용자) {
        var builder = new UserSignupRequestTestBuilder();
        builder.nickname = 사용자.getNickname();
        builder.email = 사용자.getEmail();
        builder.password = 사용자.getPassword();
        return builder;
    }


    public UserSignupRequestTestBuilder 비밀번호가(String 비밀번호) {
        this.password = 비밀번호;
        return this;
    }

    public UserSignupRequestTestBuilder 이메일이(String 이메일) {
        this.email = 이메일;
        return this;
    }

    public UserSignupRequestTestBuilder 닉네임이(String 닉네임) {
        this.nickname = 닉네임;
        return this;
    }


    public UserSignupRequestTestBuilder 인증번호가(String 인증번호) {
        this.verificationCode = 인증번호;
        return this;
    }

    public UserSignupRequest 이다() {
        return UserSignupRequest.builder()
                .password(password)
                .email(email)
                .nickname(nickname)
                .verificationCode(verificationCode)
                .build();
    }
}
