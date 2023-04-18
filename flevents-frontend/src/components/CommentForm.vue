<script setup lang="ts">
import ColorService from "@/service/colorService";
import {Comment} from "@/models/comment";
import {Account} from "@/models/account";
import {onBeforeMount, ref} from "vue";

const props = defineProps({
  account: {
    required: true,
    type: Object as () => Account,
  }
});

const plainComment = {
  author: props.account,
  content: '',
} as Comment;
const comment = ref({ ...plainComment });

const tooltip = ref('');
const loading = ref(true);

onBeforeMount(() => {
  loading.value = false;
});

async function submit() {
  loading.value = true;
  tooltip.value = '';
  if (comment.value.content.length === 0) {
    tooltip.value = 'Es können keine leeren Kommentare gepostet werden.'
    loading.value = false;
    return;
  }
  // TODO
  await new Promise(res => setTimeout(res, 2000));
  // TODO
  comment.value = { ...plainComment };
  loading.value = false;
}

</script>

<template>
  <v-timeline-item
    icon="mdi-account"
    :dot-color="ColorService.getAvatarColor(comment.author)"
    fill-dot
    width="100%"
  >
    <v-card
      elevation="0"
      border
      class="ms-n4"
      :loading="loading"
    >
      <v-textarea
        hide-details="auto"
        v-model="comment.content"
        :disabled="loading"
      >
      </v-textarea>
      <v-container class="d-flex flex-column flex-sm-row justify-end gap align-sm-center pa-2">
        <span
          v-if="tooltip"
          class="text-error"
        >
          {{ tooltip }}
        </span>
        <v-spacer />
        <v-btn
          :disabled="loading"
          prepend-icon="mdi-check"
          color="primary"
          size="small"
          @click="submit()"
        >
          Kommentieren
        </v-btn>
      </v-container>
    </v-card>
  </v-timeline-item>

</template>

<style scoped>

.w-custom {
  width: 100%;
}

</style>
