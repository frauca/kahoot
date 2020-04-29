<template>
  <b-container fluid>
    <b-row>
      <b-col>REsults</b-col>
    </b-row>
    <b-row v-if="hasMoreQuestions">
      <b-col>
        <b-button v-on:click="nextQuestion()" type="submit" variant="primary">
          Next question
        </b-button>
      </b-col>
    </b-row>
    <b-row>
      <b-col>
        <player-scores />
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import PlayerScores from '../question/PlayerScores'
export default {
  name: 'RollMasterResult',
  components: { PlayerScores },
  computed: {
    hasMoreQuestions() {
      return this.game.currentRoll < this.game.rolls.length - 1
    },
    game() {
      return this.$store.state.game.game
    }
  },
  created() {
    this.$store.dispatch('game/byId', this.game.id).then((game) => {
      this.nextRollAviable = true
    })
  },
  methods: {
    nextQuestion() {
      this.$router.push({
        path: '/game/' + this.game.id + '/master/question/'
      })
    }
  }
}
</script>

<style scoped></style>
