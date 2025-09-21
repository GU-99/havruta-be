package kr.growup.havrutabe.user.controller;

import jakarta.validation.Valid;
import kr.growup.havrutabe.user.controller.dto.request.NicknameDuplicationCheckRequest;
import kr.growup.havrutabe.user.controller.dto.request.UserSignupRequest;
import kr.growup.havrutabe.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
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
}
