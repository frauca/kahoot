<template>
  <b-container fluid>
    <b-row>
      <b-col
        v-for="(answer, i) in answers"
        :key="i"
        v-bind:class="colors[i]"
        v-on:click="chooseAnswer(answer)"
        cols="6"
        style="height: 25vh"
        class="align-middle"
      >
        {{ answerText(answer) }}
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
export default {
  name: 'Answers',
  props: {
    withAnswers: {
      type: Boolean,
      required: false,
      default: false
    }
  },
  data() {
    return {
      colors: ['bg-primary', 'bg-danger', 'bg-success', 'bg-warning']
    }
  },
  computed: {
    answers() {
      return this.$store.state.game.game.roll.question.answers
    }
  },
  methods: {
    answerText(answer) {
      return this.withAnswers ? answer.answer : ''
    },
    chooseAnswer(answer) {
      if (!this.withAnswers) {
        this.$emit('answer', answer)
      }
    }
  }
}
</script>

<style scoped></style>
