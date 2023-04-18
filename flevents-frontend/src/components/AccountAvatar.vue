<script setup lang="ts">
import {Account} from "@/models/account";
import ColorService from "@/service/colorService";

const defaultAvatarSize = 80;
const iconSizeMultiplier = 0.625;

defineProps({
  account: {
    required: true,
    type: Object as () => Account,
  },
  size: {
    required: false,
    type: String||Number,
  },
});
</script>

<template>
  <v-avatar
    class="flex-grow-0"
    :color="ColorService.getAvatarColor(account)"
    :size="size || defaultAvatarSize"
  >
    <v-icon
      icon="mdi-account"
      :size="(size || defaultAvatarSize) * iconSizeMultiplier"
    />
  </v-avatar>
</template>

<style scoped>

</style>
