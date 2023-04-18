<script lang="ts">
import { Account } from '@/models/account'
import AccountAvatar from "@/components/AccountAvatar.vue";
export default {
  name: "AccountInfo",
  components: {AccountAvatar},
  props: {
    account: {
      required: true,
      type: Object as () => Account,
    }
  }
}
</script>

<template>
  <v-card class="pa-5">
  <div class="d-flex flex-row" style="gap: 20px">
      <AccountAvatar :account="account" />
      <div class="d-flex flex-column justify-center align-start flex-grow-1">
        <h2>{{account.firstname}} {{account.lastname}}</h2>
        <span>{{account.email}}</span>
      </div>
  </div>
  </v-card>
</template>

<style scoped>
</style>
