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

package frauca.kahoot.server.quiz.state;

import frauca.kahoot.server.quiz.Answer;
import frauca.kahoot.server.quiz.Question;
import frauca.kahoot.server.quiz.Quiz;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class QuizService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final QuizRepository quizRepository;

    public QuizService(QuizRepository quizRepository, QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    public Flux<Quiz> findAll() {
        return quizRepository.findAll()
                .flatMap(this::fillQuiz);
    }

    public Mono<Quiz> findById(Long id) {
        return quizRepository.findById(id)
                .flatMap(this::fillQuiz);
    }

    public Mono<Void> delete(Quiz quiz) {
        return quizRepository.delete(quiz);
    }

    public Mono<Quiz> save(Quiz quiz) {
        return quizRepository
                .save(quiz)
                .flatMap(this::saveAllQuestions);
    }

    public Mono<Question> findQuestionById(Long questionId){
        return questionRepository.findById(questionId)
                .flatMap(this::fillQuestion);
    }

    private Mono<Quiz> saveAllQuestions(Quiz savedQuiz) {
        return getQuestionsLinkedToThis(savedQuiz)
                .flatMap(this::save)
                .collectList()
                .map(questions -> savedQuiz.toBuilder().questions(questions).build());
    }

    private Flux<Question> getQuestionsLinkedToThis(Quiz quiz) {
        return Flux.fromIterable(quiz.getQuestions())
                .map(question -> question.toBuilder()
                        .quizId(
                                Objects.requireNonNull(quiz.getId())
                        ).build()
                );
    }

    private Mono<Question> save(Question question) {
        return questionRepository.save(question)
                .flatMap(this::saveAllAnswers);
    }

    private Mono<Question> saveAllAnswers(Question savedQuestion) {
        return getAnswersLinkedToThis(savedQuestion)
                .flatMap(answerRepository::save)
                .collectList()
                .map(answers -> savedQuestion.toBuilder().answers(answers).build());
    }

    private Flux<Answer> getAnswersLinkedToThis(Question question) {
        return Flux.fromIterable(question.getAnswers())
                .map(answer -> answer.toBuilder()
                        .questionId(
                                Objects.requireNonNull(question.getId())
                        ).build()
                );
    }

    Mono<Quiz> fillQuiz(Quiz quiz) {
        return questionRepository.findByQuizId(quiz.getId())
                .flatMap(this::fillQuestion)
                .collectList()
                .map(questions -> quiz.toBuilder()
                        .questions(questions)
                        .build());
    }

    private Mono<Question> fillQuestion(Question question) {
        return answerRepository.findByQuestionId(question.getId())
                .collectList()
                .map(answers -> question.toBuilder()
                        .answers(answers)
                        .build());
    }
}
