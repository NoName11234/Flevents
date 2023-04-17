<template>
  <Heading
    text="Verwalten"
    subtext="Verwalten Sie Events und Organisationen in Ihrer Verantwortung."
    icon="mdi-pencil"
  />
  <div class="d-flex flex-column flex-sm-row align-stretch align-sm-center justify-start flex-fill gap">
    <v-btn
      v-if="showCreateButton"
      @click="$router.push({ name: 'events.create' })"
      prepend-icon="mdi-calendar-plus"
      variant="tonal"
      color="primary"
    >
      Neues Event
    </v-btn>
    <v-btn
      v-if="managedOrganizations.length > 0"
      prepend-icon="mdi-account-group"
      append-icon="mdi-chevron-down"
      variant="text"
    >
      Organisationsverwaltung

      <v-menu activator="parent">
        <v-list>
          <v-list-item
            v-for="(item, index) in managedOrganizations"
            :key="index"
            :value="index"
            :title="item.name"
            :to="{ name: 'organizations.organization', params: { uuid: item.uuid } }"
            :prepend-avatar="item.icon"
          />
        </v-list>
      </v-menu>
    </v-btn>
  </div>
  <!--  TODO: Filtermöglichkeiten (Organisation, Editierbar, Bin ich Tutor?, Nehme ich dran teil?, Sortierung, Datumsfilterung, Suche?) Liste aller Events mit Bearbeitungsmöglichkeiten (die ersten 20 oder so)-->
  <CardBanner
    v-show="error"
    message="Error: Es konnten keine Events abgerufen werden."
  />
  <ContentLoadingIndicator
    :loading="loading"
    :text="'Lade Events...'"
  />
  <Suspense v-if="events.length !== 0">
    <EventList
      :events="events"
      :show-manage-tools="true"
    />
  </Suspense>

</template>

<script setup lang="ts">
import axios from "axios";
import EventList from "@/layouts/home/EventList.vue";
import {computed, onMounted, Ref, ref} from "vue";
import CardBanner from "@/components/CardBanner.vue";
import Heading from "@/components/Heading.vue";
import {FleventsEvent} from "@/models/fleventsEvent";
import ContentLoadingIndicator from "@/components/ContentLoadingIndicator.vue";
import {Organization} from "@/models/organization";
import security from "@/service/security";
import {Account} from "@/models/account";
import {OrganizationRole} from "@/models/organizationRole";
import {EventRole} from "@/models/eventRole";

const events: Ref<FleventsEvent[]> = ref([]);
const managedOrganizations = ref([] as Organization[]);
const error = ref(false);
const loading = ref(true);
const account = security.getAccount() as Account;

// TODO: API-call durch pinia store ersetzen
const showCreateButton = computed( () => {return validateManaged()});

function validateManaged(){
  for(let i = 0; i < account.organizationPreviews.length; i++){
    if(account.organizationPreviews[i].role == OrganizationRole.admin || account.organizationPreviews[i].role == OrganizationRole.organizer){
      return true;
    }
  }
  return false;
}
onMounted(async () => {
  try {
    const response = await axios.get(`http://localhost:8082/api/accounts/${account.uuid}/managed-events`, {params: {accountId: account.uuid}})
    response.status == 200 ? events.value = response.data : error.value = true;
    response.status == 200 ? loading.value = false : -1;
  } catch (e) {
    loading.value = false;
    error.value = true;
    console.error("Failed to fetch managed events.");
  }
  try {
    let resp = await axios.get(`http://localhost:8082/api/accounts/${account.uuid}/managed-organizations`);
    managedOrganizations.value = resp.data;
  } catch (e) {
    console.error("Failed to fetch managed organizations.");
  }
})

</script>

<style scoped>

</style>
