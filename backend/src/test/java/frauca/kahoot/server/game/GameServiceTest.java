package frauca.kahoot.server.game;

import frauca.kahoot.server.game.state.GameRepository;
import frauca.kahoot.server.quiz.Quiz;
import frauca.kahoot.server.quiz.state.QuizService;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static frauca.kahoot.server.game.GameSamples.aGame;
import static frauca.kahoot.server.game.GameSamples.aPlayer;
import static frauca.kahoot.server.game.GameSamples.aRoll;
import static frauca.kahoot.server.game.GameSamples.aStartedRoll;
import static frauca.kahoot.server.quiz.QuizSamples.aCompleteQuiz;
import static frauca.kahoot.server.quiz.QuizSamples.aQuestion;
import static frauca.kahoot.server.quiz.QuizSamples.aQuiz;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GameServiceTest {

    Quiz quiz;

    @Mock
    QuizService quizService;

    @Mock
    GameRepository gameRepository;

    @Mock
    PlayerService playerService;

    @Mock
    RollService rollService;

    @Mock
    ResultService resultService;

    @InjectMocks
    GameService gameService;

    @BeforeEach
    public void setUp() {
        doAnswer(gameWithId()).when(gameRepository).save(any(Game.class));
        doAnswer(i->i.getArguments()[0]).when(resultService).score(any(Game.class));

        quiz = aQuiz();
        quiz = quiz.toBuilder().questions(List.of(aQuestion("Sample", quiz.getId()))).build();
        doReturn(Mono.just(quiz)).when(quizService).findById(anyLong());

        doReturn(Flux.empty()).when(playerService).findByGameId(anyLong());
        Roll round = aRoll(quiz.getQuestions().get(0));
        doReturn(Flux.just(round)).when(rollService).rollOfGame(any());
    }

    @Test
    public void newGame() {
        doReturn(Flux.empty()).when(rollService).rollsFrom(any(Game.class), anyList());
        Mono<Game> gameFromQuiz = gameService.gameFrom(quiz.getId());

        StepVerifier.create(gameFromQuiz)
                .assertNext(
                        game -> {
                            assertNotNull(game.getId());
                            assertNotNull(game.getQuiz());
                            assertEquals(quiz.getId(), game.getQuiz().getId());
                        }

                ).verifyComplete();
        verify(gameRepository, times(1)).save(any(Game.class));
        verify(rollService, times(1)).rollsFrom(any(), anyList());
    }

    @Test
    public void getGames() {
        Game[] games = {aGame(), aGame()};
        doReturn(Flux.just(games)).when(gameRepository).findAll();

        StepVerifier.create(gameService.all())
                .assertNext(game -> {
                    assertEquals(game.getId(), games[0].getId());
                    assertNotNull(game.getQuiz());
                    assertEquals(quiz.getId(), game.getQuiz().getId());
                })
                .assertNext(game -> {
                    assertEquals(game.getId(), games[1].getId());
                    assertNotNull(game.getQuiz());
                    assertEquals(quiz.getId(), game.getQuiz().getId());
                })
                .verifyComplete();
    }

    @Test
    public void gameById() {
        Game expected = aGame();
        doReturn(Mono.just(expected)).when(gameRepository).findById(anyLong());

        Player[] players = {aPlayer("player1", expected),
                aPlayer("player2", expected)};
        doReturn(Flux.just(players)).when(playerService).findByGameId(eq(expected.getId()));

        StepVerifier.create(gameService.findById(expected.getId()))
                .assertNext(game -> {
                    assertEquals(expected.getId(), game.getId());
                    assertNotNull(game.getQuiz());
                    assertThat(game.getQuizId())
                            .isEqualTo(quiz.getId());
                    assertEquals(2, game.getPlayers().size());
                    assertEquals("player1", game.getPlayers().get(0).getName());
                })
                .verifyComplete();

        verify(quizService, times(1)).findById(anyLong());
        verify(rollService, times(1)).rollOfGame(any(Game.class));
    }

    @Test
    public void addPlayer() {
        Game expected = aGame();
        doReturn(Mono.just(expected)).when(gameRepository).findById(anyLong());

        doReturn(Mono.just(aPlayer("player3", expected))).when(playerService).addPlayer(anyLong(), anyString());

        StepVerifier.create(gameService.addPlayer(expected.getId(), "player3"))
                .assertNext(player -> {
                    Game game = player.getGame();
                    assertThat(game.getId()).isEqualTo(expected.getId());
                    assertThat(game.getPlayers().size()).isEqualTo(1);
                    assertThat(player.getName()).isEqualTo("player3");
                    assertThat(game.getCurrentRoll()).isEqualTo(Game.NOT_STARTED_ROUND);
                })
                .verifyComplete();
    }

    @Test
    public void startGame() {
        Quiz completeQuiz = aCompleteQuiz();
        Roll roll = aStartedRoll(completeQuiz.getQuestions().get(0));
        Game prevNoPlayers = aGame().toBuilder().quiz(completeQuiz).build();
        Player[] players = {
                aPlayer("player1", prevNoPlayers),
                aPlayer("player2", prevNoPlayers)};
        final Game prevQuestion = prevNoPlayers.toBuilder()
                .players(List.of(players))
                .currentRoll(0)
                .rolls(List.of(roll))
                .build();


        doReturn(0).when(rollService).nextRoll(any(Game.class));
        doReturn(Mono.just(prevQuestion)).when(gameRepository).findById(eq(prevQuestion.getId()));
        doReturn(Flux.fromIterable(prevQuestion.getRolls())).when(rollService).rollOfGame(any(Game.class));
        doReturn(Mono.just(prevQuestion.roll())).when(rollService).startRoll(any(Roll.class));

        StepVerifier.create(gameService.nextQuestion(prevQuestion.getId()))
                .assertNext(game -> {
                    assertThat(game.roll().getStartTime()).isNotNull();
                    assertThat(game.roll().getEndTime()).isNotNull();
                    assertThat(game.getCurrentRoll()).isEqualTo(0);

                })
                .verifyComplete();

        verify(rollService, times(1)).rollOfGame(any(Game.class));
        verify(rollService, times(1)).nextRoll(any());
        verify(rollService, times(1)).startRoll(any(Roll.class));
    }

    Answer<Mono<Game>> gameWithId() {
        return (InvocationOnMock invocationOnMock) -> {
            Game game = invocationOnMock.getArgument(0, Game.class);
            return Mono.just(
                    game.toBuilder().id(1L).build()
            );
        };
    }
}
