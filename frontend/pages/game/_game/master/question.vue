<template>
  <roll-master-question :id="id" />
</template>

<script>
import RollMasterQuestion from '../../../../components/game/master/RollMasterQuestion'
export default {
  components: { RollMasterQuestion },
  data() {
    return {
      id: parseInt(this.$route.params.game)
    }
  }
}
</script>

<style scoped></style>
