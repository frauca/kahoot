export const state = () => ({
  game: emptyGame(),
  player: emptyPlayer()
})

export const actions = {
  from(context, quizId) {
    return new Promise((resolve, reject) => {
      this.$axios
        .$get('http://localhost:8080/games/of/' + quizId)
        .then((game) => {
          context.commit('select', game)
          resolve(game)
        })
    })
  },
  byId(context, gameId) {
    return new Promise((resolve, reject) => {
      this.$axios.$get('http://localhost:8080/games/' + gameId).then((game) => {
        context.commit('select', game)
        resolve(game)
      })
    })
  },
  addPlayer(context, name) {
    const gameId = context.state.game.id
    return new Promise((resolve, reject) => {
      this.$axios
        .$post('http://localhost:8080/games/' + gameId + '/player/' + name)
        .then((player) => {
          context.commit('selectPlayer', player)
          resolve(player)
        })
    })
  },
  playerById(context, playerId) {
    return new Promise((resolve, reject) => {
      this.$axios
        .$get('http://localhost:8080/players/' + playerId)
        .then((player) => {
          context.commit('selectPlayer', player)
          resolve(player)
        })
    })
  },
  nextRoll(context, gameId) {
    return new Promise((resolve, reject) => {
      this.$axios
        .$get('http://localhost:8080/games/' + gameId + '/next_question/')
        .then((game) => {
          context.commit('select', game)
          resolve(game)
        })
    })
  }
}

export const mutations = {
  select(state, game) {
    state.game = game
  },
  selectPlayer(state, player) {
    state.player = player
    state.game = player.game
  }
}

function emptyGame() {
  return {
    quiz: {},
    players: []
  }
}

function emptyPlayer() {
  return {
    name: ''
  }
}
