<script setup lang="ts">
import {ref} from "vue";

const emits = defineEmits<{
  (e: 'update', value: string): void
}>();

const showFilterDialog = ref(false);
const searchLoading = ref(false);
const searchTerm = ref("");
let loadTimeout = setTimeout(() => {}, 0);
async function search() {
  let term = searchTerm.value.toLowerCase();
  clearTimeout(loadTimeout);
  searchLoading.value = true;
  emits.call(emits,'update', term);
  loadTimeout = setTimeout(() => searchLoading.value = false, 500);
}

// TODO: Filtermöglichkeiten (Organisation, Suche, Datumsauswahl, Sortierung), Liste aller verfügbaren Events (die ersten 20 oder so)

</script>

<template>
  <div class="d-flex flex-column gap">
    <v-text-field
      :loading="searchLoading"
      v-model="searchTerm"
      density="compact"
      variant="solo"
      label="Suche"
      append-inner-icon="mdi-magnify"
      single-line
      hide-details
      @input="search"
    />
    <div class="d-flex flex-column flex-sm-row align-stretch align-sm-center justify-start gap">

      <v-btn
        prepend-icon="mdi-filter"
        variant="text"
        @click="showFilterDialog = true"
      >
        Filter
      </v-btn>
    </div>
  </div>

  <v-dialog
    v-model="showFilterDialog"
    persistent
    width="auto"
  >
    <v-card>
      <v-card-title>
        Filter
      </v-card-title>
      <v-card-text>
        <p>
          In diesem Dialogfenster können im Endprodukt verschiedene Filter ausgewählt werden, um die Events zu filtern.
          Folgende Filter sind geplant:
        </p>
        <v-container>
          <ul>
            <li>Veranstaltungsdatum (Datumsbereich)</li>
            <li>Organisation (wenn der Account mehreren angehört)</li>
            <li>Rolle im Event (Event-Verwalter, Tutor, ...)</li>
            <li>Teilnahmestatus</li>
          </ul>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-btn
          color="primary"
          block
          @click="showFilterDialog = false"
        >
          Schließen
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style scoped>

</style>
