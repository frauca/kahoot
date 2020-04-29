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

package frauca.kahoot.server;

import frauca.kahoot.server.game.Game;
import frauca.kahoot.server.quiz.Quiz;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static frauca.kahoot.server.quiz.QuizSamples.aCompleteQuiz;
import static frauca.kahoot.server.quiz.QuizSamples.aQuiz;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServerApplicationIT {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void basicQuizCrudOperations() {
        webTestClient.post().uri("/quizzes")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(aCompleteQuiz()), Quiz.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.title").isNotEmpty()
                .jsonPath("$.questions[0].id").isNotEmpty()
                .jsonPath("$.questions[0].answers[0].id").isNotEmpty()
                .jsonPath("$.title").isEqualTo(aQuiz().getTitle());


        deleteAllQuizzes();

    }

    @Test
    public void startGame() {
        webTestClient.post().uri("/quizzes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(aCompleteQuiz()), Quiz.class)
                .exchange()
                .expectBody()
                .jsonPath("$.id").value(
                quiz_id -> webTestClient.get().uri("/games/of/" + quiz_id)
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON)
                        .expectBody()
                        .jsonPath("$.quiz.id").isEqualTo(quiz_id)
                        .jsonPath("$.id").value(
                                game_id -> {
                                    Long gameId = Long.valueOf(game_id.toString());
                                    add2Players(gameId);
                                    playGame(gameId);
                                }
                        )
        );

        deleteAllQuizzes();

    }

    void add2Players(Long game_id) {
        long player1Id;
        long player2Id;
        webTestClient.post().uri("/games/" + game_id + "/player/player1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.name").isEqualTo("player1")
                .jsonPath("$.game.id").isEqualTo(game_id.toString());
        webTestClient.post().uri("/games/" + game_id + "/player/player2")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.name").isEqualTo("player2");
        webTestClient.get().uri("/games/" + game_id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.players[0].name").isEqualTo("player1")
                .jsonPath("$.players[1].name").isEqualTo("player2");
    }

    void playGame(Long game_id){
        webTestClient.get().uri("/games/" + game_id + "/next_question")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.roll.endTime").exists()
                .jsonPath("$.roll.endTime").exists()
                .jsonPath("$.roll.question").exists();

        webTestClient.get().uri("/games/" + game_id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.rolls[1].endTime").doesNotExist();
        webTestClient.get().uri("/games/" + game_id + "/next_question")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.roll.question").exists();
        webTestClient.get().uri("/games/" + game_id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.rolls[1].endTime").exists();
        webTestClient.get().uri("/games/" + game_id + "/next_question")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    }


    void deleteAllQuizzes() {
        webTestClient.get().uri("/quizzes")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$..title").isNotEmpty()
                .jsonPath("$.[0].title").isEqualTo(aQuiz().getTitle())
                .jsonPath("$.[0].id").value(
                (id) -> webTestClient.delete().uri("/quizzes/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isOk()
        );
    }
}
