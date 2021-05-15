package hanchen.springpusheventtoy;

import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@CrossOrigin(origins = "*")
@RestController
@AllArgsConstructor
public class PushEventController {
    private final GameCrudRepository gameRepository;
    private final ReactiveMongoTemplate reactiveTemplate;

    @PostMapping("/start")
    public Mono<Game> startGame() {
        Set<Cell> dummyInitialCells = Set.of(
                Cell.builder().row(0).column(6).number(8).build(),
                Cell.builder().row(1).column(4).number(4).build(),
                Cell.builder().row(1).column(6).number(9).build(),
                Cell.builder().row(1).column(7).number(1).build(),
                Cell.builder().row(2).column(0).number(6).build(),
                Cell.builder().row(2).column(2).number(2).build(),
                Cell.builder().row(2).column(4).number(8).build(),
                Cell.builder().row(2).column(7).number(3).build(),
                Cell.builder().row(3).column(3).number(4).build(),
                Cell.builder().row(3).column(6).number(2).build(),
                Cell.builder().row(3).column(7).number(8).build(),
                Cell.builder().row(4).column(0).number(4).build(),
                Cell.builder().row(4).column(1).number(2).build(),
                Cell.builder().row(4).column(3).number(1).build(),
                Cell.builder().row(4).column(4).number(6).build(),
                Cell.builder().row(4).column(5).number(9).build(),
                Cell.builder().row(4).column(7).number(5).build(),
                Cell.builder().row(4).column(8).number(3).build(),
                Cell.builder().row(5).column(1).number(7).build(),
                Cell.builder().row(5).column(2).number(6).build(),
                Cell.builder().row(5).column(5).number(3).build(),
                Cell.builder().row(6).column(1).number(9).build(),
                Cell.builder().row(6).column(4).number(5).build(),
                Cell.builder().row(6).column(6).number(3).build(),
                Cell.builder().row(6).column(8).number(1).build(),
                Cell.builder().row(7).column(1).number(3).build(),
                Cell.builder().row(7).column(2).number(7).build(),
                Cell.builder().row(7).column(4).number(1).build(),
                Cell.builder().row(8).column(2).number(5).build()
        );

        return gameRepository.save(
                Game.builder()
                        .initialCells(dummyInitialCells)
                        .build()
        );
    }

    @GetMapping("/{gameId}")
    public Mono<Game> getGame(@PathVariable String gameId) {
        return gameRepository.findById(gameId);
    }

    @GetMapping("/{gameId}/moves")
    public Flux<Move> getMoves(@PathVariable String gameId) {
        Aggregation fluxAggregation = newAggregation(match(where("fullDocument._id").is(new ObjectId(gameId))));

        ChangeStreamOptions options = ChangeStreamOptions.builder()
                .returnFullDocumentOnUpdate()
                .filter(fluxAggregation)
                .build();

        return reactiveTemplate.changeStream("game", options, Game.class)
                .map(ChangeStreamEvent::getBody)
                .map(game -> game.getMoves().get(game.getMoves().size() - 1));

    }

    @PostMapping("/{gameId}/move")
    public Mono<Game> makeMove(@PathVariable String gameId, @RequestBody Move move) {
        return gameRepository.findById(gameId).flatMap(game -> {
            //race condition
            List<Move> moves = game.getMoves();
            moves.add(move);
            game.withMoves(moves);
            return gameRepository.save(game);
        });

    }

}
