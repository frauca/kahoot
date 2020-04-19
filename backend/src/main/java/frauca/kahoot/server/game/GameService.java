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

import frauca.kahoot.server.game.state.GameRepository;
import frauca.kahoot.server.quiz.Quiz;
import frauca.kahoot.server.quiz.state.QuizService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final QuizService quizService;
    private final GameRepository gameRepository;
    private final PlayerService playerService;
    private final RollService rollService;

    public GameService(QuizService quizService,
                       GameRepository gameRepository,
                       PlayerService playerService,
                       RollService rollService) {
        this.quizService = quizService;
        this.gameRepository = gameRepository;
        this.playerService = playerService;
        this.rollService = rollService;
    }

    public Mono<Game> gameFrom(Long quiz_id) {
        return quizService.findById(quiz_id)
                .flatMap(this::gameFrom);
    }

    public Flux<Game> all() {
        return gameRepository.findAll()
                .flatMap(this::fillGame);
    }

    public Mono<Game> findById(Long id) {
        return gameRepository.findById(id)
                .flatMap(this::fillGame);
    }

    public Mono<Player> addPlayer(Long gameId, String playerName) {
        return playerService.addPlayer(gameId, playerName)
                .zipWith(
                        findById(gameId),
                        (savedPlayer, game) -> linkGameToPlayer(game, savedPlayer)
                );
    }

    public Mono<Game> nextQuestion(Long gameId) {
        return findById(gameId)
                .flatMap(game -> {
                            Game gameWithNextRoll = game.toBuilder()
                                    .currentRoll(rollService.nextRoll(game))
                                    .build();

                            return rollService.startRoll(gameWithNextRoll.roll())
                                    .map(startedRoll ->
                                            linkRollToGame(gameWithNextRoll, startedRoll)
                                    );
                        }
                )
                .flatMap(gameRepository::save);
    }

    public Mono<Player> findPlayer(Long playerId) {
        return playerService.findById(playerId)
                .flatMap(player -> findById(player.getGameId())
                        .map(game -> player.toBuilder()
                                .game(game)
                                .build()
                        ));
    }

    private Mono<Game> gameFrom(Quiz quiz) {
        return gameRepository.save(
                Game.builder()
                        .quizId(quiz.getId())
                        .quiz(quiz)
                        .build()
        ).flatMap(
                game -> rollService.rollsFrom(game, quiz.getQuestions())
                        .collectList()
                        .map(
                                rounds -> game.toBuilder().rolls(rounds).build()
                        )
        );
    }

    private Mono<Game> fillGame(Game game) {

        return quizService.findById(game.getQuizId())
                .zipWith(
                        playerService.findByGameId(game.getId()).collectList(),
                        (quiz, players) -> game.toBuilder()
                                .quiz(quiz)
                                .quizId(quiz.getId())
                                .players(players)
                                .build()
                ).zipWith(
                        rollService.rollOfGame(game).collectList(),
                        (filled, rounds) -> filled.toBuilder()
                                .rolls(rounds)
                                .build()
                );
    }

    private Player linkGameToPlayer(Game game, Player player) {
        ArrayList<Player> players = new ArrayList<>(game.getPlayers());
        players.add(player);
        Game linkedGame = game.toBuilder().players(players).build();
        Player linkedPlayer = player.toBuilder().game(linkedGame).build();
        return linkedPlayer;
    }

    private Game linkRollToGame(Game game, Roll roll) {
        List<Roll> replacedRoll = game.getRolls().stream()
                .map(current -> {
                    if (current.getId().equals(roll.getId())) {
                        return roll;
                    } else {
                        return current;
                    }
                })
                .collect(Collectors.toList());

        return game.toBuilder()
                .rolls(replacedRoll).build();
    }
}
