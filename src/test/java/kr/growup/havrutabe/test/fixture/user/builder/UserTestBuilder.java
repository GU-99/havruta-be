package kr.growup.havrutabe.test.fixture.user.builder;

import kr.growup.havrutabe.user.domain.Provider;
import kr.growup.havrutabe.user.domain.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.test.util.ReflectionTestUtils;

@SuppressWarnings("NonAsciiCharacters")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserTestBuilder {
    private Long id = 1L;
    private String email = "havruta@test.com";
    private String password = "havruta1234!@";
    private String nickname = "하브루타";
    private Provider provider = Provider.LOCAL;

    public static UserTestBuilder 사용자는() {
        return new UserTestBuilder();
    }

    public UserTestBuilder 식별자가(Long 식별자) {
        this.id = 식별자;
        return this;
    }

    public UserTestBuilder 비밀번호가(String 비밀번호) {
        this.password = 비밀번호;
        return this;
    }

    public UserTestBuilder 이메일이(String 이메일) {
        this.email = 이메일;
        return this;
    }

    public UserTestBuilder 닉네임이(String 닉네임) {
        this.nickname = 닉네임;
        return this;
    }

    public UserTestBuilder 인증_프로바이더가(Provider 인증_프로바이더) {
        this.provider = 인증_프로바이더;
        return this;
    }


    public User 이다() {
        var user = User.builder()
                .email(email)
                .password(password)
                .provider(provider)
                .nickname(nickname)

                .build();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}
