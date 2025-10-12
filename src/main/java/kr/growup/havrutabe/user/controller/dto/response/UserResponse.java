package kr.growup.havrutabe.user.controller.dto.response;

import kr.growup.havrutabe.user.domain.Provider;
import kr.growup.havrutabe.user.domain.User;
import lombok.Builder;

@Builder
public record UserResponse(
        Long userId,
        String email,
        String nickname,
        Provider provider,
        String profileImageUrl,
        Boolean isTempPassword
) {

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .provider(user.getProvider())
                .profileImageUrl(user.getProfileImageUrl())
                .isTempPassword(user.getIsTempPassword())
                .build();

    }
}

