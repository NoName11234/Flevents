<script setup lang="ts">

import {computed, onMounted, Ref, ref} from "vue";
import {useRoute, useRouter} from 'vue-router';
import {AxiosError} from "axios"
import Heading from "@/components/Heading.vue";
import {useOrganizationStore} from "@/store/organizations";
import {storeToRefs} from "pinia";
import {useEventStore} from "@/store/events";
import eventApi from "@/api/eventsApi";
import FileService from "@/service/fileService";
import {FleventsEvent} from "@/models/fleventsEvent";
const router = useRouter()
const route = useRoute();

const eventUuid = route.params.uuid as string;

const selectedOrga = ref();
const imgUrl = ref('');
const tooltip = ref('');

const eventStore = useEventStore();
const storeLoading = computed(() => eventStore.specificLoading.get(eventUuid));
const savedEvent = eventStore.getEventGetter(eventUuid);
const fleventsEvent = ref({} as FleventsEvent);

const organizationStore = useOrganizationStore();
const { managedOrganizations: organizations } = storeToRefs(organizationStore);

const formLoading = ref(false);
const loading = computed(() =>
  formLoading.value
  || storeLoading.value
);

const backRoute = { name: 'events.event', params: { uuid: eventUuid }, query: { tab: 'info' } };

const imageFile: Ref<Array<File>> = ref([]);
// image
function previewImage(e: any) {
  const file = e.target.files[0];
  imgUrl.value = URL.createObjectURL(file);
}
function resetImage() {
  imgUrl.value = savedEvent.value?.image ?? '';
}

onMounted(async () =>{
  selectedOrga.value = savedEvent.value?.organizationPreview;
  imgUrl.value = savedEvent.value?.image == null ? "" : savedEvent.value.image;
})

async function submit(pendingValidation: Promise<any>) {
  tooltip.value = '';
  const validation = await pendingValidation;
  if (validation.valid !== true) {
    tooltip.value = "Es wurden nicht alle erforderlichen Angaben gemacht.";
    return;
  }
  if (!fleventsEvent) {
    tooltip.value = 'Event ist undefined.';
    return;
  }
  formLoading.value = true;
  if (imageFile.value.length > 0) {
    const file = imageFile.value[0];
    fleventsEvent.value.image = await FileService.encodeFile(file);
  }
  try {
    const response = await eventApi.edit(eventUuid, fleventsEvent.value);
    await router.push(backRoute);
  } catch (e) {
    if (e instanceof AxiosError) {
      if (e.code === AxiosError.ERR_BAD_REQUEST) {
        tooltip.value = 'Ungültige Anfrage';
      }
      else if (e.code === AxiosError.ERR_NETWORK) {
        tooltip.value = 'Netzwerkfehler';
      }
    } else {
      tooltip.value = `Unerwarteter Fehler: ${e}`;
    }
  }
  formLoading.value = false;
  eventStore.hydrateSpecific(eventUuid);
}
</script>

<template>

  <Heading text="Event bearbeiten" />

    <v-card :disabled="loading" :loading="loading">
      <v-img
        cover
        height="200"
        :src="imgUrl"
        class="bg-gradient"
      />
      <v-form
        validate-on="submit"
        @submit.prevent="submit"
      >
        <v-container class="d-flex flex-column gap-3">

          <v-select
            label="Organisation"
            hide-details="auto"
            v-model="selectedOrga"
            :items="organizations"
            :item-title="item => item.name"
            :item-value="item => item.uuid"
            messages="Die Organisation existierender Events kann nicht verändert werden."
            menu-icon="mdi-chevron-down"
            return-object
            disabled
          />

          <v-text-field
            label="Eventname"
            :model-value="savedEvent.name"
            @input="(e: Event) => fleventsEvent.name = (e.target as HTMLInputElement).value"
            :rules="[() => fleventsEvent.name !== '' || 'Events müssen einen Namen haben.']"
            hide-details="auto"
            required
          />

          <v-text-field
            label="Ort"
            prepend-inner-icon="mdi-map-marker"
            :model-value="savedEvent.location"
            @input="(e: Event) => fleventsEvent.location = (e.target as HTMLInputElement).value"
            :rules="[() => fleventsEvent.location !== '' || 'Events müssen einen Ort haben.']"
            hide-details="auto"
            required
          />

          <div class="d-flex flex-column flex-sm-row justify-end gap">
            <v-text-field
              label="Startzeit"
              type="datetime-local"
              :model-value="savedEvent.startTime"
              @input="(e: Event) => fleventsEvent.startTime = (e.target as HTMLInputElement).value"
              :rules="[
                v => v !== '' || 'Events müssen Startdatum und -zeit haben.',
                v => new Date(v).getTime() - new Date().getTime() > 0 || 'Startzeit muss in der Zukunft liegen.'
                ]"
              hide-details="auto"
              required
            />
            <v-text-field
              label="Endzeit"
              type="datetime-local"
              :model-value="savedEvent.endTime"
              @input="(e: Event) => fleventsEvent.endTime = (e.target as HTMLInputElement).value"
              :rules="[
                v => v !== '' || 'Events müssen Enddatum und -zeit haben.',
                v => new Date(v).getTime() - new Date(fleventsEvent.startTime ?? savedEvent.startTime).getTime() > 0 || 'Endzeit muss nach Startzeit liegen.'
                ]"
              hide-details="auto"
              required
            />
          </div>

          <v-textarea
            name="input-7-1"
            label="Beschreibung"
            hide-details="auto"
            no-resize
            :model-value="savedEvent.description"
            @input="(e: Event) => fleventsEvent.description = (e.target as HTMLInputElement).value"
          ></v-textarea>

          <v-file-input
            label="Vorschaubild"
            variant="filled"
            prepend-inner-icon="mdi-image"
            hide-details="auto"
            prepend-icon=""
            @change="previewImage"
            @click:clear="resetImage"
            v-model="imageFile"
            accept="image/png, image/jpeg, image/bmp"
          />

          <div
            v-if="tooltip !== ''"
            class="text-error">
            {{tooltip}}
          </div>
        </v-container>
        <v-divider/>
        <v-container class="d-flex flex-column flex-sm-row justify-end gap">
          <v-btn
            variant="text"
            :to="backRoute"
          >
            Verwerfen
          </v-btn>
          <v-btn
            type="submit"
            color="primary"
            prepend-icon="mdi-check"
          >
            Speichern
          </v-btn>
        </v-container>
      </v-form>
    </v-card>

</template>

<style scoped>

</style>
