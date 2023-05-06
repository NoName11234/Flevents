<template>
  <v-card class="pa-5">
    <div class="d-flex flex-row gap l-gap-huge">
        <v-avatar
          class="flex-grow-0"
          :color="ColorService.getAvatarColor(account)"
          size="80"
        >
          <v-icon icon="mdi-account" size="50"></v-icon>
        </v-avatar>
        <div class="d-flex flex-column justify-center align-start flex-grow-1">
          <h2>{{account.firstname}} {{account.lastname}}</h2>
          <span>{{account.email}}</span>
        </div>
    </div>
  </v-card>
</template>

<script setup lang="ts">
import { Account } from '@/models/account'
import {AccountPreview} from "@/models/accountPreview";
import colorService from "@/service/colorService";
import ColorService from "@/service/colorService";
defineProps({
  account: {
    required: true,
    type: Object as () => Account|AccountPreview,
  }
});
</script>

<style scoped>
.l-gap-huge {
  gap: 20px !important;
}
</style>
