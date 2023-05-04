<script setup lang="ts">

import {computed, onMounted, Ref, ref} from "vue";
import {FleventsEvent} from "@/models/fleventsEvent";
import {RouteLocationRaw, useRouter} from 'vue-router';
import axios, {AxiosError} from "axios"
import {Organization} from "@/models/organization";
import security from "@/service/security";
import {Account} from "@/models/account";
import {useAccountStore} from "@/store/account";
import {storeToRefs} from "pinia";
import eventApi from "@/api/eventsApi";
import {useEventStore} from "@/store/events";
import {load} from "webfontloader";
import {useOrganizationStore} from "@/store/organizations";

const props = defineProps({
  backRoute: {
    required: true,
    type: Object as () => RouteLocationRaw,
  },
  submitRoute: {
    required: true,
    type: Object as () => RouteLocationRaw,
  },
  presetEvent: {
    required: false,
    type: Object as () => FleventsEvent|undefined,
    default: undefined,
  },
});

const router = useRouter()
const selectedOrga = ref();
const files = ref(new Array<any>());
const chips =  ref(new Array<any>());
const imgUrl = ref('');
const tooltip = ref('');
const loading = ref(false);
const fleventsEvent = ref( { ...props.presetEvent } as FleventsEvent ?? {
  name: "",
  description: "",
  location: "",
  image: "",
  startTime: "",
  endTime: "",
} as FleventsEvent);

const eventStore = useEventStore();

const organizationStore = useOrganizationStore();
const { managedOrganizations } = storeToRefs(organizationStore);

const accountStore = useAccountStore();
const { currentAccount: account } = storeToRefs(accountStore);

const imageFile: Ref<Array<File>> = ref([]);

// image
function previewImage(e: any) {
  console.log(imageFile.value.length);
  const file = e.target.files[0];
  imgUrl.value = URL.createObjectURL(file);
}
function resetImage() {
  imgUrl.value = props.presetEvent?.image || '';
}

// email-input
function remove(item: any){
  chips.value.splice(chips.value.indexOf(item), 1)
}

onMounted(async () =>{
  if (props.presetEvent) {
    imgUrl.value = props.presetEvent.image;
    selectedOrga.value = managedOrganizations.value.find(o => o.uuid === props.presetEvent?.organizationPreview.uuid);
  }
});
function getBase64(file : any) {
  return new Promise(function (resolve, reject) {
    let reader = new FileReader();
    reader.onload = function () { resolve(reader.result); };
    reader.onerror = reject;
    reader.readAsDataURL(file);
  });
}
function convertTZ(date : any, tzString: any) {
  return new Date((typeof date === "string" ? new Date(date) : date).toLocaleString("en-US", {timeZone: tzString}));
}

// submit
// TODO: properly post to backend (including image file)
async function submit() {
  if (
    fleventsEvent.value.name === ''
    || fleventsEvent.value.location === ''
    || selectedOrga.value == undefined
  ) {
    tooltip.value = "Es wurden nicht alle erforderlichen Angaben gemacht.";
    return;
  }
  loading.value = true;
  if(imageFile.value.length != 0) {
    const file = imageFile.value[0]
    fleventsEvent.value.image = await getBase64(file) as string;
    console.log(fleventsEvent.value);
  }
  let start = convertTZ(new Date(fleventsEvent.value.startTime as string), "Europe/Berlin");
  let end = convertTZ(new Date(fleventsEvent.value.endTime as string), "Europe/Berlin");
  fleventsEvent.value.startTime = start.toISOString();
  fleventsEvent.value.endTime = end.toISOString();
  console.log(fleventsEvent.value.startTime);
  console.log(fleventsEvent.value.endTime);
  try {
    fleventsEvent.value.uuid = undefined;
    // TODO: Use real mailconfig
    fleventsEvent.value.mailConfig = fleventsEvent.value.mailConfig ?? {};
    const response = await eventApi.create(fleventsEvent.value, selectedOrga.value.uuid);
    await router.push(props.submitRoute);
    eventStore.hydrate();
  } catch (e) {
    console.log(e);
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
  } finally {
    loading.value = false;
  }
}
</script>

<template>
<div>
  <v-card :loading="loading" :disabled="loading">
    <v-img
      cover
      height="200"
      :src="imgUrl"
      class="bg-gradient"
    />
    <v-form
      validate-on="submit"
      @submit.prevent="submit()"
    >
      <v-container class="d-flex flex-column gap-3">

        <v-select
          label="Organisation"
          hide-details="auto"
          v-model="selectedOrga"
          :items="managedOrganizations"
          :item-title="item => item.name"
          :item-value="item => item.uuid"
          :rules="[() => selectedOrga !== undefined || 'Events müssen einer Organisation zugehören.']"
          menu-icon="mdi-chevron-down"
          return-object
        />

        <v-text-field
          label="Eventname"
          v-model="fleventsEvent.name"
          :rules="[() => fleventsEvent.name !== '' || 'Events müssen einen Namen haben.']"
          hide-details="auto"
          required
        />

        <v-text-field
          label="Ort"
          prepend-inner-icon="mdi-map-marker"
          v-model="fleventsEvent.location"
          :rules="[() => fleventsEvent.location !== '' || 'Events müssen einen Ort haben.']"
          hide-details="auto"
          required
        />

        <div class="d-flex flex-column flex-sm-row justify-end gap">
          <v-text-field
            label="Startzeit"
            type="datetime-local"
            v-model="fleventsEvent.startTime"
            :rules="[() => fleventsEvent.startTime !== '' || 'Events müssen ein Startdatum haben.']"
            hide-details="auto"
            required
          />
          <v-text-field
            label="Endzeit"
            type="datetime-local"
            v-model="fleventsEvent.endTime"
            :rules="[() => fleventsEvent.endTime !== '' || 'Events müssen ein Enddatum haben.']"
            hide-details="auto"
            required
          />
        </div>

        <v-textarea
          name="input-7-1"
          label="Beschreibung"
          hide-details="auto"
          no-resize
          v-model="fleventsEvent.description"
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
      <v-divider />
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
          Event erstellen
        </v-btn>
      </v-container>
    </v-form>
  </v-card>
</div>
</template>

<style scoped>

</style>
