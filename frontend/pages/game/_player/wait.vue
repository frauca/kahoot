<template>
  <wait-for-others :player-id="playerId"></wait-for-others>
</template>

<script>
import WaitForOthers from '../../../components/game/player/WaitForOthers'
export default {
  components: { WaitForOthers },
  data() {
    return {
      playerId: parseInt(this.$route.params.player)
    }
  }
}
</script>

<style scoped></style>
