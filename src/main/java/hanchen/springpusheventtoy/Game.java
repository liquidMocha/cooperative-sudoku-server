package hanchen.springpusheventtoy;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

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
}
