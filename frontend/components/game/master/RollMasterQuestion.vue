<template>
  <b-container fluid class="m-0 p-0">
    <b-row v-if="question" class="my-2">
      <b-col>{{ question.question }}</b-col>
    </b-row>
    <b-row class="my-2">
      <b-col>
        <count-down v-on:timeIsUp="seeTheResults()" />
      </b-col>
    </b-row>
    <b-row v-if="nextRollAviable">
      <b-col>
        <answers with-answers />
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import CountDown from '../utils/countDown'
import Answers from '../question/Answers'

export default {
  name: 'RollMasterQuestion',
  components: { Answers, CountDown },
  props: {
    id: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      nextRollAviable: false
    }
  },
  computed: {
    question() {
      if (this.game.roll) {
        return this.game.roll.question
      }
      return null
    },
    game() {
      return this.$store.state.game.game
    }
  },
  created() {
    this.$store.dispatch('game/nextRoll', this.id).then((game) => {
      this.nextRollAviable = true
    })
  },
  methods: {
    seeTheResults() {
      this.$router.push({
        path: '/game/' + this.game.id + '/master/partialResult/'
      })
    }
  }
}
</script>

<style scoped></style>
s
