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
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.List;

//Could not be immutable. I have not get the point to make it work with r2dbc
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = Quiz.QuizBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {

    @Id
    @Builder.Default
    Long id = null;
    @NonNull
    String title;
    @Transient
    @Builder.Default
    List<Question> questions = new ArrayList<>();

    @JsonPOJOBuilder(withPrefix = "")
    public static class QuizBuilder {

    }
}
