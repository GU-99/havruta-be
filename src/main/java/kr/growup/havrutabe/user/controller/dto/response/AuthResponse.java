package kr.growup.havrutabe.user.controller.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Long userId,
        String email
) {}
