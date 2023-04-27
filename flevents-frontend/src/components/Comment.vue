<script setup lang="ts">
import {Comment} from "@/models/comment";
import ColorService from "@/service/colorService";
import DatetimeService from "../service/datetimeService";

defineProps({
  comment: {
    required: true,
    type: Object as () => Comment,
  }
})

</script>

<template>
  <v-timeline-item
    icon="mdi-account"
    width="100%"
    :dot-color="ColorService.getAvatarColor(comment.author)"
    fill-dot
  >
    <v-card
      elevation="0"
      border
      class="ms-n4"
    >
      <v-card-text class="d-flex flex-column">
        <div class="d-flex flex-row align-center gap">
          <div class="d-flex flex-column flex-sm-row gap">
            <strong>{{ comment.author.firstname }} {{ comment.author.lastname }}</strong>
            <span class="text-grey mt-n2 mt-sm-0">{{ DatetimeService.getDateTime(comment.creationDate) }}</span>
          </div>
          <v-spacer />
          <v-btn
            icon=""
            size="25"
            variant="text"
          >
            <v-icon
              icon="mdi-delete"
              size="15"
            />
          </v-btn>
        </div>
        {{ comment.content }}
      </v-card-text>
    </v-card>
  </v-timeline-item>

</template>

<style scoped>

</style>
