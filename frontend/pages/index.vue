<template>
  <b-container fluid class="m-0 p-0 mt-2">
    <b-row>
      <b-col>
        <quiz-table />
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import QuizTable from '../components/quiz/QuizTable'

export default {
  components: {
    QuizTable
  }
}
</script>
