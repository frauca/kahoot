<template>
  <b-container fluid class="m-0 p-0">
    <b-list-group v-if="players">
      <b-list-group-item v-for="player in players" :key="player.id">
        {{ player.avatar }}-{{ player.result }}
      </b-list-group-item>
    </b-list-group>
  </b-container>
</template>

<script>
export default {
  name: 'PlayerScores',
  computed: {
    players() {
      return this.$store.getters['game/playersByScore']
    }
  }
}
</script>

<style scoped></style>
