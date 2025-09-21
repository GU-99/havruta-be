package kr.growup.havrutabe.domain.deck.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "decks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Deck {

    @Id
    @GeneratedValue
    @Column(name = "deck_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private boolean isShared;

    private int viewCount;

    private int likeCount;

    private int sharedCount;

    private LocalDateTime sharedAt;

    @Builder
    public Deck(String name, String description, boolean isShared, int viewCount, int likeCount, int sharedCount,
                LocalDateTime sharedAt) {
        this.name = name;
        this.description = description;
        this.isShared = isShared;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.sharedCount = sharedCount;
        this.sharedAt = sharedAt;
    }
}
