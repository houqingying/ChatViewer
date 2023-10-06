import { createRouter, createWebHistory } from 'vue-router'
import NavigateBar from '@/components/base/NavigateBar.vue'
import NoticeList from '@/views/notice/NoticeList.vue'
import EditorView from '@/views/article/EditorView.vue'
import AddProblemView from '@/views/problem/AddProblemView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'index',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/index/HomeView.vue')
    },
    {
      path: '/login',
      name: 'login',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/nav',
      name: 'nav',
      component: NavigateBar
    },
    {
      path: '/articles',
      name: 'articles',
      component: () => import('../views/article/ArticleListView.vue')
    },
    {
      path: '/editor',
      name: 'editor',
      component: EditorView
    },
    {
      path: '/category',
      name: 'category',
      component: () => import('../components/article/CategoryMenu.vue')
    },
    {
      path: '/articleDetail',
      name: 'articleDetail',
      component: () => import('@/views/article/ArticleDetailView.vue'),
    },
    {
      path: '/notices',
      name: 'notices',
      component: NoticeList,
    },
    {
      path: '/chatgpt',
      name: 'chatgpt',
      component: () => import('@/views/gpt/GPTView.vue')
    },
    {
      path: '/problems',
      name: 'problems',
      component: () => import('@/views/problem/ProblemListView.vue')
    },
    {
      path: '/problem',
      name: 'problem',
      component: () => import('@/views/problem/GPTTestView.vue')
    },
    {
      path: '/problem/add',
      name: 'addProblems',
      component: AddProblemView
    },
  ]
})

export default router
