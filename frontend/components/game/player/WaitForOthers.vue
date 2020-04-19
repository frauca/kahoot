<template>
  <b-container fluid>
    <b-row>
      <b-col>Question num</b-col>
    </b-row>
  </b-container>
</template>

<script>
import { GamePoller } from '~/common/gamePoller'
export default {
  name: 'WaitForOthers',
  props: {
    playerId: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      poller: new GamePoller(this)
    }
  },
  beforeDestroy() {
    this.poller.stop()
  },
  created() {
    this.$store.dispatch('game/playerById', this.playerId).then((player) => {
      this.poller.playerNextRoll(player).then((player) => {})
    })
  }
}
</script>

<style scoped></style>
