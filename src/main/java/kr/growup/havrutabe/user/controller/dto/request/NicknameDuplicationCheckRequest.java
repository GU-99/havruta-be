package kr.growup.havrutabe.user.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import kr.growup.havrutabe.user.service.dto.NicknameDuplicationCheckCommand;
import lombok.Builder;

import static kr.growup.havrutabe.common.constants.RegexConstants.NICKNAME_PATTERN;

@Builder
public record NicknameDuplicationCheckRequest(
        @NotNull
        @Pattern(regexp = NICKNAME_PATTERN)
        String nickname
) {

    public NicknameDuplicationCheckCommand toCommand() {
        return NicknameDuplicationCheckCommand.builder()
                .nickname(nickname)
                .build();
    }
}
