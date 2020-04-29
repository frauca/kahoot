<template>
  <b-container fluid class="m-0 p-0">
    <b-row class="my-2">
      <b-col>
        <count-down v-on:timeIsUp="seeTheResults()" />
      </b-col>
    </b-row>
    <b-row class="my-2">
      <b-col>
        <answers v-on:answer="makeYourChoice" />
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import Answers from '../question/Answers'
import CountDown from '../utils/countDown'

export default {
  name: 'PlayerQuestion',
  components: { CountDown, Answers },
  computed: {
    player() {
      return this.$store.state.game.player
    }
  },
  methods: {
    makeYourChoice(answer) {
      this.$store
        .dispatch('game/chooseAnswer', {
          answer: answer.id,
          player: this.player.id
        })
        .then((game) => {
          this.seeTheResults()
        })
    },
    seeTheResults() {
      this.$router.push({
        path: '/game/' + this.player.id + '/result/'
      })
    }
  }
}
</script>

<style scoped></style>
