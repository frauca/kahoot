<template>
  <b-form @submit="onSubmit" v-if="quiz">
    <b-form-group
      label="Title"
      label-for="title"
      description="This is the title for the quizz."
    >
      <b-form-input
        id="title"
        v-model="quiz.title"
        type="text"
        required
        placeholder="Quizz title"
      ></b-form-input>
    </b-form-group>
    <b-form-group label="Questions" description="Questions of this quiz">
      <div :key="i" v-for="(q, i) in quiz.questions">
        <b-button v-b-toggle="'question-' + i" variant="primary">{{
          q.question
        }}</b-button>
        <b-collapse
          :id="'question-' + i"
          accordion="acd-questions"
          class="mt-2"
        >
          <b-card>
            <p class="card-text">
              <b-form-group
                :label-for="'question-' + i"
                label="Question"
                description="This is the title for the quizz."
              >
                <b-form-input
                  :id="'question-' + i"
                  v-model="q.question"
                  type="text"
                  placeholder="Question"
                ></b-form-input>
              </b-form-group>
              <b-container class="answer-by-question" fluid>
                <b-row
                  :key="i + '-' + j"
                  v-for="(a, j) in q.answers"
                  v-bind:style="correctBackground(a)"
                  class="text-center"
                >
                  <b-col cols="8">
                    <b-form-input
                      :id="'answer-' + i + '-' + j"
                      v-bind:style="correctBackground(a)"
                      v-model="a.answer"
                      type="text"
                      placeholder="Answer"
                    />
                  </b-col>
                  <b-col cols="2">
                    <b-form-checkbox
                      v-model="a.correct"
                      name="'correct-' + i + '-' + j"
                      switch
                    >
                    </b-form-checkbox>
                  </b-col>
                  <b-col cols="2">
                    <b-button v-on:click="removeAnswer(q, j)">-</b-button>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col>
                    <b-button
                      v-on:click="addAnswer(q)"
                      v-if="moreAnswerAllowed(q)"
                      >Add answer</b-button
                    >
                  </b-col>
                </b-row>
              </b-container>
            </p>
          </b-card>
        </b-collapse>
      </div>
    </b-form-group>
    <b-button v-on:click="addQuestion">add questin</b-button>
    <b-button type="submit" variant="primary">Submit</b-button>
    <b-button type="reset" variant="danger">Reset</b-button>
  </b-form>
</template>

<script>
import _ from 'lodash'

export default {
  name: 'QuizForm',
  data() {
    return {
      quiz: _.cloneDeep(this.$store.state.formQuiz.quiz)
    }
  },
  methods: {
    onSubmit(evt) {
      evt.preventDefault()
      this.$store.dispatch('quizzes/add', this.quiz)
      this.$router.push({
        path: '/'
      })
    },
    addQuestion() {
      if (!this.quiz.questions) {
        this.quiz.questions = []
      }
      this.quiz.questions.push({
        question: 'Write your ' + this.quiz.questions.length + ' question'
      })
    },
    correctBackground(answer) {
      return {
        background: answer.correct ? 'green' : 'red'
      }
    },
    addAnswer(question) {
      if (!question.answers) {
        question.answers = []
      }
      question.answers.push({
        answer: 'Write your ' + question.answers.length + ' answer',
        correct: false
      })
    },
    moreAnswerAllowed(question) {
      return !question.answers || question.answers.length < 4
    },
    removeAnswer(question, j) {
      question.answers.splice(j, 1)
    }
  }
}
</script>

<style scoped></style>
