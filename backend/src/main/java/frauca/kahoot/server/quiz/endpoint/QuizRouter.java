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

package frauca.kahoot.server.quiz.endpoint;

import frauca.kahoot.server.quiz.Quiz;
import frauca.kahoot.server.quiz.state.QuizService;
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
public class QuizRouter {

    private final QuizService service;

    public QuizRouter(QuizService service) {
        this.service = service;
    }

    @Bean
    RouterFunction<ServerResponse> quizApi() {
        return RouterFunctions.route()
                .path("/quizzes", builder -> builder
                        .POST("", accept(MediaType.APPLICATION_JSON), this::saveQuiz)
                        .GET("", accept(MediaType.APPLICATION_JSON), this::allQuizzes)
                        .DELETE("/{id}", accept(MediaType.APPLICATION_JSON), this::deleteQuiz))
                .build();
    }

    public Mono<ServerResponse> allQuizzes(ServerRequest reques) {
        Flux<Quiz> quizzes = service.findAll();
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(fromPublisher(quizzes, Quiz.class));
    }


    public Mono<ServerResponse> saveQuiz(ServerRequest request) {
        Mono<Quiz> quiz = request.bodyToMono(Quiz.class)
                .flatMap(service::save);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(fromPublisher(quiz, Quiz.class));
    }

    public Mono<ServerResponse> deleteQuiz(ServerRequest request) {
        long id = Objects.requireNonNull(Long.valueOf(request.pathVariable("id")));
        return service.findById(id)
                .flatMap(service::delete)
                .then(ServerResponse.ok().build());
    }
}
