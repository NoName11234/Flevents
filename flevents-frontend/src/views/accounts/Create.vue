<template>
  <Heading text="Account anlegen" />
  <CardBanner message="Achtung! Hierbei handelt es sich um eine Entwicklungsversion. Eingegebene Daten werden noch nicht verschlüsselt. Geben Sie deshalb keine sensiblen Daten (Passwörter, E-Mail) ein, wenn sie diese geheimhalten wollen." />
  <AccountForm  back-route="/home" submit-route="/home"/>
</template>

<script lang="ts">
import AccountForm from "@/components/AccountForm.vue";
import Heading from "@/components/Heading.vue";
import CardBanner from "@/components/CardBanner.vue";
export default {
  name: "CreateAccount",
  components: {CardBanner, Heading, AccountForm}
}
</script>

<style scoped>

</style>
