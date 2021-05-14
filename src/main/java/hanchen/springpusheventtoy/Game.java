package hanchen.springpusheventtoy;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@With
@Builder
public class Game {

    @Id
    private String id;

    @Builder.Default
    List<Move> moves = new ArrayList<>();

    @Builder.Default
    Set<Cell> initialCells = new HashSet<>();

    @Builder.Default
    Instant timestamp = Instant.now();
}
