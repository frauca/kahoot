<template>
  <b-container class="bv-example-row">
    <b-row>
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
          <div key="q.question" v-for="(q, i) in quiz.questions">
            <b-button v-b-toggle="'question-' + i" variant="primary">{{
              q.question
            }}</b-button>
            <b-collapse :id="'question-' + i" class="mt-2">
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
                </p>
              </b-card>
            </b-collapse>
          </div>
        </b-form-group>
        <b-button v-on:click="addQuestion">add questin</b-button>
        <b-button type="submit" variant="primary">Submit</b-button>
        <b-button type="reset" variant="danger">Reset</b-button>
      </b-form>
    </b-row>
  </b-container>
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
    },
    addQuestion() {
      if (!this.quiz.questions) {
        this.quiz.questions = []
      }
      this.quiz.questions.push({
        question: 'Write your question'
      })
    }
  }
}
</script>

<style scoped></style>
