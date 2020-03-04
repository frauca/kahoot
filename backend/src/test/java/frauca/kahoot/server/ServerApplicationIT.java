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

import frauca.kahoot.server.quiz.Quiz;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static frauca.kahoot.server.quiz.QuizSamples.aCompleteQuiz;
import static frauca.kahoot.server.quiz.QuizSamples.aQuiz;

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

        webTestClient.get().uri("/quizzes")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$..title").isNotEmpty()
                .jsonPath("$.[0].title").isEqualTo(aQuiz().getTitle())
                .jsonPath("$.[0].id").value((id) -> {
            webTestClient.delete().uri("/quizzes/" + id)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk();
        });


    }
}
