<template>
  <div>You will be redirected to {{ uri }}</div>
</template>

<script>
export default {
  name: 'Goto',
  data() {
    return {
      uri: this.$route.query.uri
    }
  }
}
</script>

<style scoped></style>
