<template>
  <game-creation :id="id"></game-creation>
</template>

<script>
import GameCreation from '../../../components/game/GameCreation'
export default {
  components: { GameCreation },
  data() {
    return {
      id: parseInt(this.$route.params.id)
    }
  }
}
</script>

<style scoped></style>
