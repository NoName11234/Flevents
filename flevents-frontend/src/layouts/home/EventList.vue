<script setup lang="ts">
import {FleventsEvent} from "@/models/fleventsEvent";
import {onMounted, ref} from "vue";
import EventListFilters from "@/components/EventListFilters.vue";
import EventCard from "@/components/EventCard.vue";

const props = defineProps({
  events: {
    required: true,
    type: Array<FleventsEvent>
  }
});

const eventList = ref(Array.from(props.events));

function updateList(newList: Array<FleventsEvent>) {
  eventList.value = newList;
}
onMounted(() => {
  console.log(eventList.value);
})
</script>

<template>
  <EventListFilters
    :event-list="eventList"
    @update="updateList"
  />
  <div class="d-flex flex-column gap">
    <EventCard
      v-for="(e, i) in eventList"
      :key="i"
      :flevents-event="e"
    />
  </div>
</template>

<style scoped>

</style>
