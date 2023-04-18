// Composables
import { createRouter, createWebHistory } from 'vue-router'
import security from "@/service/security";

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

      // POSTS
      {
        path: ':uuid/posts/create',
        name: 'events.posts.create',
        component: () => import(/* webpackChunkName: "posts" */ '@/views/event/posts/Create.vue'),
      },
      {
        path: ':uuid/posts/:postUuid/edit',
        name: 'events.posts.edit',
        component: () => import(/* webpackChunkName: "posts" */ '@/views/event/posts/Edit.vue'),
      },

      // SURVEYS
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
})
router.beforeEach(async (to, from) => {
    // console.log(document.cookie);
    // console.log(router.currentRoute.value);
  if (security.getAccount() != null || to.path === "/accounts/login" || to.path === "/accounts/create" || to.path.includes("/join")){
    return true;
  } else {
    if(to.path.includes("/create") || from.path.includes("/create")){
      await router.push({path: '/accounts/login', query: {location: "/"}});
    }else{
      await router.push({path: '/accounts/login', query: {location: from.path}});
    }
  }
  return false;
})
export default router
