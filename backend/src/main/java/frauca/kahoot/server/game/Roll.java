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
import frauca.kahoot.server.quiz.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = Roll.RoundBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
public class Roll {
    @Id
    @Builder.Default
    Long id = null;

    Long gameId;
    @Transient
    Game game;

    Long questionId;
    @Transient
    Question question;

    LocalTime startTime;
    LocalTime endTime;

    @Transient
    @Builder.Default
    List<Choice> choices = new ArrayList<>();

    @JsonPOJOBuilder(withPrefix = "")
    public static class RoundBuilder {

    }
}
