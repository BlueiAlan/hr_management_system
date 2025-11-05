import router from './router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { Message } from 'element-ui'
import { Route } from 'vue-router'
import { UserModule } from '@/store/modules/user'
import Cookies from 'js-cookie'

// NProgress.configure({ 'showSpinner': false })
//
// router.beforeEach(async (to: Route, _: Route, next: any) => {
//   NProgress.start()
//   if (Cookies.get('token')) {
//     next()
//   } else {
//     if (!to.meta.notNeedAuth) {
//       next('/login')
//     } else {
//       next()
//     }
//   }
// })
//
//
// router.afterEach((to: Route) => {
//   NProgress.done()
//   // 如果路由没有指定标题，就不覆盖index.html中设置的标题
//   if (to.meta.title) {
//     document.title = to.meta.title
//   } else {
//     document.title = '人力资源管理系统'
//   }
// })
NProgress.configure({ 'showSpinner': false })

router.beforeEach(async (to: Route, _: Route, next: any) => {
  NProgress.start()

  // 首先检查是否有登录token
  if (Cookies.get('token')) {
    // 获取当前登录的用户名
    const username = Cookies.get('username') || ''

    // 检查是否访问系统设置相关路由
    // 在这个项目中，系统设置页面是positions/index.vue，路由路径为/dashboard
    if (to.path === '/dashboard' || to.meta.title === '系统设置') {
      // 只有用户名为admin时才能访问系统设置
      if (username !== 'admin') {
        Message.error('只有管理员才能访问系统设置！')
        // 重定向到首页或其他允许访问的页面
        next('/')
        NProgress.done()
        return
      }
    }

    next()
  } else {
    if (!to.meta.notNeedAuth) {
      next('/login')
    } else {
      next()
    }
  }
})

router.afterEach((to: Route) => {
  NProgress.done()
  // 如果路由没有指定标题，就不覆盖index.html中设置的标题
  if (to.meta.title) {
    document.title = to.meta.title
  } else {
    document.title = '人力资源管理系统'
  }
})
