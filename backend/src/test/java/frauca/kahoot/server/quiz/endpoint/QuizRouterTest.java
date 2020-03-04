package frauca.kahoot.server.quiz.endpoint;

import frauca.kahoot.server.quiz.Quiz;
import frauca.kahoot.server.quiz.state.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static frauca.kahoot.server.quiz.QuizSamples.aQuiz;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ContextConfiguration(classes = {QuizRouter.class})
@WebFluxTest
class QuizRouterTests {

    @MockBean
    QuizService service;

    @Autowired
    WebTestClient webClient;

    @BeforeEach
    public void mockRepository() {
        doAnswer(quizWithId()).when(service).save(any(Quiz.class));
    }

    @Test
    public void postAQuiz() {
        webClient.post()
                .uri("/quizzes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(aQuiz()))
                .exchange()
                .expectStatus().is2xxSuccessful();

        verify(service).save(any(Quiz.class));
    }

    @Test
    public void getAQuiz() {
        doReturn(Flux.just(aQuiz())).when(service).findAll();

        webClient.get()
                .uri("/quizzes")
                .exchange()
                .expectStatus().is2xxSuccessful();

        verify(service).findAll();
    }

    @Test
    public void deleteAQuiz() {
        Quiz toDelete = Quiz.builder()
                .title("toDelete").id(1l).build();
        doReturn(Mono.just(toDelete)).when(service).findById(1l);
        doReturn(Mono.empty()).when(service).delete(toDelete);

        webClient.delete()
                .uri("/quizzes/1")
                .exchange()
                .expectStatus().is2xxSuccessful();

        verify(service).findById(1l);
        verify(service).delete(toDelete);
    }

    Answer<Mono<Quiz>> quizWithId() {
        return (invocationOnMock) -> {
            Quiz firstArg = invocationOnMock.getArgument(0, Quiz.class);
            return Mono.just(
                    firstArg.toBuilder()
                            .id(1l).build()
            );
        };
    }

}
