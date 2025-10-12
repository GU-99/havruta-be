package kr.growup.havrutabe.user.service.dto;

import kr.growup.havrutabe.user.domain.Provider;
import kr.growup.havrutabe.user.domain.User;
import lombok.Builder;

@Builder
public record UserSignupCommand(
        String email,
        String password,
        String nickname
        // String verificationCode
) {
    public User toEntity() {
        return User.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .provider(Provider.LOCAL)
                .build();
    }
}
