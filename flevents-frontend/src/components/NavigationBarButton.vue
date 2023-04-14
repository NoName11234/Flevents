<template>

</template>

<script lang="ts">
export default {
  name: "NavigationBarButton",
  props: {
    icon: {
        type: Object as () => string,
    },
    title: {
        type: Object as () => string,
    },
    route: {
        type: Object as () => string,
    },
  }
}
</script>

<style scoped>

</style>
