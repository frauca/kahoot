package frauca.kahoot.server.game;

import frauca.kahoot.server.quiz.Answer;
import frauca.kahoot.server.quiz.Quiz;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static frauca.kahoot.server.game.GameSamples.aGame;
import static frauca.kahoot.server.game.GameSamples.aPlayer;
import static frauca.kahoot.server.quiz.QuizSamples.aCompleteQuiz;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ResultServiceTest {

    @InjectMocks
    ResultService service;

    @Test
    public void oneRollResult() {
        Game game = sampleGame();
        Roll roll = sampleGame().getRolls().get(0);
        Choice player1Choice = answer(
                game.getPlayers().get(0),
                roll.getQuestion().getAnswers().get(0),
                roll,
                1
        );
        Choice player2Choice = answer(
                game.getPlayers().get(1),
                roll.getQuestion().getAnswers().get(0),
                roll,
                2
        );
        Choice player3Choice = answer(
                game.getPlayers().get(2),
                roll.getQuestion().getAnswers().get(1),
                roll,
                0
        );
        Choice player3ChoiceCorrect = answer(
                game.getPlayers().get(2),
                roll.getQuestion().getAnswers().get(0),
                roll,
                2
        );

        List<Choice> ordered = service.orderByDate(List.of(
                player1Choice, player2Choice, player3Choice, player3ChoiceCorrect
        ).stream()).collect(Collectors.toUnmodifiableList());

        List<Choice> results = service.filterAndScore(List.of(
                player1Choice, player2Choice, player3Choice, player3ChoiceCorrect
        ));

        assertThat(results.size()).isEqualTo(3);
    }



    public Choice answer(Player player, Answer answer, Roll roll, int delay) {
        return Choice.builder()
                .answer(answer)
                .player(player)
                .choiceTime(roll.getStartTime().plusSeconds(delay))
                .build();
    }


    public Game sampleGame() {
        Game game = aGame();
        Player[] players = {
                aPlayer("player1", game),
                aPlayer("player2", game),
                aPlayer("player3", game)
        };
        Quiz quiz = aCompleteQuiz();
        List<Roll> rolls = quiz.getQuestions().stream()
                .map(GameSamples::aStartedRoll)
                .collect(Collectors.toList());
        return game.toBuilder()
                .players(asList(players))
                .quiz(quiz)
                .rolls(rolls)
                .build();
    }
}
