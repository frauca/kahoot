/*
 * Copyright (c) 2020 by Marfeel Solutions (http://www.marfeel.com)
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Marfeel Solutions S.L and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Marfeel Solutions S.L and its
 * suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Marfeel Solutions SL.
 */

package frauca.kahoot.server.game.endpoint;

import frauca.kahoot.server.game.Game;
import frauca.kahoot.server.game.GameService;
import frauca.kahoot.server.game.Player;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class GameRouter {

    private final GameService gameService;

    public GameRouter(GameService gameService) {
        this.gameService = gameService;
    }

    @Bean
    RouterFunction<ServerResponse> gameApi() {
        return RouterFunctions.route()
                .path("/games", builder -> builder
                        .GET("/{id}", accept(MediaType.APPLICATION_JSON), this::game)
                        .GET("/of/{id}", accept(MediaType.APPLICATION_JSON), this::newGame)
                        .GET("", accept(MediaType.APPLICATION_JSON), this::all)
                        .GET("/{id}/next_question", accept(MediaType.APPLICATION_JSON), this::nextQuestion)
                        .POST("/{id}/player/{name}", accept(MediaType.APPLICATION_JSON), this::addPlayer)
                        .POST("/{player}/choose/{answer}", accept(MediaType.APPLICATION_JSON), this::playerChoice)
                )
                .build();
    }

    public Mono<ServerResponse> game(ServerRequest request) {
        long game_id = Objects.requireNonNull(Long.valueOf(request.pathVariable("id")));
        Mono<Game> game = gameService.findById(game_id);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(fromPublisher(game, Game.class));
    }

    public Mono<ServerResponse> newGame(ServerRequest request) {
        long quiz_id = Objects.requireNonNull(Long.valueOf(request.pathVariable("id")));
        Mono<Game> game = gameService.gameFrom(quiz_id);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(fromPublisher(game, Game.class));
    }

    public Mono<ServerResponse> all(ServerRequest request) {
        Flux<Game> games = gameService.all();

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(fromPublisher(games, Game.class));
    }

    public Mono<ServerResponse> addPlayer(ServerRequest request) {
        long gameId = Objects.requireNonNull(Long.valueOf(request.pathVariable("id")));
        String player_name = Objects.requireNonNull(request.pathVariable("name"));
        Mono<Player> player = gameService.addPlayer(gameId, player_name);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(fromPublisher(player, Player.class));
    }

    public Mono<ServerResponse> playerChoice(ServerRequest request) {
        long playerId = Objects.requireNonNull(Long.valueOf(request.pathVariable("player")));
        long answerId = Objects.requireNonNull(Long.valueOf(request.pathVariable("answer")));
        Mono<Game> game = gameService.chose(playerId, answerId);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(fromPublisher(game, Game.class));
    }

    public Mono<ServerResponse> nextQuestion(ServerRequest request) {
        long gameId = Objects.requireNonNull(Long.valueOf(request.pathVariable("id")));
        Mono<Game> game = gameService.nextQuestion(gameId);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(fromPublisher(game, Game.class));
    }
}
