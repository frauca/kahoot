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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import frauca.kahoot.server.quiz.Answer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.time.LocalTime;

@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = Choice.ChoiceBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
public class Choice {

    @Id
    @Builder.Default
    Long id = null;

    Long rollId;
    @Transient
    Roll roll;

    Long playerId;
    @Transient
    Player player;

    Long answerId;
    @Transient
    Answer answer;

    @NonNull
    LocalTime choiceTime;

    @Transient
    @Builder.Default
    int result = 0;

    Game getGame(){
        return getPlayer().getGame();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class ChoiceBuilder {

    }
}
