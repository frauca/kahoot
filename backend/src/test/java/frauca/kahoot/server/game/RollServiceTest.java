package frauca.kahoot.server.game;

import frauca.kahoot.server.game.state.ChoiceRepository;
import frauca.kahoot.server.game.state.RollRepository;
import frauca.kahoot.server.quiz.Question;
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
import static frauca.kahoot.server.game.GameSamples.aRoll;
import static frauca.kahoot.server.game.RollService.TIME_TO_ANSWER;
import static frauca.kahoot.server.quiz.QuizSamples.aQuestion;
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
class RollServiceTest {

    @Mock
    RollRepository roundRepository;

    @Mock
    ChoiceRepository choiceRepository;

    @Mock
    QuizService quizService;

    @InjectMocks
    RollService service;

    @BeforeEach
    public void setUp() {
        doAnswer(roundWithId()).when(roundRepository).save(any(Roll.class));
        doReturn(Mono.empty()).when(quizService).findQuestionById(anyLong());
    }

    @Test
    public void startRound() {

        Question question = aQuestion("sample", 1L);
        Roll round = aRoll(question);

        StepVerifier.create(service.startRoll(round))
                .assertNext(result -> {
                            assertThat(result.getQuestion().getQuestion())
                                    .isEqualTo(question.getQuestion());
                            assertThat(result.getEndTime())
                                    .isEqualTo(result.getStartTime().plus(TIME_TO_ANSWER));
                        }
                ).verifyComplete();
    }

    @Test
    public void createRounds() {
        List<Question> questions = List.of(
                aQuestion("sample", 1L),
                aQuestion("sample2", 1L)
        );
        Game game = aGame();

        StepVerifier.create(service.rollsFrom(game, questions))
                .assertNext(
                        round -> {
                            assertThat(round.getQuestion().getQuestion())
                                    .isEqualTo(questions.get(0).getQuestion());
                            assertThat(round.getQuestionId())
                                    .isEqualTo(questions.get(0).getId());
                            assertThat(round.getGameId()).isEqualTo(game.getId());
                            assertThat(round.getGame()).isSameAs(game);
                        }
                )
                .assertNext(
                        round -> {
                            assertThat(round.getQuestion().getQuestion())
                                    .isEqualTo(questions.get(1).getQuestion());
                        }
                )
                .verifyComplete();

        verify(roundRepository, times(2)).save(any(Roll.class));
    }

    @Test
    public void getRounds() {
        Game game = aGame();
        Roll expected = aRoll(aQuestion("sample", 1L));
        doReturn(Mono.just(expected.getQuestion())).when(quizService).findQuestionById(anyLong());
        doReturn(Flux.just(expected)).when(roundRepository).findByGameId(eq(game.getId()));

        StepVerifier.create(service.rollOfGame(game))
                .assertNext(round -> {
                    assertThat(round.getId())
                            .isSameAs(expected.getId());
                })
                .verifyComplete();

        verify(roundRepository, times(1)).findByGameId(eq(game.getId()));
        verify(quizService, times(1)).findQuestionById(eq(expected.getQuestionId()));
    }

    Answer<Mono<Roll>> roundWithId() {
        return (InvocationOnMock invocationOnMock) -> {
            Roll round = invocationOnMock.getArgument(0, Roll.class);
            return Mono.just(
                    round.toBuilder().id(1L).build()
            );

        };
    }
}
