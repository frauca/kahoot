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

package frauca.kahoot.server.quiz;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;

@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = Answer.AnswerBuilder.class)
@AllArgsConstructor
@NoArgsConstructor
public class Answer {

    @Id
    @Builder.Default
    Long id = null;
    @NonNull
    String answer;
    @NonNull
    Boolean correct;
    Long questionId;

    @JsonPOJOBuilder(withPrefix = "")
    public static class AnswerBuilder {

    }
}
