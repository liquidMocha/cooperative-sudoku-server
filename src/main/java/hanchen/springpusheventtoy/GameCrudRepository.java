package hanchen.springpusheventtoy;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameCrudRepository extends ReactiveCrudRepository<Game, String> {
}
