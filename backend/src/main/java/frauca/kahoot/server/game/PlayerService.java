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

package frauca.kahoot.server.game;

import frauca.kahoot.server.game.state.PlayerRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Mono<Player> findById(Long playerId) {
        return playerRepository.findById(playerId);
    }

    public Flux<Player> findByGameId(Long gameId) {
        return playerRepository.findByGameId(gameId);
    }

    public Mono<Player> addPlayer(Long gameId, String playerName) {
        return playerRepository.save(
                playerFrom(gameId, playerName)
        );
    }

    private Player playerFrom(Long gameId, String name) {
        String avatar = name.substring(0, Math.min(2, name.length())).toUpperCase();
        return Player.builder()
                .name(name)
                .avatar(avatar)
                .gameId(gameId).build();
    }
}
