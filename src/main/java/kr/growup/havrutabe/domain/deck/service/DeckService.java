package kr.growup.havrutabe.domain.deck.service;

import jakarta.validation.Valid;
import kr.growup.havrutabe.domain.deck.dto.DeckCreateRequest;
import kr.growup.havrutabe.domain.deck.entity.Deck;
import kr.growup.havrutabe.domain.deck.repository.DeckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeckService {

    private DeckRepository deckRepository;

    public void createDeck(@Valid @RequestBody DeckCreateRequest request) {
        Deck newDeck = Deck.builder()
                .name(request.name())
                .description(request.description())
                .build();
        deckRepository.save(newDeck);
    }
}
