export class GamePoller {
  constructor(vue) {
    this.vue = vue
    this.polling = null
  }

  poll(gameId) {
    this.pollAndCallBack(gameId, null)
  }

  playerNextRoll(player) {
    return new Promise((resolve, reject) => {
      this.callOnNextRoll(player.game.id, player.game.currentRoll).then(
        (game) => {
          resolve(player)
        }
      )
    })
  }

  callOnNextRoll(gameId, previousRoll) {
    return new Promise((resolve, reject) => {
      function resolveOnNextRoll(game) {
        if (game.currentRoll > previousRoll) {
          resolve(game)
        }
      }

      this.pollAndCallBack(gameId, resolveOnNextRoll)
    })
  }

  stop() {
    clearInterval(this.polling)
  }

  pollAndCallBack(gameId, callback) {
    this.polling = setInterval(() => {
      this.vue.$store.dispatch('game/byId', gameId).then((game) => {
        if (callback) {
          callback(game)
        }
      })
    }, 3000)
  }
}
