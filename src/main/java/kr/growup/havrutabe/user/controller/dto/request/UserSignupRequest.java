package kr.growup.havrutabe.user.controller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kr.growup.havrutabe.user.service.dto.UserSignupCommand;
import lombok.Builder;

import static kr.growup.havrutabe.common.constants.RegexConstants.NICKNAME_PATTERN;
import static kr.growup.havrutabe.common.constants.RegexConstants.PASSWORD_PATTERN;

@Builder
public record UserSignupRequest(
        @NotNull
        @Email
        @Size(max = 128)
        String email,

        @NotNull
        @Pattern(regexp = PASSWORD_PATTERN)
        String password,

        @NotNull
        @Pattern(regexp = NICKNAME_PATTERN)
        String nickname

        // @NotNull
        // String verificationCode
) {
    public UserSignupCommand toCommand() {
        return UserSignupCommand.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                // .verificationCode(verificationCode)
                .build();
    }
}
