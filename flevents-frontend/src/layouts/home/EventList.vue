<script setup lang="ts">
import {FleventsEvent} from "@/models/fleventsEvent";
import {computed, ref} from "vue";
import EventListFilters from "@/components/EventListFilters.vue";
import EventCard from "@/components/EventCard.vue";
import ContentLoadingIndicator from "@/components/ContentLoadingIndicator.vue";
import CardBanner from "@/components/CardBanner.vue";
import {useAccountStore} from "@/store/account";
import {storeToRefs} from "pinia";
import {EventListFilterOptions} from "@/models/EventListFilterOptions";
import {EventRole} from "@/models/eventRole";

const props = defineProps({
  events: {
    required: true,
    type: Array<FleventsEvent>
  },
  loading: {
    required: true,
    type: Boolean,
    default: false,
  },
  error: {
    required: true,
    type: Boolean,
    default: false,
  },
  showManageTools: {
    required: false,
    type: Boolean,
    default: false,
  }
});

const accountStore = useAccountStore();
const { currentAccount: account } = storeToRefs(accountStore);

const filters = ref({} as EventListFilterOptions);

const eventList = computed(() => {
  const currentFilters = filters.value;
  let events = [ ...props.events ];

  if (currentFilters.queryText !== undefined) {
    const queryText = currentFilters.queryText!.toLowerCase();
    events = events.filter(e =>
      e.description?.toLowerCase().includes(queryText)
      || e.name?.toLowerCase().includes(queryText)
      || e.location?.toLowerCase().includes(queryText)
      || e.organizationPreview?.name.toLowerCase().includes(queryText)
      || e.organizationPreview?.description.toLowerCase().includes(queryText)
    );
  }

  if (currentFilters.organizationUuids !== undefined && currentFilters.organizationUuids.length > 0) {
    const organizationUuids = currentFilters.organizationUuids!;
    events = events.filter(e =>
      organizationUuids.includes(e.organizationPreview.uuid)
    );
  }

  if (currentFilters.eventRoles !== undefined && currentFilters.eventRoles.length > 0) {
    const eventRoles = currentFilters.eventRoles!;
    events = events.filter(e =>
      e.accountPreviews.find(a =>
        a.uuid === account.value!.uuid
        && eventRoles.includes(a.role as EventRole)
      )
    );
  }

  if (currentFilters.fromDate !== undefined) {
    const fromDate = currentFilters.fromDate!;
    events = events.filter(e =>
      new Date(e.endTime).getTime() - new Date(fromDate).getTime() >= 0
    );
  }

  if (currentFilters.toDate !== undefined) {
    const toDate = currentFilters.toDate!;
    events = events.filter(e =>
      new Date(toDate).getTime() - new Date(e.startTime).getTime() >= 0
    );
  }

  return events.sort((a, b) => new Date(b.endTime).getTime() - new Date(a.endTime).getTime());
});

function updateList(newFilters: EventListFilterOptions) {
  filters.value = newFilters;
}
</script>

<template>
  <EventListFilters
    @update="updateList"
  />
  <div
    class="d-flex flex-column gap"
  >
    <ContentLoadingIndicator
      :loading="loading"
    />
    <CardBanner
      v-show="!loading && error"
      message="Error: Es konnten keine Events abgerufen werden."
    />
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
