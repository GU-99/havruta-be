package kr.growup.havrutabe.domain.deck.repository;

import kr.growup.havrutabe.domain.deck.entity.Deck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeckRepository extends JpaRepository<Deck, Long> {
}
