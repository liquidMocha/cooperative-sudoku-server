package hanchen.springpusheventtoy;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@AllArgsConstructor
@Data
public class Move {
    int row;
    int column;
    int number;
    Instant timestamp = Instant.now();
}
