<template>
  <b-progress :max="max">
    <b-progress-bar :value="value"> {{ value }} s</b-progress-bar>
  </b-progress>
</template>

<script>
export default {
  name: 'CountDown',
  data() {
    return {
      value: 50,
      max: 100,
      polling: null,
      config: false
    }
  },
  beforeDestroy() {
    this.stop()
  },
  beforeMount() {
    this.polling = setInterval(this.oneMoreSecond, 1000)
  },
  methods: {
    oneMoreSecond() {
      if (this.setUpTime()) {
        this.value++
        if (this.value >= this.max) {
          this.stop()
          this.$emit('timeIsUp')
        }
      }
    },
    setUpTime() {
      if (!this.config && this.$store.state.game.game.roll) {
        this.max = this.secondsToAnswer()
        this.value = 0
        this.config = true
      }
      return this.config
    },
    stop() {
      clearInterval(this.polling)
    },
    secondsToAnswer() {
      const now = new Date()
      const todaystr = now.toISOString().substring(0, 11)
      const strtime = this.$store.state.game.game.roll.endTime.substring(0, 8)
      const millisCountDown = Date.parse(todaystr + strtime) - now
      return Math.round(millisCountDown / 1000)
    }
  }
}
</script>

<style scoped></style>
