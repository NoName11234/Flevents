<script setup lang="ts">
import {FleventsEvent} from "@/models/fleventsEvent";
import {onMounted, ref} from "vue";
import EventListFilters from "@/components/EventListFilters.vue";
import EventCard from "@/components/EventCard.vue";

const props = defineProps({
  events: {
    required: true,
    type: Array<FleventsEvent>
  },
  showManageTools: {
    required: false,
    type: Boolean,
    default: false,
  }
});

//TODO: Filtermöglichkeiten (Organisation, Suche, Datumsauswahl, Sortierung), Liste aller verfügbaren Events (die ersten 20 oder so)

const term = ref('');
const eventList = computed(() => {
  return props.events?.filter((event) => (
      event.description?.toLowerCase().includes(term.value)
      || event.name?.toLowerCase().includes(term.value)
      || event.location?.toLowerCase().includes(term.value)
      || event.organizationPreview?.name.toLowerCase().includes(term.value)
      || event.organizationPreview?.description.toLowerCase().includes(term.value)
    )
  );
});

function updateList(newTerm: string) {
  term.value = newTerm;
}
onMounted(() => {
  console.log(eventList.value);
})
</script>

<template>
  <EventListFilters
    @update="updateList"
  />
  <div class="d-flex flex-column gap">
    <EventCard
      v-for="(e, i) in eventList"
      :key="i"
      :flevents-event="e"
      :show-manage-tools="showManageTools"
    />
  </div>
</template>

<style scoped>

</style>
