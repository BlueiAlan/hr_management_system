import router from './router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { Message } from 'element-ui'
import { Route } from 'vue-router'
import { UserModule } from '@/store/modules/user'
import Cookies from 'js-cookie'
import { Role, hasPermission, checkRoutePermission } from '@/utils/permission'

/**
 * 根据用户角色获取有权限的首页路径
 * @param userRole 用户角色
 * @returns 首页路径
 */
function getHomePathByRole(userRole: number): string {
    switch (userRole) {
        case Role.ADMIN: // 超级管理员 - dashboard被注释，使用position或resources
            return '/position'
        case Role.HR_SPECIALIST: // 人事专员 - employee被注释，使用resources
            return '/resources'
        case Role.HR_MANAGER: // 人事经理 - dashboard被注释，使用position或resources
            return '/position'
        case Role.SALARY_SPECIALIST: // 薪酬专员
            return '/salaryStandards'
        case Role.SALARY_MANAGER: // 薪酬经理
            return '/salaryStandards'
        default:
            return '/resources'
    }
}

NProgress.configure({ 'showSpinner': false })

router.beforeEach(async (to: Route, _: Route, next: any) => {
    NProgress.start()

    // 首先检查是否有登录token
    if (Cookies.get('token')) {
        // 获取用户角色
        const userInfo = Cookies.get('user_info') ? JSON.parse(Cookies.get('user_info') as string) : {}
        const userRole = UserModule.role || userInfo.role || 0

        // 如果访问根路径，根据用户角色重定向到对应的首页
        if (to.path === '/') {
            const homePath = getHomePathByRole(userRole)
            next(homePath)
            NProgress.done()
            return
        }

        // 如果路由需要权限验证（不是登录页、404等）
        if (!to.meta.notNeedAuth) {
            // 检查路由是否有权限要求
            if (to.meta.roles && Array.isArray(to.meta.roles)) {
                // 检查用户是否有访问该路由的权限
                if (!hasPermission(userRole, to.meta.roles as number[])) {
                    Message.error('您没有权限访问该页面！')
                    // 重定向到用户有权限的首页
                    const homePath = getHomePathByRole(userRole)
                    next(homePath)
                    NProgress.done()
                    return
                }
            } else {
                // 如果没有在meta中定义roles，使用路径检查权限（兼容旧代码）
                if (!checkRoutePermission(userRole, to.path)) {
                    Message.error('您没有权限访问该页面！')
                    // 重定向到用户有权限的首页
                    const homePath = getHomePathByRole(userRole)
                    next(homePath)
                    NProgress.done()
                    return
                }
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
