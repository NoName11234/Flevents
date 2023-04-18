<script setup lang="ts">
import {Post} from "@/models/post";
import Comment from "@/components/Comment.vue";
import AccountAvatar from "@/components/AccountAvatar.vue";
import {useRoute} from "vue-router";
import CommentForm from "@/components/CommentForm.vue";
import security from "@/service/security";

const route = useRoute();

const props = defineProps({
  post: {
    required: true,
    type: Object as () => Post,
  }
});

</script>

<template>
  <v-expansion-panel
    color="primary"
  >

    <v-expansion-panel-title>

      <div class="d-flex flex-row justify-start align-center gap-3 w-100">

        <strong>
          {{ post.title }}
        </strong>

        <span class="text-grey">
          {{ post.creationDate.toLocaleDateString("DE-de") }}
        </span>

      </div>

    </v-expansion-panel-title>

    <v-expansion-panel-text>
      <div class="d-flex flex-column my-1 mx-n3">
        <v-card
          elevation="0"
          border
        >

          <v-container
            class="d-flex flex-column gap-2"
          >
            <div
              class="d-flex flex-row align-center gap-3"
            >
              <AccountAvatar
                :size="'40'"
                :account="post.author"
              />
              <div class="d-flex flex-column">
                <strong>
                  {{ post.author.firstname }} {{ post.author.lastname }}
                </strong>
<!--                <small class="text-grey">-->
<!--                  {{ post.creationDate.toLocaleDateString("DE-de") }}-->
<!--                </small>-->
              </div>
              <v-spacer />

              <v-btn
                icon=""
                size="small"
                variant="text"
              >
                <v-icon
                  icon="mdi-dots-vertical"
                />
                <v-menu activator="parent">
                  <v-list>
                    <v-list-item
                      prepend-icon="mdi-pencil"
                      title="Bearbeiten"
                      :to="{ name: 'events.posts.edit', params: { uuid: route.params.uuid, postUuid: 1 } }"
                    />
                    <v-list-item
                      prepend-icon="mdi-delete"
                      title="Löschen"
                    />
                  </v-list>
                </v-menu>
              </v-btn>
            </div>
            <span>
              {{ post.content }}
            </span>

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
                @click=""
              >
              </v-chip>
            </v-chip-group>

          </v-container>

        </v-card>

        <v-timeline
          side="end"
          truncate-line="end"
          density="compact"
        >
          <CommentForm
            :account="security.getAccount()"
          />
          <Comment
            v-for="(comment, cIndex) in post.comments"
            :key="cIndex"
            :comment="comment"
          />
        </v-timeline>

      </div>

    </v-expansion-panel-text>

  </v-expansion-panel>
</template>

<style scoped>

</style>
