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
        <b-table :items="quizzes" :fields="fields" hover bordered></b-table>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import { BIconPlus } from 'bootstrap-vue'

export default {
  name: 'QuizTable',
  components: {
    BIconPlus
  },
  data() {
    return {
      fields: [
        {
          key: 'title',
          sortable: true
        }
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
    addQuizz() {
      this.$router.push({
        path: '/quiz'
      })
    }
  }
}
</script>

<style scoped></style>
