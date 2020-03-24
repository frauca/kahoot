<template>
  <b-container fluid>
    <b-row>
      <b-col>
        <nav-bar />
      </b-col>
    </b-row>
    <b-row>
      <b-col>
        <nuxt />
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import NavBar from '@/components/navigation/NavBar'

export default {
  components: {
    NavBar
  }
}
</script>
