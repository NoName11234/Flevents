<template>
  <v-card
    class="mx-auto"
    width="100%"
    max-width="800px"
  >

    <v-row>
      <v-col class="hidden-xs v-col-4" no-gutters>
        <v-img
          :src="fleventsEvent.image"
          height="150px"
          class="bg-gradient"
          cover
        ></v-img>
      </v-col>
      <v-col class="d-flex flex-column">

        <v-img
          :src="fleventsEvent.image"
          height="200px"
          class="hidden-sm-and-up bg-gradient"
          cover
        ></v-img>

        <v-card-title class="text-break d-flex flex-row align-center">
          {{ fleventsEvent.name }}
          <v-badge
            v-if="fleventsEvent.organizationPreview?.name"
            :content="fleventsEvent.organizationPreview.name || 'Organisationsname'"
            color="grey-lighten-3"
            inline
          />
        </v-card-title>

        <v-card-text class="text-break pb-0">
          {{
            fleventsEvent.description.length <= 120 ?
              fleventsEvent.description
              : fleventsEvent.description
                .substring(0, 120)
                .split(' ')
                .slice(0, -1)
                .join(' ')
                .concat('...')
          }}
        </v-card-text>

        <v-spacer/>

        <v-card-actions>
          <v-btn
            class="flex-grow-1 flex-sm-grow-0"
            color="primary"
            :to="{ name: 'events.event', params: { uuid: fleventsEvent.uuid } }"
          >
            Mehr
          </v-btn>
        </v-card-actions>
      </v-col>
    </v-row>
  </v-card>
</template>

<script lang="ts">
import { FleventsEvent } from '@/models/fleventsEvent';

export default {
  name: "EventCard",
  props: {
    fleventsEvent: {
      required: true,
      type: Object as () => FleventsEvent
    }
  }
}
</script>

<style scoped>

</style>
