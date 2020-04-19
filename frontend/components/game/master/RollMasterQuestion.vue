<template>
  <b-container fluid>
    <b-row v-if="question">
      <b-col>{{ question.question }}</b-col>
    </b-row>
    <b-row>
      <b-col>
        <count-down v-on:timeIsUp="seeTheResults()" />
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import CountDown from '../utils/countDown'

export default {
  name: 'RollMasterQuestion',
  components: { CountDown },
  props: {
    id: {
      type: Number,
      required: true
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
    this.$store.dispatch('game/nextRoll', this.id)
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
