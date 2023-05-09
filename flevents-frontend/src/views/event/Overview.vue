<script setup lang="ts">
import router from "@/router";
import eventApi from "@/api/eventsApi";
import {computed, ref} from "vue";
import {VALIDATION} from "@/constants";
import {AxiosError} from "axios";
import {Account} from "@/models/account";
import {EventRole} from "@/models/eventRole";
import {AccountPreview} from "@/models/accountPreview";
import {MailConfig} from "@/models/mailConfig";
import {FleventsEvent} from "@/models/fleventsEvent";
import {OrganizationRole} from "@/models/organizationRole";
import {useRoute} from "vue-router";
import {useEventStore} from "@/store/events";
import {useQuestionnaireStore} from "@/store/questionnaires";
import {useAccountStore} from "@/store/account";
import {storeToRefs} from "pinia";
import {useAppStore} from "@/store/app";
import {useOrganizationStore} from "@/store/organizations";
import {usePostStore} from "@/store/posts";
import Heading from "@/components/Heading.vue";
import PostDisplay from "@/components/PostDisplay.vue";
import QuestionnaireDisplay from "@/components/QuestionnaireDisplay.vue";
import MailConfigCard from "@/components/MailConfigCard.vue";
import DatetimeService from "../../service/datetimeService";

const openContext = ref(false);
const address = ref("");
const route = useRoute();
const eventUuid = route.params.uuid as string;

const tab = computed({
  get: () => route.query.tab ?? 'info',
  set: (tabValue) => router.replace({ ...route, query: { ...route.query, tab: tabValue }}),
});

const accountStore = useAccountStore();
const { currentAccount: account } = storeToRefs(accountStore);

const appStore = useAppStore();

const eventStore = useEventStore();
const event = eventStore.getEventGetter(eventUuid);

const postStore = usePostStore();
const posts = computed(() => postStore.getPostsGetterOf(eventUuid).value
  ?.sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime()));

const surveyStore = useQuestionnaireStore();
const questionnaires = computed(() => surveyStore.getQuestionnairesGetterOf(eventUuid).value
  ?.sort((a, b) => new Date(b.closingDate).getTime() - new Date(a.closingDate).getTime()));

const anonAcc = ref({
  email: '',
  firstname: '',
  lastname: ''
} as AccountPreview);

const organizationStore = useOrganizationStore();

const addAnon = ref(false);
const enrollLoading = ref(false);
const organizersLoading = ref(false);
const attendeesLoading = ref(false);
const storesLoading = computed(() =>
  eventStore.specificLoading.get(eventUuid)
);

const attendees = computed(() => {
  return event?.value?.accountPreviews.filter(a => [EventRole.attendee, EventRole.tutor, EventRole.guest].includes(a.role as EventRole)) as AccountPreview[];
});
const organizers = computed(() => {
  return event?.value?.accountPreviews.filter(a => [EventRole.organizer].includes(a.role as EventRole)) as AccountPreview[]
});
const allAttendees = computed(() => attendees?.value?.concat(organizers.value) as AccountPreview[]);

const isAttending = computed(() => {
  return (attendees?.value?.find(v => v!.uuid === account.value!.uuid) != undefined) as boolean;
});

const validateRole = computed(() => {

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
      return { code: 1, text: 'ANSTEHEND', color: "blue", icon: "mdi-clock-outline" };
    case start <= now && end >= now:
      return { code: 0, text: 'IM GANGE', color: "green", icon: "mdi-play" };
    case end < now:
      return { code: -1, text: 'VERGANGEN', color: "grey", icon: "mdi-archive" };
    default:
      return { code: NaN, text: 'STATUS NICHT ERMITTELBAR', color: 'error', icon: "mdi-exclamation-thick" };
  }
});
async function addAnonAcc(pendingValidation: Promise<any>) {
  const validation = await pendingValidation;
  if (validation.valid !== true) {
    return;
  }
  enrollLoading.value = true;
  try {
    const response = await eventApi.addUnregisteredAttendee(eventUuid, anonAcc.value);
    await eventStore.hydrateSpecific(eventUuid);
    appStore.addToast({
      text: 'Unangemeldeten Nutzer hinzugefügt.',
      color: 'success',
    });
  } catch (e) {
    // already enrolled
    console.error("Enrollment failed, probably already enrolled.", e);
    appStore.addToast({
      text: 'Anmelden des unangemeldeten Nutzers fehlgeschlagen. Versuchen Sie die Seite neu zu laden.',
      color: 'error',
    });
  }
  anonAcc.value = {email: '', firstname: '', lastname: ''} as AccountPreview
  addAnon.value = false;
  enrollLoading.value = false;
  eventStore.hydrate();
}

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

async function updateMailConfig(config: MailConfig) {
  const newEvent = { ...event.value, mailConfig: config } as FleventsEvent;
  try {
    const response = await eventApi.edit(eventUuid, newEvent);
    appStore.addToast({
      text: 'Mail-Konfiguration aktualisiert!',
      color: 'success',
    });
  } catch (e) {
    console.error('Failed to delete event.', e);
    appStore.addToast({
      text: 'Fehler beim Aktualisieren der Mail-Konfiguration.',
      color: 'error',
    });
  }
  eventStore.hydrateSpecific(eventUuid);
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
      <!-- TODO: hier eventuell anpassen, weil ja auch Orgaleute die Umfrage sehen dürfen -->
      <v-tab
       value="polls"
      :disabled="storesLoading"
       v-if="validateRole === EventRole.tutor || validateRole === EventRole.organizer || validateRole === EventRole.attendee || validateRole === OrganizationRole.organizer || validateRole === OrganizationRole.admin"
        >
       Umfragen
      </v-tab>
      <v-tab
        v-if="validateRole === EventRole.tutor || validateRole == EventRole.organizer"
        value="mails"
      >
        E-Mail-Vorlagen
      </v-tab>
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
            {{ event?.description }}
          </v-container>
          <v-divider />
        </template>
        <v-list>
          <v-list-item
            v-if="event?.startTime && event?.endTime"
            prepend-icon="mdi-clock"
            subtitle="Zeitraum"
          >
            {{ DatetimeService.formatDateRange(event?.startTime, event?.endTime) }}
          </v-list-item>
          <v-list-item
            v-if="event?.location"
            prepend-icon="mdi-map-marker"
            subtitle="Ort"
          >
            {{ event?.location }}
          </v-list-item>
          <v-list-item
            v-if="event?.organizationPreview?.name"
            prepend-icon="mdi-account-group"
            subtitle="Organisation"
          >
            {{ event?.organizationPreview?.name }}
          </v-list-item>
        </v-list>
        <v-divider/>
        <v-container class="d-flex flex-column flex-sm-row justify-end gap">
          <v-btn
            v-if="validateRole === EventRole.tutor || validateRole == EventRole.organizer"
            :disabled="eventStatus.code < 0"
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
            :disabled="enrollLoading || eventStatus.code < 0"
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
            :disabled="enrollLoading || eventStatus.code < 0"
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
          <PostDisplay
            v-for="(post, pIndex) in posts"
            :event-uuid="eventUuid"
            :post="post"
            :admin-view="validateRole === EventRole.organizer"
            :show-comment-form="isAttending"
            :key="pIndex"
          />
        </v-expansion-panels>
      </v-window-item>

      <v-window-item value="polls">
        <v-container
          v-if="validateRole === EventRole.tutor || validateRole == EventRole.organizer"
          class="d-flex flex-column flex-sm-row justify-start gap"
        >
          <v-btn
            prepend-icon="mdi-book-plus"
            color="primary"
            variant="tonal"
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

      <v-window-item value="mails">
        <MailConfigCard
          :config="event.mailConfig"
          :event-start="new Date(event.startTime)"
          :event-end="new Date(event.endTime)"
          @update="updateMailConfig"
        />
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
            prepend-icon="mdi-email-fast"
            color="primary"
            variant="tonal"
            v-if="validateRole === EventRole.tutor || validateRole == EventRole.organizer"
          >
            Teilnehmer einladen
          </v-btn>
          <v-btn
            :to="{ name: 'events.inviteOrga', params: { uuid: eventUuid } }"
            prepend-icon="mdi-account-plus"
            color="primary"
            variant="tonal"
            v-if="validateRole === EventRole.tutor || validateRole == EventRole.organizer"
          >
            Teilnehmer hinzufügen
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
             v-show="item.role !== EventRole.guest"
            >
              <td>
                {{item.firstname}}&nbsp;{{item.lastname}}
              </td>
              <td>
                {{item.email}}
              </td>
              <td v-if="validateRole === EventRole.tutor || validateRole == EventRole.organizer">
                <v-btn
                  v-if="item.role !== EventRole.guest"
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
            @click="addAnon = true"
          >
            Unangemeldeter Teilnehmer
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
                v-model="item.checkedIn"
                @click="item.checkedIn ? eventApi.checkOutAttendee(eventUuid, item.uuid) : eventApi.checkInAttendee(eventUuid, item.uuid)"
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
            prepend-icon="mdi-email-fast"
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

  <v-dialog
    v-model="addAnon"
    persistent
    width="30rem"
  >
    <v-card>
      <v-form
        validate-on="submit"
        @submit.prevent="addAnonAcc"
      >
        <v-card-title class="text-h5">
          Unangemeldeter Teilnehmer
        </v-card-title>
        <v-divider />
        <v-card-text>
          <v-text-field
            class="mt-2"
            v-model="anonAcc.firstname"
            label="Vorname"
            prepend-inner-icon="mdi-account"
            :rules="[() => anonAcc.firstname !== '' || 'Dieses Feld wird benötigt.']"
            required
          ></v-text-field>
          <v-text-field
            class="mt-2"
            v-model="anonAcc.lastname"
            label="Nachname"
            prepend-inner-icon="mdi-account"
            :rules="[() => anonAcc.lastname !== '' || 'Dieses Feld wird benötigt.']"
            required
          ></v-text-field>
          <v-text-field
            class="mt-2"
            v-model="anonAcc.email"
            label="Mailadresse"
            prepend-inner-icon="mdi-email"
            :rules="[
              () => anonAcc.email !== '' || 'Dieses Feld wird benötigt.',
              () => anonAcc.email.match(VALIDATION.EMAIL) !== null || 'Die angegebene E-Mail-Adresse ist ungültig.'
              ]"
            required
          ></v-text-field>
        </v-card-text>
        <v-divider />
        <v-card-actions class="justify-end">
          <v-btn
            variant="flat"
            @click="addAnon = false"
          >
            Abbrechen
          </v-btn>
          <v-btn
            color="primary"
            variant="flat"
            type="submit"
            prepend-icon="mdi-check"
          >
            Hinzufügen
          </v-btn>
        </v-card-actions>
      </v-form>
    </v-card>
  </v-dialog>
</template>

<style scoped>

</style>
