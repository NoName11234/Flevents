<script setup lang="ts">
import {computed, ref} from "vue";
import {useRoute} from "vue-router";
import Heading from "@/components/Heading.vue";
import axios from "axios";
import {Post} from "@/models/post";
import router from "@/router";
import EventPost from "@/components/EventPost.vue";
import {Account} from "@/models/account";
import {EventRole} from "@/models/eventRole";
import {Questionnaire} from "@/models/questionnaire";
import QuestionnaireDisplay from "@/components/QuestionnaireDisplay.vue";
import {useEventStore} from "@/store/events";
import {useSurveyStore} from "@/store/surveys";
import {usePostStore} from "@/store/posts";
import {AccountPreview} from "@/models/accountPreview";
import {useAccountStore} from "@/store/account";
import {storeToRefs} from "pinia";
import eventApi from "@/api/eventApi";
import {useAppStore} from "@/store/app";
import api from "@/api/api";

const openContext = ref(false);
const tab = ref(null);
const address = ref("");
const route = useRoute();
const accountStore = useAccountStore();
const { currentAccount: account } = storeToRefs(accountStore);

const appStore = useAppStore();

const eventStore = useEventStore();
const event = eventStore.getEventGetter(route.params.uuid as string);

const surveyStore = useSurveyStore();
const questionnaires = computed(() => surveyStore.getSurveys(route.params.uuid as string) as Questionnaire[]);

const postStore = usePostStore();
const posts = computed(() => postStore.getPosts(route.params.uuid as string) as Post[]);

const enrollLoading = ref(false);
const storesLoading = computed(() =>
  eventStore.specificLoading.get(route.params.uuid as string)
  || surveyStore.loading
  || postStore.loading
);

const attendees = computed(() => {
  return event?.value?.accountPreviews.filter(a => [EventRole.attendee||EventRole.tutor].includes(a.role as EventRole))
});
const organizers = computed(() => {
  return event?.value?.accountPreviews.filter(a => [EventRole.organizer].includes(a.role as EventRole))
});
const allAttendees = computed(() => attendees?.value?.concat(organizers.value));

const isAttending = computed(() => {
  return attendees?.value?.find(v => v!.uuid === account.value!.uuid) != undefined;
});

const validateRole = computed(() => {
  //TODO: Ändern in JWT
  //TODO: als admin anzeigen
  for (let j = 0; j < organizers?.value?.length; j++){
    if (account.value?.uuid === organizers.value[j]!.uuid && organizers.value[j]!.role === "organizer") {
      return EventRole.organizer;
    }
  }
  for (let i = 0; i < allAttendees?.value?.length; i++){
    if (account.value?.uuid === allAttendees.value[i]!.uuid && allAttendees.value[i]!.role === "organizer") {
      return EventRole.organizer;
    }
    else if (account.value?.uuid === allAttendees.value[i]!.uuid && allAttendees.value[i]!.role === "tutor") {
      return EventRole.tutor;
    }
  }
  if (eventStore.managedEventsIds.includes(event.value.uuid!)) {
    return EventRole.organizer;
  }
  return EventRole.attendee;
})

const debugPosts = ref([
  {
    title: "Ankündigung des Sprechers",
    text: "Nach langem Warten können wir Ihnen endlich unseren Sprecher vorstellen! Peter Korstens hat begeistert zugesagt und wird Sie durch den Termin begleiten. Anbei finden Sie das Handout zum Vortrag. Wir freuen uns auf Ihr Kommen!",
    date: new Date(),
    author: {
      firstname: "Peter",
      lastname: "Korstens",
    },
    attachments: [
      "Handout Vortrag.pdf"
    ],
  },
  {
    title: "Vorabinfos",
    text: "Voller Vorfreude planen wir unseren gemeinsamen Workshop. Damit auch Sie bestens vorbereitet sind, möchten wir Ihnen hiermit noch einmal den Flyer und für den Veranstaltungstag Lageplan und Parkplatzplan bereitstellen. Wir freuen uns auf Ihr Kommen!",
    date: new Date(),
    author: {
      firstname: "Sabine",
      lastname: "Meier",
    },
    attachments: [
      "Flyer.pdf",
      "Lageplan und Parkplätze.pdf"
    ],
  }
] as Post[]);

const eventStatus = computed(() => {
  let start = new Date(event?.value?.startTime);
  let end = new Date(event?.value?.endTime);
  let now = new Date();
  switch (true) {
    case start > now:
      return {text: 'ANSTEHEND', color: "blue", icon: "mdi-clock-outline"};
    case start <= now && end >= now:
      return {text: 'IM GANGE', color: "green", icon: "mdi-play"};
    case end < now:
      return {text: 'VERGANGEN', color: "grey", icon: "mdi-archive"};
    default:
      return {text: 'STATUS NICHT ERMITTELBAR', color: 'error', icon: "mdi-exclamation-thick"};
  }
});

async function enroll(){
  enrollLoading.value = true;
  try {
    // const response = await api.post(`http://localhost:8082/api/events/${route.params.uuid as string}/add-account/${account.value!.uuid as string}`);
    const response = await eventApi.addAccount(route.params.uuid as string);
    await eventStore.hydrateSpecific(route.params.uuid as string);
  } catch (e) {
    // already enrolled
    console.error("Enrollment failed, probably already enrolled.", e);
    appStore.addToast({
      text: 'Anmelden fehlgeschlagen. Sie sind womöglich bereits angemeldet. Versuchen Sie die Seite neu zu laden.',
      color: 'error',
    });
  }
  enrollLoading.value = false;
  eventStore.hydrate();
}
async function disEnroll(){
  enrollLoading.value = true;
  if (account.value!.organizationPreviews.filter(o => o.uuid === event.value.organizationPreview.uuid).length === 0) {
    const accept = window.confirm("Das Event wird von einer Organisation veranstaltet, der Sie nicht angehören. Wenn Sie sich abmelden, können Sie nur über erneute Einladung wieder teilnehmen. Sind Sie sicher?");
    if (!accept) {
      enrollLoading.value = false;
      return;
    }
  }
  try {
    // const response = await axios.post(`http://localhost:8082/api/events/${route.params.uuid as string}/remove-account/${account.value!.uuid as string}`, {}, {params: {role: "attendee"}});
    const role = attendees.value.find(a => a.uuid === account.value!.uuid) as Account|undefined;
    if (role === undefined) throw new Error('Not in attendee list');
    const response = await eventApi.removeAccount(route.params.uuid as string, account.value!.uuid, role.role as EventRole);
    await eventStore.hydrateSpecific(route.params.uuid as string);
  } catch (e) {
    // not enrolled
    console.error("Disenrollment failed.", e);
    appStore.addToast({
      text: 'Abmelden fehlgeschlagen. Sie sind womöglich gar nicht angemeldet. Versuchen Sie die Seite neu zu laden.',
      color: 'error',
    });
  }
  enrollLoading.value = false;
  eventStore.hydrate();
}

function parseDate(from: any, to: any) {
  let short = {
    timeStyle: "short",
  } as Intl.DateTimeFormatOptions;
  let long = {
    dateStyle: "long",
    timeStyle: "short",
  } as Intl.DateTimeFormatOptions;
  let start = new Date(from);
  let end = new Date(to);
  if (isSameDay(start, end)) {
    return start.toLocaleString("DE-de", long)
      + " - "
      + end.toLocaleString("DE-de", short);
  }
  return start.toLocaleString("DE-de", long)
    + " - "
    + end.toLocaleString("DE-de", long);
}

function isSameDay(a: Date, b: Date) {
  let sameYear = a.getFullYear() === b.getFullYear();
  let sameMonth = a.getMonth() === b.getMonth();
  let sameDay = a.getDate() === b.getDate();
  return sameYear && sameMonth && sameDay;
}

function removeOrganizer(uuid : string){
  api.post(`/events/${address.value}/remove-account/${uuid}?role=organizer`).then(() => {return true;})
  for(let i = 0; i < organizers.value.length; i++){
    if(organizers.value[i].uuid === uuid){
      organizers.value.splice(i,1);
    }
  }
}

function removeAccount(uuid : string, role : string){
  api.post(`/events/${address.value}/remove-account/${uuid}?role=${role}`).then(() => {return true;})
  for(let i = 0; i < attendees.value.length; i++){
    if(attendees.value[i].uuid === uuid){
      attendees.value.splice(i,1);
    }
  }
}

async function updateRole(account: AccountPreview) {
  console.log("changing role to: ", account.role);
  console.log(attendees.value, account.uuid)
  const fromRole = attendees.value.find(a => a.uuid === account.uuid)!.role;
  try {
    //await axios.post(`http://localhost:8082/api/events/${event.value.uuid}/change-role/${account.uuid}?role=${account.role}`)
    await api.post(`/events/${event.value.uuid}/change-role/${account.uuid}?toRole=${account.role}&fromRole=${fromRole}`);
  } catch (e) {
    console.log("Failed to update role.", e);
  }
}

async function deleteEvent() {
  try {
    const response = await eventApi.delete(route.params.uuid as string);
    openContext.value = false;
    await router.push({ name: 'home.manage', force: true });
  } catch (e) {
    console.error('Failed to delete event.', e);
    openContext.value = false;
  }
}

</script>

<template>
  <Heading :text="event?.name ?? 'Lade Event...'" />

  <v-card
    :loading="storesLoading"
    :disabled="storesLoading"
  >

    <v-img
       height="250"
       class="bg-gradient"
       cover
       :src="event?.image ?? ''"
    >
      <v-badge
        :color="eventStatus.color"
        :content="eventStatus.text"
        offset-x="-20"
      />
    </v-img>

    <v-tabs
      v-model="tab"
      class="bg-primary"
    >
      <v-tab
        value="info"
        :disabled="storesLoading"
      >
        Informationen
      </v-tab>
<!--      <v-tab-->
<!--        value="posts"-->
<!--        :disabled="storesLoading"-->
<!--      >-->
<!--        Posts-->
<!--      </v-tab>-->
<!--      <v-tab-->
<!--        value="polls"-->
<!--        :disabled="storesLoading"-->
<!--        >-->
<!--        Umfragen-->
<!--      </v-tab>-->
      <v-tab
        v-if="validateRole === EventRole.tutor || validateRole == EventRole.organizer"
        value="attendees"
        :disabled="storesLoading"
      >
        Teilnehmer
      </v-tab>
      <v-tab
        v-if="validateRole === EventRole.tutor || validateRole == EventRole.organizer"
        value="attendance"
        :disabled="storesLoading"
      >
        Anwesenheit
      </v-tab>
      <v-tab
        v-if="validateRole == EventRole.organizer"
        value="organizers"
        :disabled="storesLoading"
      >
        Verwalter
      </v-tab>
    </v-tabs>

    <v-window v-model="tab">

      <v-window-item value="info">
        <v-container>
          {{event?.description}}
        </v-container>
        <v-divider />
        <v-list>
          <v-list-item
            v-if="event?.startTime && event?.endTime"
            prepend-icon="mdi-clock"
          >
            {{parseDate(event?.startTime, event?.endTime)}}
          </v-list-item>
          <v-list-item
            v-if="event?.location"
            prepend-icon="mdi-map-marker"
          >
            {{event?.location}}
          </v-list-item>
          <v-list-item
            v-if="event?.organizationPreview?.name"
            prepend-icon="mdi-account-group"
          >
            {{event?.organizationPreview?.name}}
          </v-list-item>
        </v-list>
        <v-divider/>
        <v-container class="d-flex flex-column flex-sm-row justify-end gap">
          <v-btn
            v-if="validateRole === EventRole.tutor || validateRole == EventRole.organizer"
            variant="text"
            prepend-icon="mdi-pencil"
            :to="{ name: 'events.edit', params: { uuid: route.params.uuid }}"
          >
            Bearbeiten
          </v-btn>
          <v-btn
            v-if="validateRole == EventRole.organizer"
            color="error"
            variant="text"
            prepend-icon="mdi-delete"
            @click="openContext = true"
          >
            Löschen
          </v-btn>
          <v-overlay
            max-width="600"
            :model-value="openContext"
            class="align-center justify-center"
          >
            <v-card>
              <v-container><h4>Sind sie sicher das sie das Event {{event.name}} löschen möchten? Danach kann das Event nicht mehr wiederhersgestellt werden.</h4></v-container>
              <v-container class="d-flex justify-end gap"><v-btn @click="openContext = false" variant="text">Abbrechen</v-btn><v-btn @click="deleteEvent()" prepend-icon="mdi-delete" color="red">Löschen</v-btn></v-container>

            </v-card>
          </v-overlay>
          <v-spacer/>
          <v-btn
            :loading="enrollLoading"
            :disabled="enrollLoading"
            v-if="!isAttending"
            color="primary"
            variant="elevated"
            prepend-icon="mdi-check"
            @click="enroll()"
          >
            Anmelden
          </v-btn>
          <v-btn
            :loading="enrollLoading"
            :disabled="enrollLoading"
            v-if="isAttending"
            color="primary"
            variant="tonal"
            prepend-icon="mdi-close"
            @click="disEnroll()"
          >
            Abmelden
          </v-btn>
        </v-container>
      </v-window-item>

      <v-window-item value="posts">
        <v-container class="d-flex flex-column flex-sm-row justify-start gap">
          <v-btn
            prepend-icon="mdi-chat-plus"
            color="primary"
            variant="tonal"
            v-if="validateRole === EventRole.tutor || validateRole == EventRole.organizer"
          >
            Update posten
          </v-btn>
        </v-container>

        <v-divider />

        <v-expansion-panels
          variant="accordion"
          multiple
        >
          <EventPost
            v-for="(post, index) in posts"
            :post="post"
            :key="index"
          />
        </v-expansion-panels>
      </v-window-item>

      <v-window-item value="polls">
        <v-container class="d-flex flex-column flex-sm-row justify-start gap">
          <v-btn
            prepend-icon="mdi-book-plus"
            color="primary"
            variant="tonal"
            v-if="validateRole === EventRole.tutor || validateRole == EventRole.organizer"
            :to="{ name: 'events.questionnaires.create', params: { uuid: event.uuid } }"
          >
            Fragebogen erstellen
          </v-btn>
        </v-container>

        <v-divider />

        <v-expansion-panels
          variant="accordion"
          multiple
        >
          <QuestionnaireDisplay
            v-for="(questionnaire, index) in questionnaires"
            :key="index"
            :questionnaire="questionnaire"
            :event="event"
          />
        </v-expansion-panels>
      </v-window-item>

      <v-window-item value="attendees">
        <v-container class="d-flex flex-column flex-sm-row justify-start gap">
          <v-btn
            :to="{ name: 'events.invite', params: { uuid: event.uuid } }"
            prepend-icon="mdi-account-plus"
            color="primary"
            variant="tonal"
            v-if="validateRole === EventRole.tutor || validateRole == EventRole.organizer"
          >
            Teilnehmer einladen
          </v-btn>
        </v-container>
        <v-divider />
        <v-table
        fixed-header>
          <thead>
            <tr>
              <th>
                Vorname
              </th>
              <th>
                Nachname
              </th>
              <th>
                E-Mail
              </th>
              <th v-if="validateRole === EventRole.tutor || validateRole == EventRole.organizer">
                Rolle
              </th>
              <th>
                Bestätigt
              </th>
              <th v-if="validateRole === EventRole.tutor || validateRole == EventRole.organizer">Entfernen</th>
            </tr>
          </thead>
          <tbody>
            <tr
             v-for="(item, index) in attendees"
             :key="index"
            >
              <td>{{item.firstname}}</td>
              <td>{{item.lastname}}</td>
              <td>{{item.email}}</td>
              <td v-if="validateRole === EventRole.tutor || validateRole == EventRole.organizer">
                <v-select
                  :items="[
                    EventRole.attendee,
                    EventRole.tutor
                  ]"
                  v-model="item.role"
                  density="compact"
                  variant="outlined"
                  hide-details="auto"
                  @update:model-value="updateRole(item)"
                />
              </td>
              <td>
                <v-icon v-if="item.role !== 'invited'" icon="mdi-check-circle" color="gray"/>
                <v-icon v-if="item.role === 'invited'" icon="mdi-close-circle" color="gray"/>
              </td>
              <td v-if="validateRole === EventRole.tutor || validateRole == EventRole.organizer">
                <v-btn variant="text" size="small" icon="mdi-delete" v-if="validateRole === EventRole.tutor || validateRole == EventRole.organizer" @click="removeAccount(item.uuid, item.role)"></v-btn>
              </td>
            </tr>
          </tbody>
        </v-table>
      </v-window-item>

      <v-window-item value="attendance">
        <v-container class="d-flex flex-column flex-sm-row justify-start gap">
          <v-btn
            prepend-icon="mdi-account-plus"
            color="primary"
            variant="tonal"
          >
            Unangemeldeten Teilnehmer hinzufügen
          </v-btn>
        </v-container>
        <v-divider />
        <v-table
          fixed-header>
          <thead>
          <tr>
            <th>
              Vorname
            </th>
            <th>
              Nachname
            </th>
            <th>
              E-Mail
            </th>
            <th>
              Anwesend
            </th>
          </tr>
          </thead>
          <tbody>
          <tr
            v-for="(item, index) in attendees"
            :key="index"
          >
            <td>{{item.firstname}}</td>
            <td>{{item.lastname}}</td>
            <td>{{item.email}}</td>
            <td>
              <v-checkbox
                hide-details="auto"
                density="compact"
              />
            </td>
          </tr>
          </tbody>
        </v-table>
      </v-window-item>
      <v-window-item value="organizers">
        <v-container class="d-flex flex-column flex-sm-row justify-start gap">
          <v-btn
            :to="{ name: 'events.organizer', params: { uuid: event.uuid } }"
            prepend-icon="mdi-account-plus"
            color="primary"
            variant="tonal"
          >
            Verwalter einladen
          </v-btn>
        </v-container>
        <v-divider />
        <v-table
          fixed-header>
          <thead>
          <tr>
            <th>
              Vorname
            </th>
            <th>
              Nachname
            </th>
            <th>
              E-Mail
            </th>
            <th>Entfernen</th>
          </tr>
          </thead>
          <tbody>
          <tr
            v-for="(item, index) in organizers"
            :key="index"
          >
            <td>{{item.firstname}}</td>
            <td>{{item.lastname}}</td>
            <td>{{item.email}}</td>
            <td><v-btn
              :disabled="organizers.length <= 1"
              variant="text"
              size="small"
              icon="mdi-delete"
              @click="removeOrganizer(item.uuid)"
            >
            </v-btn></td>
          </tr>
          </tbody>
        </v-table>
      </v-window-item>

    </v-window>
  </v-card>
</template>

<style scoped>

</style>
