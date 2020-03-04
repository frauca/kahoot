package frauca.kahoot.server.quiz.state;

import frauca.kahoot.server.quiz.Answer;
import frauca.kahoot.server.quiz.Question;
import frauca.kahoot.server.quiz.Quiz;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static frauca.kahoot.server.quiz.QuizSamples.aAnswer;
import static frauca.kahoot.server.quiz.QuizSamples.aQuestion;
import static frauca.kahoot.server.quiz.QuizSamples.aQuiz;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class QuizServiceTest {

    @Mock
    QuestionRepository questionRepository;

    @Mock
    AnswerRepository answerRepository;

    @Mock
    QuizRepository quizRepository;

    @InjectMocks
    QuizService service;

    @BeforeEach
    void setUp() {
        doAnswer(quizWithId()).when(quizRepository).save(any(Quiz.class));
        doAnswer(questionWithId()).when(questionRepository).save(any(Question.class));
        doAnswer(answerWithId()).when(answerRepository).save(any(Answer.class));
    }

    @Test
    void save() {
        Answer[] answers = {
                Answer.builder().answer("With one answer.. must be true").correct(true).build(),
                Answer.builder().answer("Same as previous").correct(true).build()
        };
        Question[] questions = {
                Question.builder().question("easy question").answers(List.of(answers[0])).build(),
                Question.builder().question("easier as is the same").answers(List.of(answers[1])).build()
        };

        Quiz quiz = aQuiz()
                .toBuilder()
                .questions(List.of(questions[0], questions[1])).build();


        service.save(quiz).block();

        verify(quizRepository).save(quiz);
        verify(questionRepository).save(argThat(hasQuestion("easy question")));
        verify(questionRepository).save(argThat(hasQuestion("easier as is the same")));
        verify(answerRepository).save(argThat(hasAnswer("With one answer.. must be true")));
        verify(answerRepository).save(argThat(hasAnswer("Same as previous")));
    }

    @Test
    void findAll(){
        Quiz quiz = aQuiz();
        Quiz quiz2 = aQuiz();

        Question[] questions = {
                aQuestion("First question", quiz.getId()),
                aQuestion("Second question", quiz.getId())
        };
        Answer[] answers = {
                aAnswer("First answer", true, questions[0].getId()),
                aAnswer("Second answer", false, questions[0].getId()),
                aAnswer("First answer", true, questions[1].getId())
        };

        doReturn(Flux.just(quiz,quiz2)).when(quizRepository).findAll();
        doReturn(Flux.just(questions[0])).when(questionRepository).findByQuizId(quiz.getId());
        doReturn(Flux.just(questions[1])).when(questionRepository).findByQuizId(quiz2.getId());
        doReturn(Flux.just(answers[0], answers[1])).when(answerRepository).findByQuestionId(questions[0].getId());
        doReturn(Flux.just(answers[2])).when(answerRepository).findByQuestionId(questions[1].getId());

        StepVerifier.create(service.findAll())
                .assertNext(q->{
                    assertThat(q.getQuestions().size()).isEqualTo(1);
                    assertThat(q.getQuestions().get(0).getQuestion()).isEqualTo("First question");
                })
                .assertNext(q-> {
                    assertThat(q.getQuestions().get(0).getAnswers().size()).isEqualTo(1);
                    assertThat(q.getQuestions().get(0).getAnswers().get(0).getAnswer()).isEqualTo("First answer");
                })
                .verifyComplete();
    }

    @Test
    void findById(){


        Quiz quiz = aQuiz();

        Question[] questions = {
                aQuestion("First question", quiz.getId()),
                aQuestion("Second question", quiz.getId())
        };
        Answer[] answers = {
                aAnswer("First answer", true, questions[0].getId()),
                aAnswer("Second answer", false, questions[0].getId()),
                aAnswer("First answer", true, questions[1].getId())
        };

        doReturn(Mono.just(quiz)).when(quizRepository).findById(quiz.getId());
        doReturn(Flux.just(questions)).when(questionRepository).findByQuizId(quiz.getId());
        doReturn(Flux.just(answers[0], answers[1])).when(answerRepository).findByQuestionId(questions[0].getId());
        doReturn(Flux.just(answers[2])).when(answerRepository).findByQuestionId(questions[1].getId());

        StepVerifier.create(service.findById(quiz.getId()))
                .assertNext(q->{
                    assertThat(q.getQuestions().size()).isEqualTo(2);
                    assertThat(q.getQuestions().get(0).getQuestion()).isEqualTo("First question");
                    assertThat(q.getQuestions().get(1).getAnswers().size()).isEqualTo(1);
                    assertThat(q.getQuestions().get(1).getAnswers().get(0).getAnswer()).isEqualTo("First answer");
                })
                .verifyComplete();

    }

    @Test
    void fillTest() {

        Quiz quiz = aQuiz();

        Question[] questions = {
                aQuestion("First question", quiz.getId()),
                aQuestion("Second question", quiz.getId())
        };
        Answer[] answers = {
                aAnswer("First answer", true, questions[0].getId()),
                aAnswer("Second answer", false, questions[0].getId()),
                aAnswer("First answer", true, questions[1].getId())
        };

        doReturn(Flux.just(questions)).when(questionRepository).findByQuizId(quiz.getId());
        doReturn(Flux.just(answers[0], answers[1])).when(answerRepository).findByQuestionId(questions[0].getId());
        doReturn(Flux.just(answers[2])).when(answerRepository).findByQuestionId(questions[1].getId());

        assertThat(quiz.getQuestions()).isNullOrEmpty();

        Quiz filled = service.fillQuiz(quiz).block();


        assertThat(filled.getQuestions()).hasSize(2);
        assertThat(filled.getQuestions().get(0).getAnswers()).hasSize(2);
        assertThat(filled.getQuestions().get(1).getAnswers()).hasSize(1);
    }


    private org.mockito.stubbing.Answer<Mono<Quiz>> quizWithId() {
        return (InvocationOnMock invocationOnMock) -> {
            Quiz quiz = invocationOnMock.getArgument(0, Quiz.class);
            return Mono.just(
                    quiz.toBuilder().id(1l).build()
            );
        };
    }

    private org.mockito.stubbing.Answer<Mono<Question>> questionWithId() {
        return (InvocationOnMock invocationOnMock) -> {
            Question question = invocationOnMock.getArgument(0, Question.class);
            return Mono.just(
                    question.toBuilder().id(1l).build()
            );
        };
    }

    private org.mockito.stubbing.Answer<Mono<Answer>> answerWithId() {
        return (InvocationOnMock invocationOnMock) -> {
            Answer answer = invocationOnMock.getArgument(0, Answer.class);
            return Mono.just(
                    answer.toBuilder().id(1l).build()
            );
        };
    }

    private ArgumentMatcher<Question> hasQuestion(String question) {
        return (q) -> {
            return q.getQuestion().equals(question);
        };
    }

    private ArgumentMatcher<Answer> hasAnswer(String answer) {
        return (a) -> {
            return a.getAnswer().equals(answer);
        };
    }
}
