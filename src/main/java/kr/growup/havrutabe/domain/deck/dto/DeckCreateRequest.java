package kr.growup.havrutabe.domain.deck.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DeckCreateRequest(
        @NotEmpty
        @Size(min = 1, max = 30)
        @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s`~!@#$%^&*()\\-_=+\\[\\]{}|;:'\",.<>/?]+$")
        String name,

        @Size(max = 200)
        String description) {
}
