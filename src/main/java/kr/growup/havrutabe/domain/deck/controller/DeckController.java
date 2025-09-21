package kr.growup.havrutabe.domain.deck.controller;

import kr.growup.havrutabe.domain.deck.dto.DeckCreateRequest;
import kr.growup.havrutabe.domain.deck.service.DeckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class DeckController {

    private final DeckService deckService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/decks")
    public void createDeck(DeckCreateRequest request) {
        deckService.createDeck(request);
    }
}
