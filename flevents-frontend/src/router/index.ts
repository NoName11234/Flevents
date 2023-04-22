// Composables
import {createRouter, createWebHistory, RouteLocationRaw} from 'vue-router'
import {useAppStore} from "@/store/app";
import {useAccountStore} from "@/store/account";
import security, {setAccount} from "@/service/security";
import {Account} from "@/models/account";
import {logout, tryRestoreSession} from "@/service/authService";

const routes = [
  {
    path: '/',
    redirect: '/home/personal',
  },
  {
    path: '/home',
    name: 'home',
    redirect: '/home/personal',
    component: () => import('@/layouts/default/BaseLayout.vue'),
    children: [
      {
        path: 'personal',
        name: 'home.personal',
        component: () => import(/* webpackChunkName: "home" */ '@/views/home/Personal.vue'),
      },
      {
        path: 'manage',
        name: 'home.manage',
        component: () => import(/* webpackChunkName: "home" */ '@/views/home/Manage.vue'),
      },
      {
        path: 'explore',
        name: 'home.explore',
        component: () => import(/* webpackChunkName: "home" */ '@/views/home/Explore.vue'),
      },
      {
        path: 'account',
        name: 'home.account',
        component: () => import(/* webpackChunkName: "home" */ '@/views/home/Account.vue'),
      },
    ],
  },

  {
    path: '/events',
    name: 'events',
    component: () => import('@/layouts/default/BaseLayout.vue'),
    children: [
      {
        path: 'create',
        name: 'events.create',
        component: () => import(/* webpackChunkName: "events" */ '@/views/event/Create.vue'),
      },
      {
        path: ':uuid',
        name: 'events.event',
        component: () => import(/* webpackChunkName: "events" */ '@/views/event/Overview.vue'),
      },
      {
        path: ':uuid/edit',
        name: 'events.edit',
        component: () => import(/* webpackChunkName: "events" */ '@/views/event/Edit.vue')
      },
      {
        path: ':uuid/addOrganizer',
        name: 'events.organizer',
        component: () => import(/* webpackChunkName: "events" */ '@/views/event/InviteOrganizer.vue')
      },
      {
        path: ':uuid/add',
        name: 'events.invite',
        component: () => import(/* webpackChunkName: "events" */ '@/views/event/Invite.vue')
      },
      // {
      //   path: ':uuid/edit',
      //   name: 'events.edit',
      //   component: () => import(/* webpackChunkName: "events" */ '@/views/event/Overview.vue'),
      // },
      {
        path: ':uuid/questionnaires/create',
        name: 'events.questionnaires.create',
        component: () => import(/* webpackChunkName: "questionnaires" */ '@/views/event/questionnaire/Create.vue'),
      },
      {
        path: ':uuid/questionnaires/:questionnaireUuid/edit',
        name: 'events.questionnaires.edit',
        component: () => import(/* webpackChunkName: "questionnaires" */ '@/views/event/questionnaire/Edit.vue'),
      },
    ],
  },
  {
    path: '/accounts',
    name: 'accounts',
    component: () => import('@/layouts/default/BaseLayout.vue'),
    children: [
      {
        path: 'create',
        name: 'accounts.create',
        meta: {
          public: true,
        },
        component: () => import(/* webpackChunkName: "accounts" */ '@/views/accounts/Create.vue'),
      },
      {
        path: 'edit',
        name: 'accounts.edit',
        component: () => import(/* webpackChunkName: "accounts" */ '@/views/accounts/Edit.vue'),
      },
      {
        path: 'login',
        name: 'accounts.login',
        meta: {
          public: true,
        },
        component: () => import(/* webpackChunkName: "accounts" */ '@/views/forms/Login.vue'),
      }
    ],
  },
  {
    path: '/join',
    name: 'join',
    component: () => import('@/layouts/default/SoftlockLayout.vue'),
    children: [
      {
        path: '/join/:uuid',
        name: 'join.uuid',
        meta: {
          public: true,
        },
        component: () => import(/* webpackChunkName: "join" */ '@/views/event/EventInvite.vue')
      }
    ]
  },
  {
    path: '/organizations/join',
    name: 'join.organization',
    component: () => import('@/layouts/default/SoftlockLayout.vue'),
    children: [
      {
        path: ':uuid',
        name: 'organizations.inviteLink',
        meta: {
          public: true,
        },
        component: () => import(/* webpackChunkName: "join" */ '@/views/organizations/OrganizationInviteLink.vue'),
      },
    ]
  },
  {
    path: '/organizations',
    name: 'organizations',
    component: () => import('@/layouts/default/BaseLayout.vue'),
    children: [
      {
        path: ':uuid',
        name: 'organizations.organization',
        component: () => import(/* webpackChunkName: "organizations" */ '@/views/organizations/Overview.vue'),
      },
      {
        path: ':uuid/edit',
        name: 'organizations.edit',
        component: () => import(/* webpackChunkName: "organizations" */ '@/views/organizations/Edit.vue'),
      },
      {
        path: ':uuid/add',
        name: 'organizations.invite',
        component: () => import(/* webpackChunkName: "organizations" */ '@/views/organizations/Invite.vue'),
      },
    ],
  },
  {
    path: '/errors',
    name: 'errors',
    component: () => import('@/layouts/default/BaseLayout.vue'),
    children: [
      {
        path: '/errors/404',
        name: 'errors.404',
        meta: {
          public: true,
        },
        component: () => import(/* webpackChunkName: "events" */ '@/views/error/404.vue')
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/errors/404',
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

let firstLoad = true;

/**
 * Navigation guard to check for logged-in status and redirect to /login.
 */
router.beforeEach(async (to, from) => {
  // console.log(document.cookie);
  // console.log(router.currentRoute.value);
  const loginRoute = {
    name: 'accounts.login',
    query: { location: encodeURIComponent(to.fullPath) }
  } as RouteLocationRaw;

  // If target route is public do not intercept
  if (to.meta?.public === true) {
    return true;
  }

  const appStore = useAppStore();
  const accountStore = useAccountStore();

  if (firstLoad) {
    firstLoad = false;
    // On first load, try to restore an eventually present previous session
    await tryRestoreSession();
  }

  if (!appStore.loggedIn) {
    // Not logged-in
    appStore.addToast('Für die angefragte Seite müssen Sie sich einloggen.');
    return loginRoute;
  }

  if (!accountStore.currentAccount) {
    // Logged-in but no account is loaded
    // Try to load an account:
    await accountStore.hydrate();
    if (accountStore.error) {
      // Logged-in but cannot load account
      // Log-out and get new re-login:
      await logout();
      return loginRoute;
    }
    // Logged-in and account loaded (previous session restored)
    return true;
  }
})
export default router
