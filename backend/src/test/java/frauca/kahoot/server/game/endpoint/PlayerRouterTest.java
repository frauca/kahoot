package frauca.kahoot.server.game.endpoint;

import frauca.kahoot.server.game.Game;
import frauca.kahoot.server.game.GameService;
import frauca.kahoot.server.game.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static frauca.kahoot.server.game.GameSamples.aGame;
import static frauca.kahoot.server.game.GameSamples.aPlayer;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;


@ContextConfiguration(classes = {PlayerRouter.class})
@WebFluxTest
class PlayerRouterTest {

    @MockBean
    GameService gameService;

    @Autowired
    WebTestClient webClient;

    @Test
    public void getPlayer() {
        Game game = aGame();
        Player player = aPlayer("player1",game);
        doReturn(Mono.just(player)).when(gameService).findPlayer(anyLong());
        webClient.get()
                .uri("/players/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.game.id").isEqualTo(game.getId())
                .jsonPath("$.name").isEqualTo("player1");
    }

}
