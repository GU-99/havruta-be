package kr.growup.havrutabe.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 인증(Authentication)
    AUTHENTICATION_FAILED(UNAUTHORIZED, "AT_001", "인증을 실패했습니다. 아이디와 비밀번호를 확인해 주세요."),
    INVALID_REFRESH_TOKEN(UNAUTHORIZED, "AT_002", "리프레시 토큰이 유효하지 않습니다. 다시 로그인해 주세요."),
    REFRESH_TOKEN_NOT_FOUND(UNAUTHORIZED, "AT_003", "리프레시 토큰이 존재하지 않습니다."),
    EMAIL_SENDING_FAILURE(INTERNAL_SERVER_ERROR, "AT_004", "이메일 전송에 실패했습니다. 잠시 후 다시 시도해 주세요."),
    INVALID_EMAIL_VERIFICATION_CODE(UNAUTHORIZED, "AT_005", "이메일 인증 번호가 일치하지 않습니다. 다시 확인해 주세요."),
    OAUTH2_AUTHENTICATION_FAILED(UNAUTHORIZED, "AT_006", "OAUTH2 인증에 실패했습니다. 요청 정보를 확인해 주세요."),
    INVALID_PROVIDER(UNAUTHORIZED, "AT_007", "OAUTH2 PROVIDER가 유효하지 않습니다. 요청 PROVIDER를 다시 확인해 주세요."),


    // 사용자(User) - US
    USER_NOT_FOUND(NOT_FOUND, "US_001", "해당 사용자를 찾을 수 없습니다. 입력 정보를 확인해 주세요."),
    USER_ALREADY_EXISTS(CONFLICT, "US_002", "이미 등록된 사용자입니다. 다른 정보로 시도해 주세요."),
    INVALID_PASSWORD(BAD_REQUEST, "US_003", "비밀번호가 일치하지 않습니다. 다시 확인해 주세요"),
    NICKNAME_ALREADY_EXISTS(CONFLICT, "US_004", "이미 등록된 닉네임입니다. 다른 정보로 시도해 주세요."),
    EMAIL_ALREADY_EXISTS(CONFLICT, "US_005", "이미 등록된 이메일입니다. 다른 정보로 시도해 주세요.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
