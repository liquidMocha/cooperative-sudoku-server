package hanchen.springpusheventtoy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Move {
    int row;
    int column;
    int number;
    Instant timestamp = Instant.now();
}
