<script setup lang="ts">
import {computed, ref} from "vue";
import {useRoute} from "vue-router";
import Heading from "@/components/Heading.vue";
import {AxiosError} from "axios";
import router from "@/router";
import EventPost from "@/components/EventPost.vue";
import {Account} from "@/models/account";
import {EventRole} from "@/models/eventRole";
import {Questionnaire} from "@/models/questionnaire";
import QuestionnaireDisplay from "@/components/QuestionnaireDisplay.vue";
import {useEventStore} from "@/store/events";
import {useSurveyStore} from "@/store/surveys";
import {AccountPreview} from "@/models/accountPreview";
import {useAccountStore} from "@/store/account";
import {storeToRefs} from "pinia";
import eventApi from "@/api/eventsApi";
import {useAppStore} from "@/store/app";
import {useOrganizationStore} from "@/store/organizations";

const openContext = ref(false);
const address = ref("");
const route = useRoute();
const eventUuid = route.params.uuid as string;

const tab = computed({
  get: () => route.query.tab ?? 'info',
  set: (tabValue) => router.push({ ...route, query: { ...route.query, tab: tabValue }}),
});

const accountStore = useAccountStore();
const { currentAccount: account } = storeToRefs(accountStore);

const appStore = useAppStore();

const eventStore = useEventStore();
const event = eventStore.getEventGetter(eventUuid);
const posts = computed(() => event.value
  .posts?.sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime()));

const surveyStore = useSurveyStore();
const questionnaires = computed(() => surveyStore.getSurveys(eventUuid) as Questionnaire[]);

const organizationStore = useOrganizationStore();

const enrollLoading = ref(false);
const organizersLoading = ref(false);
const attendeesLoading = ref(false);
const storesLoading = computed(() =>
  eventStore.specificLoading.get(eventUuid)
);

const attendees = computed(() => {
  return event?.value?.accountPreviews.filter(a => [EventRole.attendee, EventRole.tutor].includes(a.role as EventRole)) as AccountPreview[];
});
const organizers = computed(() => {
  return event?.value?.accountPreviews.filter(a => [EventRole.organizer].includes(a.role as EventRole)) as AccountPreview[]
});
const allAttendees = computed(() => attendees?.value?.concat(organizers.value) as AccountPreview[]);

const isAttending = computed(() => {
  return (attendees?.value?.find(v => v!.uuid === account.value!.uuid) != undefined) as boolean;
});

const validateRole = computed(() => {
  //TODO: Ändern in JWT

  // If admin in event's organization then show everything
  if (organizationStore.managedOrganizations.find(o => o.uuid === event.value.organizationPreview.uuid)) {
    return EventRole.organizer;
  }

  // Else check for specific rights
  if (organizers.value.find(o => o.uuid === account.value?.uuid)) {
    return EventRole.organizer;
  }
  if (attendees.value.find(a => a.uuid === account.value?.uuid && a.role === EventRole.tutor)) {
    return EventRole.tutor;
  }
  return EventRole.attendee;
});

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
    const response = await eventApi.addAccount(eventUuid);
    await eventStore.hydrateSpecific(eventUuid);
    appStore.addToast({
      text: 'Erfolgreich angemeldet.',
      color: 'success',
    });
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
    const role = attendees.value.find(a => a.uuid === account.value!.uuid) as Account|undefined;
    if (role === undefined) throw new Error('Not in attendee list');
    const response = await eventApi.removeAccount(eventUuid, account.value!.uuid, role.role as EventRole);
    await eventStore.hydrateSpecific(eventUuid);
    appStore.addToast({
      text: 'Erfolgreich abgemeldet.',
      color: 'success',
    });
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

async function removeOrganizer(deletedOrganizer: AccountPreview) {
  let ok = false;
  if (deletedOrganizer.uuid === account.value!.uuid) {
    // Current user removes himself and possibly removes his access
    ok = window.confirm(
      `Sind Sie sicher, dass Sie Sich aus dem Event ${event.value.name} entfernen wollen?`
      + ` Wenn Sie kein Administrator in ${event.value.organizationPreview.name} sind, werden Ihnen damit alle Verwalterrechte in ihm entzogen.`
      + ` Sind Sie für das Event als Teilnehmer oder Tutor registriert, bleibt dieser Zugriff erhalten.`
      + ` Um erneut Verwalterrechte zu erhalten, müssen Sie erneut dazu eingeladen werden.`
    );
  } else {
    // Someone else is being removed
    ok = window.confirm(
      `Sind Sie sicher, dass Sie ${deletedOrganizer.firstname} ${deletedOrganizer.lastname} (${deletedOrganizer.email}) aus dem Event ${event.value.name} entfernen wollen?`
      + ` Wenn die Person kein Administrator in ${event.value.organizationPreview.name} ist, werden ihr damit alle Verwalterrechte in ihm entzogen.`
      + ` Ist sie für das Event als Teilnehmer oder Tutor registriert, bleibt dieser Zugriff erhalten.`
      + ` Um erneut Verwalterrechte zu erhalten, muss sie erneut dazu eingeladen werden.`
    );
  }
  if (!ok) {
    return;
  }
  organizersLoading.value = true;
  try {
    await eventApi.removeAccount(eventUuid, deletedOrganizer.uuid, EventRole.organizer);
    await eventStore.hydrateSpecific(eventUuid);
    appStore.addToast({
      text: 'Organisator entfernt.',
      color: 'success',
    });
  } catch (e) {
    let errorMessage = '';
    if (e instanceof AxiosError) {
      if (e.code === AxiosError.ERR_BAD_REQUEST) {
        errorMessage = 'Ungültige Anfrage';
      }
      else if (e.code === AxiosError.ERR_NETWORK) {
        errorMessage = 'Netzwerkfehler';
      }
    } else {
      errorMessage = `Unerwarteter Fehler: ${e}`;
    }
    appStore.addToast({
      text: `Organisator konnte nicht entfernt werden: ${errorMessage}`,
      color: 'error',
    });
  }
  organizersLoading.value = false;
  eventStore.hydrate();
}

async function removeAttendee(deletedAttendee: AccountPreview) {
  let ok = false;
  if (
    deletedAttendee.uuid === account.value!.uuid
    && validateRole.value !== EventRole.organizer
  ) {
    // Current user removes himself and possibly removes his access
    ok = window.confirm(
      `Sind Sie sicher, dass Sie Sich aus dem Event ${event.value.name} entfernen wollen?`
      + ` Wenn Sie kein Teil von ${event.value.organizationPreview.name} sind, wird Ihnen damit der Zugriff darauf entzogen.`
      + ` Um erneut Zugriff zu erhalten, müssen Sie erneut dazu eingeladen werden.`
    );
  } else if (deletedAttendee.uuid === account.value!.uuid) {
    // Current user removes himself and is organizer of event or admin of its organization
    // Therefore he is not in risk of removing his access
    ok = true;
  } else {
    // Someone else is being removed
    ok = window.confirm(
      `Sind Sie sicher, dass Sie ${deletedAttendee.firstname} ${deletedAttendee.lastname} (${deletedAttendee.email}) aus dem Event ${event.value.name} entfernen wollen?`
      + ` Wenn die Person kein Teil von ${event.value.organizationPreview.name} sind, wird ihr damit der Zugriff darauf entzogen.`
      + ` Um erneut Zugriff zu erhalten, muss sie erneut dazu eingeladen werden.`
    );
  }
  if (!ok) {
    return;
  }
  attendeesLoading.value = true;
  try {
    await eventApi.removeAccount(eventUuid, deletedAttendee.uuid, deletedAttendee.role as EventRole);
    await eventStore.hydrateSpecific(eventUuid);
    appStore.addToast({
      text: 'Account entfernt.',
      color: 'success',
    });
  } catch (e) {
    let errorMessage = '';
    if (e instanceof AxiosError) {
      if (e.code === AxiosError.ERR_BAD_REQUEST) {
        errorMessage = 'Ungültige Anfrage';
      }
      else if (e.code === AxiosError.ERR_NETWORK) {
        errorMessage = 'Netzwerkfehler';
      }
    } else {
      errorMessage = `Unerwarteter Fehler: ${e}`;
    }
    appStore.addToast({
      text: `Account konnte nicht entfernt werden: ${errorMessage}`,
      color: 'error',
    });
  }
  attendeesLoading.value = false;
  eventStore.hydrate();
}

async function updateRole(updatedAttendee: AccountPreview, newRole: EventRole) {
  if (
    updatedAttendee.uuid === account.value!.uuid
    && validateRole.value !== EventRole.organizer
  ) {
    // Current changes own role and possibly removes his access
    const ok = window.confirm(
      `Sind Sie sicher, dass Sie Ihre eigene Rolle zu "${newRole}" ändern möchten?`
      + ` Sie verlieren damit alle Rechte, die Sie als ${updatedAttendee.role} besitzen.`
    );
    if (!ok) {
      return;
    }
  }
  attendeesLoading.value = true;
  try {
    await eventApi.changeRole(eventUuid, updatedAttendee.uuid, updatedAttendee.role as EventRole, newRole);
    await eventStore.hydrateSpecific(eventUuid);
    appStore.addToast({
      text: 'Rolle aktualisiert.',
      color: 'success',
    });
  } catch (e) {
    let errorMessage = '';
    if (e instanceof AxiosError) {
      if (e.code === AxiosError.ERR_BAD_REQUEST) {
        errorMessage = 'Ungültige Anfrage';
      }
      else if (e.code === AxiosError.ERR_NETWORK) {
        errorMessage = 'Netzwerkfehler';
      }
    } else {
      errorMessage = `Unerwarteter Fehler: ${e}`;
    }
    appStore.addToast({
      text: `Rolle konnte nicht aktualisiert werden: ${errorMessage}`,
      color: 'error',
    });
  }
  attendeesLoading.value = false;
  eventStore.hydrate();
}

async function deleteEvent() {
  try {
    const response = await eventApi.delete(eventUuid);
    openContext.value = false;
    await router.push({ name: 'home.manage', force: true });
  } catch (e) {
    console.error('Failed to delete event.', e);
    openContext.value = false;
  }
  eventStore.hydrate();
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
      <v-tab
        value="posts"
        :disabled="storesLoading"
      >
        Posts
      </v-tab>
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
        <template v-if="event?.description">
          <v-container>
            {{event?.description}}
          </v-container>
          <v-divider />
        </template>
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
            :to="{ name: 'events.edit', params: { uuid: eventUuid }}"
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
        <v-container
          v-if="validateRole === EventRole.tutor || validateRole == EventRole.organizer"
          class="d-flex flex-column flex-sm-row justify-start gap"
        >
          <v-btn
            prepend-icon="mdi-chat-plus"
            color="primary"
            variant="tonal"
            :to="{ name: 'events.posts.create', params: { uuid: eventUuid } }"
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
            v-for="(post, pIndex) in posts"
            :event-uuid="eventUuid"
            :post="post"
            :admin-view="validateRole === EventRole.organizer"
            :key="pIndex"
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
            :to="{ name: 'events.questionnaires.create', params: { uuid: eventUuid } }"
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

      <v-window-item value="attendees" :disabled="attendeesLoading">
        <v-progress-linear
          :active="attendeesLoading"
          color="grey-lighten-1"
          indeterminate
          rounded-bar
          rounded
          absolute
        />
        <v-container class="d-flex flex-column flex-sm-row justify-start gap">
          <v-btn
            :to="{ name: 'events.invite', params: { uuid: eventUuid } }"
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
                Name
              </th>
              <th>
                E-Mail
              </th>
              <th v-if="validateRole === EventRole.tutor || validateRole === EventRole.organizer">
                Rolle
              </th>
<!--              <th>-->
<!--                Bestätigt-->
<!--              </th>-->
              <th v-if="validateRole === EventRole.tutor || validateRole === EventRole.organizer">
                Entfernen
              </th>
            </tr>
          </thead>
          <tbody>
            <tr
             v-for="(item, index) in attendees"
             :key="index"
            >
              <td>
                {{item.firstname}}&nbsp;{{item.lastname}}
              </td>
              <td>
                {{item.email}}
              </td>
              <td v-if="validateRole === EventRole.tutor || validateRole == EventRole.organizer">
                <v-btn
                  append-icon="mdi-chevron-down"
                  variant="text"
                  class="d-flex flex-row justify-space-between"
                  block
                >
                  {{ item.role }}

                  <v-menu activator="parent">
                    <v-list>
                      <v-list-subheader>
                        Rolle wechseln zu:
                      </v-list-subheader>
                      <v-list-item
                        v-for="(role, index) in [
                          EventRole.attendee,
                          EventRole.tutor
                        ]"
                        :key="index"
                        :value="index"
                        :disabled="role === item.role"
                        :title="role.toString()"
                        @click="updateRole(item, role)"
                        density="compact"
                      />
                    </v-list>
                  </v-menu>
                </v-btn>
              </td>
<!--              <td>-->
<!--                <v-icon v-if="item.role !== 'invited'" icon="mdi-check-circle" color="gray"/>-->
<!--                <v-icon v-if="item.role === 'invited'" icon="mdi-close-circle" color="gray"/>-->
<!--              </td>-->
              <td v-if="validateRole === EventRole.tutor || validateRole == EventRole.organizer">
                <v-btn
                  variant="text"
                  size="small"
                  icon="mdi-delete"
                  v-if="validateRole === EventRole.tutor || validateRole == EventRole.organizer"
                  @click="removeAttendee(item)"
                />
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
              Name
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
            <td>{{item.firstname}} {{item.lastname}}</td>
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

      <v-window-item value="organizers" :disabled="organizersLoading">
        <v-progress-linear
          :active="organizersLoading"
          color="grey-lighten-1"
          indeterminate
          rounded-bar
          rounded
          absolute
        />
        <v-container class="d-flex flex-column flex-sm-row justify-start gap">
          <v-btn
            :to="{ name: 'events.organizer', params: { uuid: eventUuid } }"
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
              Name
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
            <td>{{item.firstname}} {{item.lastname}}</td>
            <td>{{item.email}}</td>
            <td><v-btn
              :disabled="organizers.length <= 1"
              variant="text"
              size="small"
              icon="mdi-delete"
              @click="removeOrganizer(item)"
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
