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

import java.util.List;
import java.util.Random;

public class QuizSamples {

    private static Random rand = new Random();

    public static Quiz aQuiz() {
        return Quiz.builder()
                .id(random())
                .title("The first sample for testing")
                .build();
    }

    public static Question aQuestion(String question, Long quizId) {
        return Question.builder()
                .id(random())
                .quizId(quizId)
                .question(question)
                .build();
    }

    public static Answer aAnswer(String answer, Boolean correct, Long questionId) {
        return Answer.builder()
                .answer(answer)
                .correct(correct)
                .questionId(questionId)
                .build();
    }

    public static Quiz aCompleteQuiz() {
        Quiz quiz = aQuiz();

        return quiz.toBuilder().id(null)
                .questions(List.of(questionWithAnswers(), questionWithAnswers()))
                .build();
    }

    public static Question questionWithAnswers() {
        Question question = aQuestion("sample question", null);
        Answer correct = aAnswer("answer", true, null).toBuilder().id(null).build();
        Answer incorrect = aAnswer("answer 2", false, null).toBuilder().id(null).build();
        return question.toBuilder().id(null).answers(List.of(correct, incorrect)).build();
    }

    static long random() {
        return rand.nextLong();
    }
}
