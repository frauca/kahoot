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

import frauca.kahoot.server.game.error.CouldNotFindQuestion;
import frauca.kahoot.server.game.state.ChoiceRepository;
import frauca.kahoot.server.game.state.RollRepository;
import frauca.kahoot.server.quiz.Question;
import frauca.kahoot.server.quiz.state.QuizService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Service
class RollService {
    static final Duration TIME_TO_ANSWER = Duration.ofSeconds(15);

    private final RollRepository roundRepository;
    private final ChoiceRepository choiceRepository;
    private final QuizService quizService;

    RollService(RollRepository roundRepository, ChoiceRepository choiceRepository, QuizService quizService) {
        this.roundRepository = roundRepository;
        this.choiceRepository = choiceRepository;
        this.quizService = quizService;
    }


    Mono<Roll> startRoll(Roll roll) {
        LocalTime start = LocalTime.now();
        LocalTime end = start.plus(TIME_TO_ANSWER);
        return roundRepository.save(
                roll.toBuilder()
                        .startTime(start)
                        .endTime(end)
                        .build()
        );
    }

    Flux<Roll> rollsFrom(Game game, List<Question> questions) {
        return Flux.fromIterable(questions)
                .map(question -> this.rollFrom(game, question))
                .flatMap(roundRepository::save);
    }

    Integer nextRoll(Game game) {
        Integer nextRoll = game.getCurrentRoll() + 1;
        Integer maxPossible = game.getRolls().size();
        if (nextRoll < 0 || nextRoll >= maxPossible) {
            throw new CouldNotFindQuestion(
                    String.format("%s could not be next round, there are only %s questions",
                            nextRoll, maxPossible)
            );
        }
        return nextRoll;
    }

    Flux<Roll> rollOfGame(Game game) {
        return roundRepository.findByGameId(game.getId())
                .flatMap(this::fillWithQuestion);
    }

    Mono<Roll> fillWithQuestion(Roll round){
        return quizService.findQuestionById(round.questionId)
                .map(question -> round.toBuilder()
                        .question(question)
                        .questionId(question.getId())
                        .build());
    }

    private Roll rollFrom(Game game, Question question) {
        return Roll.builder()
                .gameId(game.getId())
                .game(game)
                .question(question)
                .questionId(question.getId())
                .build();
    }
}
