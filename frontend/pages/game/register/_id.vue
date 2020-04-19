<template>
  <player-registration></player-registration>
</template>

<script>
import PlayerRegistration from '../../../components/game/player/PlayerRegistration'
export default {
  components: { PlayerRegistration },
  computed: {
    game() {
      return this.$store.state.game.game
    }
  },
  created() {
    this.$store.dispatch('game/byId', this.$route.params.id)
  }
}
</script>
