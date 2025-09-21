package kr.growup.havrutabe.user.service.dto;

import lombok.Builder;

@Builder
public record NicknameDuplicationCheckCommand(String nickname) {
}
