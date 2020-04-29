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

import frauca.kahoot.server.game.state.ChoiceRepository;
import frauca.kahoot.server.game.state.PlayerRepository;
import frauca.kahoot.server.quiz.state.QuizService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalTime;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final ChoiceRepository choiceRepository;
    private final QuizService quizService;

    public PlayerService(PlayerRepository playerRepository, ChoiceRepository choiceRepository, QuizService quizService) {
        this.playerRepository = playerRepository;
        this.choiceRepository = choiceRepository;
        this.quizService = quizService;
    }

    public Mono<Player> findById(Long playerId) {
        return playerRepository.findById(playerId);
    }

    public Mono<Choice> makeChoice(Player player, Roll roll, long answerId) {
        return choiceRepository.save(
                Choice.builder()
                        .answerId(answerId)
                        .playerId(player.getId())
                        .rollId(roll.getId())
                        .player(player)
                        .choiceTime(LocalTime.now())
                        .build()
        );
    }

    public Flux<Player> findByGameId(Long gameId) {
        return playerRepository.findByGameId(gameId);
    }

    public Mono<Player> addPlayer(Long gameId, String playerName) {
        return playerRepository.save(
                playerFrom(gameId, playerName)
        );
    }

    public Mono<Roll> fillChoices(Roll roll) {
        return choiceRepository
                .findByRollId(roll.getId())
                .flatMap(this::fillWitPlayer)
                .flatMap(this::fillWithAnswer)
                .collectList()
                .map(choices ->
                        roll.toBuilder()
                                .choices(choices)
                                .build());
    }

    private Player playerFrom(Long gameId, String name) {
        String avatar = name.substring(0, Math.min(2, name.length())).toUpperCase();
        return Player.builder()
                .name(name)
                .avatar(avatar)
                .gameId(gameId).build();
    }

    private Mono<Choice> fillWitPlayer(Choice choice) {
        return findById(choice.getPlayerId())
                .map(player -> choice.toBuilder()
                        .player(player)
                        .build());
    }

    private Mono<Choice> fillWithAnswer(Choice choice) {
        return quizService.findAnswer(choice.getAnswerId())
                .map(answer -> choice.toBuilder()
                        .answer(answer)
                        .build());
    }
}
