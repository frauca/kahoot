package frauca.kahoot.server.game.endpoint;

import frauca.kahoot.server.game.Game;
import frauca.kahoot.server.game.GameService;
import frauca.kahoot.server.game.Player;
import frauca.kahoot.server.game.Roll;
import frauca.kahoot.server.quiz.Question;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalTime;
import java.util.List;

import static frauca.kahoot.server.game.GameSamples.aGame;
import static frauca.kahoot.server.game.GameSamples.aPlayer;
import static frauca.kahoot.server.quiz.QuizSamples.aQuestion;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@ContextConfiguration(classes = {GameRouter.class})
@WebFluxTest
class GameRouterTest {

    @MockBean
    GameService gameService;

    @Autowired
    WebTestClient webClient;

    @Test
    public void getAGame() {
        Game game = aGame();
        doReturn(Mono.just(game)).when(gameService).gameFrom(anyLong());
        webClient.get()
                .uri("/games/of/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.quiz.id").isEqualTo(game.getQuiz().getId())
                .jsonPath("$.id").isEqualTo(game.getId());

    }

    @Test
    public void listOfGames() {
        Game[] games = {aGame(), aGame()};
        doReturn(Flux.just(games)).when(gameService).all();
        webClient.get()
                .uri("/games")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.[0].quiz.id").isEqualTo(games[0].getQuiz().getId())
                .jsonPath("$.[1].id").isEqualTo(games[1].getId());

    }

    @Test
    public void addPlayer() {
        Game game = aGame();
        Player player = aPlayer("player1", game);
        doReturn(Mono.just(player)).when(gameService).addPlayer(anyLong(), anyString());
        webClient.post()
                .uri("/games/1/player/player1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.game.id").isEqualTo(game.getId())
                .jsonPath("$.name").isEqualTo("player1");

    }

    @Test
    public void getGame() {
        Game game = aGame();
        game = game.toBuilder().players(List.of(aPlayer("player", game))).build();
        doReturn(Mono.just(game)).when(gameService).findById(anyLong());
        webClient.get()
                .uri("/games/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.id").isEqualTo(game.getId())
                .jsonPath("$.players[0].name").isEqualTo("player");
        ;

    }

    @Test
    public void nextQuestion() {
        Question question = aQuestion("Sample question", 1L);
        Roll round = Roll.builder()
                .question(question)
                .startTime(LocalTime.now())
                .endTime(LocalTime.now())
                .build();
        Game game = aGame().toBuilder()
                .id(1L)
                .rolls(List.of(round))
                .currentRoll(0)
                .build();

        doReturn(Mono.just(game)).when(gameService).nextQuestion(eq(1L));

        webClient.get()
                .uri("/games/1/next_question")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.roll.question.question").isEqualTo(question.getQuestion())
                .jsonPath("$.roll.startTime").exists()
                .jsonPath("$.roll.endTime").exists();

    }
}
