package hanchen.springpusheventtoy;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@CrossOrigin(origins = "*")
@RestController
@AllArgsConstructor
public class PushEventController {
    GameCrudRepository gameRepository;
    private final ReactiveMongoTemplate reactiveTemplate;

    @GetMapping(path = "/stream-events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Game> streamFlux() {
        ChangeStreamOptions options = ChangeStreamOptions.builder()
                .returnFullDocumentOnUpdate()
                .build();

        return reactiveTemplate.changeStream(
                "game",
                options,
                Game.class
        ).map(ChangeStreamEvent::getBody);
    }

    @PostMapping("/start")
    public Mono<Game> startGame() {
        return gameRepository.save(Game.builder().build());
    }

    @GetMapping("/{gameId}/moves")
    public Flux<Move> getMoves(@PathVariable String gameId) {
        Aggregation fluxAggregation = newAggregation(match(where("fullDocument._id").is(gameId)));

        ChangeStreamOptions options = ChangeStreamOptions.builder()
                .returnFullDocumentOnUpdate()
                .filter(fluxAggregation)
                .build();

        return reactiveTemplate.changeStream("game", options, Game.class)
                .map(ChangeStreamEvent::getBody)
                .map(game -> game.getMoves().get(game.getMoves().size() - 1));

    }

    @PostMapping("/{gameId}/move")
    public Mono<Game> makeMove(
            @PathVariable String gameId,
            @RequestBody Move move
    ) {
        return gameRepository.findById(gameId).flatMap(game -> {
            //race condition
            List<Move> moves = game.getMoves();
            moves.add(move);
            game.withMoves(moves);
            return gameRepository.save(game);
        });

    }

}
