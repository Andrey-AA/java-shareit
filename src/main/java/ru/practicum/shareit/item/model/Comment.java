package ru.practicum.shareit.item.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
@Getter
@Setter
@ToString
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @JoinColumn(name = "item_id", nullable = false)
    private Long itemId;

    @JoinColumn(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return id.equals(comment.id) &&
                text.equals(comment.text) &&
                itemId.equals(comment.itemId) &&
                authorId.equals(comment.authorId) &&
                created.equals(comment.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
