<script setup lang="ts">
import {computed, ref} from "vue";
import {useAccountStore} from "@/store/account";
import {storeToRefs} from "pinia";
import {EventListFilterOptions} from "@/models/EventListFilterOptions";
import {EventRole} from "@/models/eventRole";

const emits = defineEmits<{
  (e: 'update', value: EventListFilterOptions): void
}>();

const accountStore = useAccountStore();
const { currentAccount: account } = storeToRefs(accountStore);

const organizations = computed(() => account.value?.organizationPreviews ?? []);
const roles = ref([EventRole.attendee, EventRole.tutor, EventRole.organizer]);

const showFilterDialog = ref(false);
const searchLoading = ref(false);
let loadTimeout = setTimeout(() => {}, 0);

const filterOptions = ref({
  queryText: '',
} as EventListFilterOptions);

const dialogFilterOptions = ref({} as EventListFilterOptions);

const filtersActive = computed(() =>
  filterOptions.value.eventRoles !== undefined && filterOptions.value.eventRoles.length > 0
  || filterOptions.value.organizationUuids !== undefined && filterOptions.value.organizationUuids.length > 0
  || filterOptions.value.fromDate !== undefined
  || filterOptions.value.toDate !== undefined
  || filterOptions.value.dateAscending !== undefined
);

async function applySearch() {
  clearTimeout(loadTimeout);
  searchLoading.value = true;
  emits.call(emits,'update', filterOptions.value);
  loadTimeout = setTimeout(() => searchLoading.value = false, 500);
}

async function openFilters() {
  dialogFilterOptions.value = { ...filterOptions.value } as EventListFilterOptions;
  showFilterDialog.value = true;
}

async function abortFilters() {
  dialogFilterOptions.value = {} as EventListFilterOptions;
  showFilterDialog.value = false;
}

async function clearFilters() {
  filterOptions.value = {
    queryText: filterOptions.value.queryText
  };
  emits.call(emits,'update', filterOptions.value);
}

async function applyFilters() {
  filterOptions.value = { ...dialogFilterOptions.value };
  emits.call(emits,'update', filterOptions.value);
  showFilterDialog.value = false;
}

</script>

<template>
  <div class="d-flex flex-column gap">
    <v-text-field
      :loading="searchLoading"
      v-model="filterOptions.queryText"
      density="compact"
      variant="solo"
      label="Suche"
      append-inner-icon="mdi-magnify"
      single-line
      hide-details
      @input="applySearch"
    />
    <v-btn-group
      density="compact"
      variant="text"
      divided
    >

      <v-btn
        class="flex-grow-1 flex-sm-grow-0"
        prepend-icon="mdi-filter"
        @click="openFilters"
      >
        Filter
      </v-btn>

      <v-btn
        v-if="filtersActive"
        size="small"
        @click="clearFilters"
      >
        <v-icon
          icon="mdi-close"
          size="large"
        />
      </v-btn>

    </v-btn-group>
  </div>

  <v-dialog
    v-model="showFilterDialog"
    persistent
    width="100vw"
    max-width="770px"
  >
    <v-card>
      <v-card-title>
        Filter
      </v-card-title>

      <v-divider />

      <v-container class="d-flex flex-column gap-3">

        <v-select
          v-if="organizations.length > 0"
          v-model="dialogFilterOptions.organizationUuids"
          :items="organizations"
          :item-title="o => o.name"
          :item-value="o => o.uuid"
          menu-icon="mdi-chevron-down"
          prepend-inner-icon="mdi-account-group"
          label="Organisationen"
          hide-details="auto"
          multiple
          chips
        />

        <v-select
          v-model="dialogFilterOptions.eventRoles"
          :items="roles"
          :item-title="o => o.toString().toUpperCase()"
          :item-value="o => o"
          menu-icon="mdi-chevron-down"
          prepend-inner-icon="mdi-account-tie"
          label="Eventrollen"
          hide-details="auto"
          multiple
          chips
        />

        <v-text-field
          label="Events ab"
          type="datetime-local"
          v-model="dialogFilterOptions.fromDate"
          prepend-inner-icon="mdi-calendar-start"
          hide-details="auto"
          required
        />

        <v-text-field
          label="Events bis"
          type="datetime-local"
          v-model="dialogFilterOptions.toDate"
          prepend-inner-icon="mdi-calendar-end"
          hide-details="auto"
          required
        />

        <v-select
          v-model="dialogFilterOptions.dateAscending"
          :items="[true, false]"
          :item-title="asc => asc ? 'Datum aufsteigend' : 'Datum absteigend'"
          :item-value="asc => asc"
          menu-icon="mdi-chevron-down"
          :prepend-inner-icon="dialogFilterOptions.dateAscending ? 'mdi-sort-calendar-descending' : 'mdi-sort-calendar-ascending'"
          label="Sortierung"
          hide-details="auto"
        />

      </v-container>

      <v-divider />

      <v-container class="d-flex flex-column flex-sm-row align-stretch align-sm-center justify-end gap">

        <v-btn
          variant="text"
          @click="abortFilters"
        >
          Verwerfen
        </v-btn>

        <v-spacer />

        <v-btn
          variant="tonal"
          prepend-icon="mdi-check"
          color="primary"
          @click="applyFilters"
        >
          Anwenden
        </v-btn>

      </v-container>
    </v-card>
  </v-dialog>
</template>

<style scoped>

</style>
