<script setup lang="ts">
import {onMounted, Ref, ref} from "vue";
import {Organization} from "@/models/organization";
import {RouteLocationRaw, useRoute, useRouter} from "vue-router";
import axios, {AxiosError} from "axios";
import {useOrganizationStore} from "@/store/organizations";
import organizationsApi from "@/api/organizationsApi";
import {VALIDATION} from "@/constants";
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

const route = useRoute();
const router = useRouter();

const organizationUuid = route.params.uuid as string;

const tooltip = ref("");
const imageFile: Ref<Array<File>> = ref([]);
const loading = ref(false);

const organizationStore = useOrganizationStore()
const organization = organizationStore.getOrganizationGetter(organizationUuid);

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
async function submit(pendingValidation: Promise<any>) {
  loading.value = true;
  const validation = await pendingValidation;
  if (validation?.valid != true) {
    tooltip.value = 'Eine oder mehrere Angaben sind fehlerhaft.';
    loading.value = false;
    return;
  }
  try {
    if (imageFile.value.length != 0) {
      const file = imageFile.value[0]
      organization.value.icon = await getBase64(file) as string;
    }
    const response = organizationsApi.edit(organizationUuid, organization.value);
    await router.push(props.submitRoute);
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
    console.error(e);
  }
  loading.value = false;
  organizationStore.hydrateSpecific(organizationUuid);
}

</script>
<template>
  <v-card :loading="loading" :disabled="loading">
    <v-form
      ref="form"
      validate-on="submit"
      @submit.prevent="submit"
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
          :rules="[
            () => organization.phoneContact !== '' || 'Dieses Feld wird benötigt.',
            () => (organization.phoneContact?.match(VALIDATION.PHONE)?.length ?? 0) > 0 || 'Muss mit +(Ländervorwahl) oder 0 beginnen.'
            ]"
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
