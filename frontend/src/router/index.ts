import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/login/index.vue')
    },
    {
      path: '/',
      component: () => import('../components/Layout.vue'),
      redirect: '/dashboard',
      children: [
        { path: 'dashboard', name: 'Dashboard', component: () => import('../views/dashboard/index.vue') },
        { path: 'apps', name: 'Apps', component: () => import('../views/apps/index.vue') },
        { path: 'tasks', name: 'Tasks', component: () => import('../views/tasks/index.vue') },
        { path: 'fonts', name: 'Fonts', component: () => import('../views/fonts/index.vue') },
        { path: 'settings', name: 'Settings', component: () => import('../views/settings/index.vue') }
      ]
    }
  ]
})

export default router
