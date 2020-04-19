<template>
  <b-form @submit="onSubmit">
    <b-form-group
      label="Your Name"
      label-for="name"
      description="Name which other will see you. Only letters"
    >
      <b-form-input
        id="name"
        v-model="name"
        type="text"
        required
        placeholder="Player name"
      ></b-form-input>
    </b-form-group>
    <b-button type="submit" variant="primary">Submit</b-button>
    <b-button type="reset" variant="danger">Reset</b-button>
  </b-form>
</template>

<script>
export default {
  name: 'PlayerRegistration',
  data() {
    return {
      name: ''
    }
  },
  methods: {
    onSubmit(evt) {
      evt.preventDefault()
      this.$store.dispatch('game/addPlayer', this.name).then((player) => {
        this.$router.push({
          path: '/game/' + player.id + '/wait/'
        })
      })
    }
  }
}
</script>

<style scoped></style>
