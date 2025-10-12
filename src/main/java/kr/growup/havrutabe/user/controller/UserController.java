package kr.growup.havrutabe.user.controller;

import jakarta.validation.Valid;
import kr.growup.havrutabe.auth.dto.RefreshTokenRequest;
import kr.growup.havrutabe.common.security.CustomUserDetail;
import kr.growup.havrutabe.user.controller.dto.response.AuthResponse;
import kr.growup.havrutabe.user.controller.dto.request.LoginRequest;
import kr.growup.havrutabe.user.controller.dto.request.NicknameDuplicationCheckRequest;
import kr.growup.havrutabe.user.controller.dto.request.UserSignupRequest;
import kr.growup.havrutabe.user.controller.dto.response.UserResponse;
import kr.growup.havrutabe.user.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Void> signupUser(@Valid @RequestBody UserSignupRequest request) {
        return ResponseEntity.created(URI.create("/api/v1/user/" + userService.signUp(request.toCommand()))).build();
    }

    @PostMapping("/nickname")
    public ResponseEntity<Void> duplicateCheckNickname(@Valid @RequestBody NicknameDuplicationCheckRequest request) {
        userService.checkNicknameDuplication(request.toCommand());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request.toCommand());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = userService.refresh(request.refreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal CustomUserDetail userDetail) {
        userService.logout(userDetail.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUser(@AuthenticationPrincipal CustomUserDetail userDetail) {
        UserResponse response = userService.getUser(userDetail.getId());
        return ResponseEntity.ok(response);
    }
}

