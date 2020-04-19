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
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class PlayerRouter {
    private final GameService gameService;

    public PlayerRouter(GameService gameService) {
        this.gameService = gameService;
    }

    @Bean
    RouterFunction<ServerResponse> playerApi() {
        return RouterFunctions.route()
                .path("/players", builder -> builder
                        .GET("/{id}", accept(MediaType.APPLICATION_JSON), this::player)
                )
                .build();
    }

    public Mono<ServerResponse> player(ServerRequest request) {
        long playerId = Objects.requireNonNull(Long.valueOf(request.pathVariable("id")));
        Mono<Player> player = gameService.findPlayer(playerId);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(fromPublisher(player, Player.class));
    }
}
