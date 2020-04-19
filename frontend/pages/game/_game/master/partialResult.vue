<template>
  <roll-master-result :id="id" />
</template>

<script>
import RollMasterResult from '../../../../components/game/master/RollMasterResult'
export default {
  name: 'PartialResult',
  components: { RollMasterResult },
  data() {
    return {
      id: parseInt(this.$route.params.game)
    }
  }
}
</script>

<style scoped></style>
