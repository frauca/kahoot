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

import frauca.kahoot.server.quiz.Question;
import frauca.kahoot.server.quiz.Quiz;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.TemporalAmount;
import java.util.Random;

import static frauca.kahoot.server.game.RollService.TIME_TO_ANSWER;
import static frauca.kahoot.server.quiz.QuizSamples.aQuiz;

public class GameSamples {

    private static Random rand = new Random();

    public static Game aGame() {
        Quiz quiz = aQuiz();
        return Game.builder()
                .id(random())
                .quizId(quiz.getId())
                .quiz(quiz)
                .build();
    }

    public static Player aPlayer(String name, Game game){
        return Player.builder()
                .id(random())
                .name(name)
                .game(game)
                .gameId(game.getId())
                .build();
    }

    public static Roll aRoll(Question question){
        return Roll.builder()
                .id(random())
                .question(question)
                .questionId(question.getId())
                .build();
    }

    public static Roll aStartedRoll(Question question){
        return aRoll(question).toBuilder()
                .startTime(LocalTime.now())
                .endTime(LocalTime.now().plus(TIME_TO_ANSWER))
                .build();
    }

    static long random() {
        return rand.nextLong();
    }
}
