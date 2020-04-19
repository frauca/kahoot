package frauca.kahoot.server.game;

import frauca.kahoot.server.game.state.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static frauca.kahoot.server.game.GameSamples.aGame;
import static frauca.kahoot.server.game.GameSamples.aPlayer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PlayerServiceTest {

    @Mock
    PlayerRepository playerRepository;

    @InjectMocks
    PlayerService service;

    @BeforeEach
    public void setUp() {
        Game game = aGame();
        Player player = aPlayer("samplePlayer", game);

        doReturn(Mono.just(player)).when(playerRepository).findById(anyLong());
        doAnswer(playerWithId()).when(playerRepository).save(any(Player.class));
    }

    @Test
    public void findById() {
        StepVerifier.create(service.findById(1L))
                .assertNext(player -> {
                    assertThat(player.getName()).isEqualTo("samplePlayer");
                })
                .verifyComplete();
        verify(playerRepository, times(1)).findById(eq(1L));
    }

    @Test
    public void addPlayer() {
        StepVerifier.create(service.addPlayer(1L, "anotherPlayer"))
                .assertNext(player -> {
                    assertThat(player.getName()).isEqualTo("anotherPlayer");
                    assertThat(player.getAvatar()).isEqualTo("AN");
                })
                .verifyComplete();

        verify(playerRepository, times(1)).save(any(Player.class));
    }

    Answer<Mono<Player>> playerWithId() {
        return (InvocationOnMock invocationOnMock) -> {
            Player player = invocationOnMock.getArgument(0, Player.class);
            return Mono.just(
                    player.toBuilder().id(1L).build()
            );
        };
    }
}
