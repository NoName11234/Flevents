<script setup lang="ts">
import {Post} from "@/models/post";

const props = defineProps({
  post: {
    required: true,
    type: Object as () => Post,
  }
});

</script>

<template>
  <v-expansion-panel color="primary">

    <v-expansion-panel-title>

      <div class="d-flex flex-row justify-start align-center gap-3 w-100">

        <strong>
          {{ post.title }}
        </strong>

        <span class="text-grey">
          {{ post.date.toLocaleDateString("DE-de") }}
        </span>

        <v-spacer />

        <span class="d-none d-sm-flex flex-row align-center">
          <strong>
            {{ post.author.firstname }} {{ post.author.lastname }}
          </strong>
          <v-badge inline :content="['Tutor', 'Eventverwalter'][post.author.lastname.length % 2]" color="secondary"></v-badge>
        </span>

      </div>

    </v-expansion-panel-title>

    <v-expansion-panel-text>

      <div class="d-flex flex-column gap-3 mt-3">

        <p>
            {{ post.content }}
        </p>

        <v-divider/>

        <div class="d-flex flex-row justify-end justify-sm-start gap">
          <v-chip-group
            column
          >
            <v-chip
              v-for="(attachment, index) in post.attachments"
              :key="index"
              :text="attachment"
              prepend-icon="mdi-file"
              variant="tonal"
              link
            >
            </v-chip>
          </v-chip-group>

          <v-spacer></v-spacer>

          <v-btn
            icon="mdi-pencil"
            size="small"
            variant="text"
          />
          <v-btn
            icon="mdi-delete"
            size="small"
            variant="text"
          />
        </div>



      </div>

    </v-expansion-panel-text>

  </v-expansion-panel>
</template>

<style scoped>

</style>
