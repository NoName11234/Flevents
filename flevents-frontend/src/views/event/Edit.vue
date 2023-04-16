<script setup lang="ts">

import {onMounted, Ref, ref} from "vue";
import {FleventsEvent} from "@/models/fleventsEvent";
import {useRoute, useRouter} from 'vue-router';
import axios from "axios"
import {Organization} from "@/models/organization";
import security from "@/service/security";
import {Account} from "@/models/account";
import Heading from "@/components/Heading.vue";
const router = useRouter()
const selectedOrga = ref();
const files = ref([new Array<any>()]);
const route = useRoute();
const chips =  ref(new Array<any>());
const imgUrl = ref('');
const tooltip = ref('');
const base64String : any = ref();
const fleventsEvent : Ref<Partial<FleventsEvent>> = ref({
  name: "",
  description: "",
  location: "",
  image: "",
  startTime: "",
  endTime: "",
}) ;
// TODO: Obtain organizations of user from backend
const organizations = ref([] as Organization[]);

const imageFile: Ref<Array<File>> = ref([]);
// image
function previewImage(e: any) {
  const file = e.target.files[0];
  imgUrl.value = URL.createObjectURL(file);
}
function resetImage() {
  imgUrl.value = '';
}

// email-input
function remove(item: any){
  chips.value.splice(chips.value.indexOf(item), 1)
}
function convertTZ(date : any, tzString: any) {
  return new Date((typeof date === "string" ? new Date(date) : date).toLocaleString("en-US", {timeZone: tzString}));
}

onMounted(async () =>{
  organizations.value = (await axios.get("http://localhost:8082/api/organizations")).data;
  fleventsEvent.value = (await axios.get(`http://localhost:8082/api/events/${route.params.uuid }`)).data;
  selectedOrga.value = fleventsEvent.value.organizationPreview;
  let start =   convertTZ(new Date(fleventsEvent.value.startTime as string), "Europe/Berlin");
  let end = convertTZ(new Date(fleventsEvent.value.endTime as string), "Europe/Berlin");

  if(start >= new Date(`${start.getFullYear()}-03-26`) && start <= new Date(`${start.getFullYear()}-10-29`)){
    start.setHours(start.getHours() + 2)
  }else{
    start.setHours(start.getHours() + 1)
  }


  if(end >= new Date(`${end.getFullYear()}-03-26`) && end <= new Date(`${end.getFullYear()}-10-29`)){
    end.setHours(end.getHours() + 2)
  }else{
    end.setHours(end.getHours() + 1)
  }

  fleventsEvent.value.startTime = start.toISOString().replace(":00.000Z", "");

  fleventsEvent.value.endTime = end.toISOString().replace(":00.000Z", "");

  imgUrl.value = fleventsEvent.value.image == null ? "" : fleventsEvent.value.image;
  console.log(fleventsEvent.value);
})

function getBase64(file : any) {
  return new Promise(function (resolve, reject) {
    let reader = new FileReader();
    reader.onload = function () { resolve(reader.result); };
    reader.onerror = reject;
    reader.readAsDataURL(file);
  });
}

// submit
// TODO: properly post to backend (including image file)
async function submit() {
  if(imageFile.value.length != 0) {
    const file = imageFile.value[0]
    fleventsEvent.value.image = await getBase64(file) as string;
    console.log(base64String.value);
  }
  let start =   convertTZ(new Date(fleventsEvent.value.startTime as string), "Europe/Berlin");
  let end = convertTZ(new Date(fleventsEvent.value.endTime as string), "Europe/Berlin");
  fleventsEvent.value.startTime = start.toISOString();
  fleventsEvent.value.endTime = end.toISOString();
  if (
    fleventsEvent.value.name === ''
    || fleventsEvent.value.description === ''
    || selectedOrga.value == undefined
  ) {
    tooltip.value = "Es wurden nicht alle erforderlichen Angaben gemacht.";
    return;
  }
  try {
    console.log(selectedOrga.value.uuid);
    // await axios.post(`http://h3005487.stratoserver.net:8082/api/organizations/${selectedOrga.value.uuid}/create-event`, fleventsEvent.value, {params: {accountId: JSON.parse(document.cookie.split(";")[0].split("=")[1]).uuid}});
    await axios.post(`http://localhost:8082/api/events/${route.params.uuid}`, fleventsEvent.value);
    console.log(fleventsEvent.value);
    await router.push(`/events/${route.params.uuid}`);
  } catch (e) {
    tooltip.value = "Das Event konnte nicht bearbeitet werden.";
  }
}
</script>

<template>

  <Heading text="Event bearbeiten" />

    <v-card>

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
            label="Event als Vorlage verwenden"
            messages="Hier kann später optional ein existierendes Event als Grundlage für das neue Event ausgewählt werden."
            hide-details="auto"
            disabled
          />

          <v-select
            label="Organisation"
            hide-details="auto"
            v-model="selectedOrga"
            :items="organizations"
            :item-title="item => item.name"
            :item-value="item => item.uuid"
            :rules="[() => selectedOrga !== undefined || 'Events müssen einer Organisation zugehören.']"
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
        <v-divider/>
        <v-container class="d-flex flex-column flex-sm-row justify-end gap">
          <v-btn
            variant="text"
            :to="{ name: 'events.event', params: { uuid: route.params.uuid } }"
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
