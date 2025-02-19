import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: {
      title: '登录',
      public: true
    }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../components/layout/AppLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: {
          title: '数据面板',
          icon: 'DataLine'
        }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('../views/Users.vue'),
        meta: {
          title: '用户管理',
          icon: 'User'
        }
      },
      {
        path: 'devices',
        name: 'Devices',
        component: () => import('../views/Devices.vue'),
        meta: {
          title: '设备管理',
          icon: 'Monitor'
        }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('../views/Settings.vue'),
        meta: {
          title: '系统设置',
          icon: 'Setting'
        }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.meta.public) {
    next()
  } else if (!token && to.path !== '/login') {
    next('/login')
  } else {
    next()
  }
})

export default router
