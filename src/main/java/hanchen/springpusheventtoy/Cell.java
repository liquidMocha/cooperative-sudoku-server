package hanchen.springpusheventtoy;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Cell {
    int row;
    int column;
    int number;
}
