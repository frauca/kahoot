<template>
  <b-container fluid>
    <b-row>
      <b-col>Question num {{ playerId }}</b-col>
    </b-row>
  </b-container>
</template>

<script>
export default {
  name: 'PlayerQuestion',
  props: {
    playerId: {
      type: Number,
      required: true
    }
  }
}
</script>

<style scoped></style>
