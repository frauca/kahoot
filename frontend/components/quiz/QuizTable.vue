<template>
  <b-container fluid class="m-0 p-0">
    <b-row>
      <b-col>
        <b-button
          v-on:click="addQuizz"
          size="lg"
          class="m-0"
          variant="success"
          block
        >
          <b-icon-plus size="lg"></b-icon-plus> Add Quiz
        </b-button>
      </b-col>
    </b-row>
    <b-row>
      <b-col>
        <b-table :items="quizzes" :fields="tableFields" hover bordered>
          <template v-slot:cell(actions)="row">
            <b-button @click="playQuizz(row.index)" size="sm" class="mr-1">
              <b-icon-play size="is-small"></b-icon-play
            ></b-button>
            <b-button @click="editQuizz(row.index)" size="sm" class="mr-1">
              <b-icon-pencil size="is-small"></b-icon-pencil
            ></b-button>
            <b-button @click="deleteQuizz(row.index)" size="sm">
              <b-icon-dash size="is-small"></b-icon-dash>
            </b-button>
          </template>
        </b-table>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import { BIconPlus, BIconPencil, BIconDash, BIconPlay } from 'bootstrap-vue'

export default {
  name: 'QuizTable',
  components: {
    BIconPlus,
    BIconPencil,
    BIconDash,
    BIconPlay
  },
  data() {
    return {
      tableFields: [
        { key: 'title', sortable: true },
        { key: 'actions', label: 'Actions' }
      ]
    }
  },
  computed: {
    quizzes() {
      return this.$store.state.quizzes.list
    }
  },
  created() {
    this.$store.dispatch('quizzes/all')
  },
  methods: {
    addQuizz(quiz) {
      this.$store.dispatch('formQuiz/select', quiz)
      this.$router.push({
        path: '/quiz'
      })
    },
    deleteQuizz(index) {
      if (index >= 0 && index <= this.$store.state.quizzes.list.length) {
        const toDelete = this.$store.state.quizzes.list[index]
        this.$store.dispatch('quizzes/delete', toDelete)
      }
    },
    editQuizz(index) {
      if (index >= 0 && index <= this.$store.state.quizzes.list.length) {
        const toAdd = this.$store.state.quizzes.list[index]
        this.addQuizz(toAdd)
      }
    },
    playQuizz(index) {
      if (index >= 0 && index <= this.$store.state.quizzes.list.length) {
        const toPlay = this.$store.state.quizzes.list[index]
        this.$router.push({
          path: '/game/new/' + toPlay.id
        })
      }
    }
  }
}
</script>

<style scoped></style>
