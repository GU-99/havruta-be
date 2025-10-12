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
    private String profileImageUrl = "https://google.com/photo.jpg";
    private Boolean isTempPassword = false;

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

    public UserTestBuilder 제공자가(Provider provider) {
        this.provider = provider;
        return this;
    }

    public UserTestBuilder 프로필_이미지가(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        return this;
    }

    public UserTestBuilder 임시_비밀번호_여부가(Boolean isTempPassword) {
        this.isTempPassword = isTempPassword;
        return this;
    }

    public User 이다() {
        User user = User.builder()
                .email(email)
                .password(password)
                .provider(provider)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .isTempPassword(isTempPassword)
                .build();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}
