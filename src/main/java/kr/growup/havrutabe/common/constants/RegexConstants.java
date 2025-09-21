package kr.growup.havrutabe.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RegexConstants {

    public static final String NICKNAME_PATTERN = "^[A-Za-z0-9가-힣]{2,20}$";
    public static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()_+={}|`'<>,.?/:;\\-\\\\\\[\\]\"])[A-Za-z0-9~!@#$%^&*()_+={}|`'<>,.?/:;\\-\\\\\\[\\]\"]{8,16}$";
}
