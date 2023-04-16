<script setup lang="ts">
import {onMounted, Ref, ref} from "vue";
import {Organization} from "@/models/organization";
import {RouteLocationRaw, useRoute, useRouter} from "vue-router";
import axios from "axios";
const props = defineProps({
  backRoute: {
    required: true,
    type: Object as () => RouteLocationRaw,
  },
  submitRoute: {
    required: true,
    type: Object as () => RouteLocationRaw,
  },
});

const tooltip = ref("");
const route = useRoute();
const imageFile: Ref<Array<File>> = ref([]);
const router = useRouter();
const organization = ref({
  uuid: '0',
} as Organization);

onMounted(async () => {
  try {
    let response = await axios.get(`http://localhost:8082/api/organizations/${route.params?.uuid}`);
    organization.value = response.data;
  } catch (e) {
    console.error("Failed to load organization.");
  }
})

const initialIcon = organization.value.icon;
function previewImage(e: any) {
  const file = e.target.files[0];
  organization.value.icon = URL.createObjectURL(file);
}
function resetImage() {
  organization.value.icon = initialIcon;
}
function getBase64(file : any) {
  return new Promise(function (resolve, reject) {
    let reader = new FileReader();
    reader.onload = function () { resolve(reader.result); };
    reader.onerror = reject;
    reader.readAsDataURL(file);
  });
}
async function submit() {
  try {
    console.log(imageFile.value);
    if(imageFile.value.length != 0) {
      const file = imageFile.value[0]
      organization.value.icon = await getBase64(file) as string;
    }
    await axios.post(`http://localhost:8082/api/organizations/${organization.value.uuid}`, organization.value);
    await router.push(props.submitRoute);
  } catch (e) {
    tooltip.value = "Speichern fehlgeschlagen.";
    console.error(e);
  }
}

</script>
<template>
  <v-card>
    <v-form
      validate-on="submit"
      @submit.prevent="submit()"
    >
      <v-container class="d-flex flex-column gap-3">
        <v-text-field
          v-model="organization.name"
          label="Name"
          :rules="[() => organization.name !== '' || 'Dieses Feld wird benötigt.']"
          required
          hide-details="auto"
        />
        <v-textarea
          name="input-7-1"
          label="Info-Text"
          hide-details="auto"
          prepend-inner-icon="mdi-text"
          no-resize
          v-model="organization.description"
        ></v-textarea>
        <v-textarea
          name="input-7-1"
          label="Anschrift"
          hide-details="auto"
          prepend-inner-icon="mdi-map-marker"
          no-resize
          v-model="organization.address"
        ></v-textarea>
        <v-text-field
          v-model="organization.phoneContact"
          label="Telefonkontakt"
          prepend-inner-icon="mdi-phone"
          :rules="[() => organization.phoneContact !== '' || 'Dieses Feld wird benötigt.']"
          required
          hide-details="auto"
        />
        <div class="d-flex flex-row gap-3">
          <v-avatar
            :image="organization.icon"
            class="bg-grey-lighten-3"
            size="55"
          />
          <v-file-input
            label="Logo"
            variant="filled"
            prepend-inner-icon="mdi-image"
            hide-details="auto"
            prepend-icon=""
            v-model="imageFile"
            @change="previewImage"
            @click:clear="resetImage"
            accept="image/png, image/jpeg, image/bmp"
          />
        </div>
        <div
          v-if="tooltip !== ''"
          class="text-error"
        >
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
          color="primary"
          type="submit"
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
