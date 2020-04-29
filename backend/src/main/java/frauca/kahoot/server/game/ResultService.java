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

package frauca.kahoot.server.game;

import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

@Service
public class ResultService {


    public Game score(Game game) {
        Game rollsScored = scoreAllRolls(game);
        return scorePlayers(rollsScored);
    }

    int pointsForCorrect() {
        return 10;
    }

    int pointsForSpeed(int pos) {
        return Math.max(3 - pos, 0);
    }

    Game scoreAllRolls(Game game) {
        List<Roll> rolls = new ArrayList<>();
        for (int i = 0; i < game.getRolls().size(); i++) {
            Roll unscored = game.getRolls().get(i);
            List<Choice> withResults = filterAndScore(unscored.getChoices());
            rolls.add(setResultEachChoice(unscored, withResults));
        }
        return game.toBuilder()
                .rolls(rolls)
                .build();
    }

    Game scorePlayers(Game game) {
        List<Player> scoredPlayers = game.getPlayers().stream()
                .map(player -> scorePlayer(player, game.getRolls())
                ).collect(Collectors.toUnmodifiableList());

        return game.toBuilder()
                .players(scoredPlayers)
                .build();

    }

    Player scorePlayer(Player player, List<Roll> rolls) {
        int score = rolls.stream()
                .flatMap(roll -> roll.getChoices().stream())
                .filter(choice -> choice.getPlayerId().equals(player.getId()))
                .map(Choice::getResult)
                .reduce(0, Integer::sum);

        return player.toBuilder()
                .result(score)
                .build();
    }

    Choice score(Choice choice, int pos) {
        @NonNull Boolean correct = choice.getAnswer().getCorrect();
        int result = pointsForCorrect() + pointsForSpeed(pos);
        result *= correct ? 1 : 0;
        return choice.toBuilder()
                .result(result)
                .build();
    }

    List<Choice> filterAndScore(List<Choice> choices) {
        List<Choice> filtered = filter(choices).collect(Collectors.toUnmodifiableList());
        return score(filtered);
    }

    List<Choice> score(List<Choice> choices) {
        List<Choice> choiceWithResult = new ArrayList<>();
        for (int i = 0; i < choices.size(); i++) {
            Choice choice = choices.get(i);
            choiceWithResult.add(score(choice, i));
        }
        return choiceWithResult;
    }

    Stream<Choice> filter(List<Choice> choices) {
        return oneByPlayer(
                orderByDate(choices.stream())
        );
    }

    Stream<Choice> orderByDate(Stream<Choice> choices) {
        return choices
                .filter(choice -> choice.getChoiceTime() != null)
                .sorted(Comparator.comparingInt(
                        choice -> choice.getChoiceTime().toSecondOfDay())
                );
    }


    Stream<Choice> oneByPlayer(Stream<Choice> choices) {
        return choices
                .filter(choice ->
                        choice.getPlayer() != null && choice.getPlayer().getId() != null
                )
                .collect(
                        groupingBy(choice -> choice.getPlayer().getId())
                ).values().stream()
                .map(samePlayerChoices -> samePlayerChoices.get(0));
    }


    Roll setResultEachChoice(Roll unscored, List<Choice> withResults) {
        List<Choice> scored = new ArrayList<>();
        for (Choice toFind : unscored.getChoices()) {
            Optional<Choice> founded = Optional.empty();
            for (Choice withResult : withResults) {
                if (withResult.getId().equals(toFind.getId())) {
                    founded = Optional.of(withResult);
                }
            }
            scored.add(founded.orElse(toFind));
        }
        return unscored.toBuilder()
                .choices(scored)
                .build();
    }
}
