<template>
  <b-container fluid>
    <b-row>
      <b-col>REsults</b-col>
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
  name: 'PlayerResult',
  components: { PlayerScores }
}
</script>

<style scoped></style>
