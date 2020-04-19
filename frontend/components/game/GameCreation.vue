<template>
  <b-container fluid>
    <b-row>
      <b-col>Go to /game/register/{{ game.id }}</b-col>
    </b-row>
    <b-row>
      <b-col cols="10"
        >Waiting for users to register. When all users are done press
        start</b-col
      >
      <b-col
        ><b-button v-on:click="startGame()" type="submit" variant="primary"
          >Start game</b-button
        ></b-col
      >
    </b-row>
    <b-row>
      <b-col>
        <div class="mb-2">
          <b-avatar
            v-for="player in game.players"
            :key="player.id"
            :text="player.avatar"
          ></b-avatar>
        </div>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import { GamePoller } from '~/common/gamePoller'
export default {
  name: 'GameCreation',
  props: {
    id: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      poller: new GamePoller(this)
    }
  },
  computed: {
    game() {
      return this.$store.state.game.game
    }
  },
  beforeDestroy() {
    this.poller.stop()
  },
  created() {
    this.$store.dispatch('game/from', this.$route.params.id).then((game) => {
      this.poller.poll(game.id)
    })
  },
  methods: {
    startGame() {
      this.$router.push({
        path: '/game/' + this.game.id + '/master/question/'
      })
    }
  }
}
</script>

<style scoped></style>
