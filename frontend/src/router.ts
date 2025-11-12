import Vue from "vue";
import Router from "vue-router";
import Layout from "@/layout/index.vue";
// import {
//   getToken,
//   setToken,
//   removeToken,
//   getStoreId,
//   setStoreId,
//   removeStoreId,
//   setUserInfo,
//   getUserInfo,
//   removeUserInfo
// } from "@/utils/cookies";
// import store from "@/store";

Vue.use(Router);

const router = new Router({
    scrollBehavior: (to, from, savedPosition) => {
        if (savedPosition) {
            return savedPosition;
        }
        return { x: 0, y: 0 };
    },
    // 路由配置文件
    base: process.env.BASE_URL,
    routes: [
        {
            path: "/login",
            component: () => import("@/views/login/index.vue"),
            meta: { title: "人力资源管理系统", hidden: true, notNeedAuth: true }
        },
        {
            path: "/404",
            component: () => import("@/views/404.vue"),
            meta: { title: "人力资源管理系统", hidden: true, notNeedAuth: true }
        },
        {
            path: "/",
            component: Layout,
            redirect: "/dashboard",
            children: [
                {
                    path: "dashboard",
                    component: () => import("@/views/positions/index.vue"),
                    name: "Dashboard",
                    meta: {
                        title: "系统设置",
                        icon: "dashboard",
                        affix: true,
                        // notNeedAuth: true
                    }
                },
                {
                    path: "/positions/add",
                    component: () => import("@/views/positions/addPositions.vue"),
                    meta: {
                        // title: "添加/修改职位",
                        title: "系统设置",
                        hidden: true
                    }
                },
                {
                    path: "/organizations/:orgLevel",
                    component: () => import("@/views/organizations/index.vue"),
                    meta: {
                        // title: "机构管理",
                        title: "系统设置",
                        icon: "icon-employee",
                        hidden: true,
                        // affix: true
                    }
                },
                {
                    path: "/organizations/info",
                    name: "organizationInfo",
                    component: () => import("@/views/organizations/infoOrg.vue"),
                    meta: {
                        // title: "添加/修改机构",
                        title: "系统设置",
                        hidden: true
                    }
                },
                {
                    path: "/salaryItems",
                    component: () => import("@/views/salary/salaryItems.vue"),
                    meta: {
                        // title: "薪资项目管理",
                        title: "系统设置",
                        hidden: true
                    }
                },
                {
                    path: "/salaryItems/info",
                    component: () => import("@/views/salary/salaryItemsInfo.vue"),
                    meta: {
                        // title: "添加/修改薪资项目",
                        title: "系统设置",
                        hidden: true
                    }
                },
                {
                    path: "/employee",
                    component: () => import("@/views/employee/index.vue"),
                    meta: {
                        title: "员工管理",
                        icon: "icon-employee",
                        affix: true
                    }
                },
                {
                    path: "/employee/add",
                    component: () => import("@/views/employee/addEmployee.vue"),
                    meta: {
                        title: "员工管理",
                        hidden: true
                    }
                },
                {
                    path: "/salaryStandards",
                    component: () => import("@/views/salary/salaryStandards.vue"),
                    meta: {
                        title: "薪酬标准管理",
                        icon: "icon-order",
                        affix: true
                    }
                },
                {
                    path: "/salaryStandards/add",
                    component: () => import("@/views/salary/addSalaryStandard.vue"),
                    meta: {
                        title: "薪酬标准管理",
                        hidden: true
                    }
                },
                {
                    path: "/salaryStandards/detail",
                    component: () => import("@/views/salary/salaryStandardDetail.vue"),
                    meta: {
                        title: "薪酬标准管理",
                        hidden: true
                    }
                },
                {
                    path: "/salaryStandards/review",
                    component: () => import("@/views/salary/salaryStandardReview.vue"),
                    meta: {
                        title: "薪酬标准管理",
                        hidden: true
                    }
                },
                {
                    path: "/salaryIssues",
                    component: () => import("@/views/salary/salaryIssues.vue"),
                    meta: {
                        title: "薪酬发放管理",
                        icon: "icon-statistics",
                        affix: true
                    }
                },
                {
                    path: "/salaryIssues/add",
                    component: () => import("@/views/salary/addSalaryIssue.vue"),
                    meta: {
                        title: "薪酬发放管理",
                        hidden: true
                    }
                },
                {
                    path: "/salaryIssues/detail",
                    component: () => import("@/views/salary/salaryIssueDetail.vue"),
                    meta: {
                        title: "薪酬发放管理",
                        hidden: true
                    }
                },
                {
                    path: "/salaryIssues/review",
                    component: () => import("@/views/salary/salaryIssueReview.vue"),
                    meta: {
                        title: "薪酬发放管理",
                        hidden: true
                    }
                },
                {
                    path: "/resources",
                    component: () => import("@/views/resources/index.vue"),
                    meta: {
                        title: "人力资源档案管理",
                        icon: "icon-category",
                        affix: true
                    }
                },
                {
                    path: "/resources/register",
                    component: () => import("@/views/resources/register.vue"),
                    meta: {
                        title: "人力资源档案管理",
                        hidden: true
                    }
                },
                {
                    path: "/resources/review",
                    component: () => import("@/views/resources/review.vue"),
                    meta: {
                        title: "人力资源档案管理",
                        hidden: true
                    }
                },
                {
                    path: "/resources/update",
                    component: () => import("@/views/resources/update.vue"),
                    meta: {
                        title: "人力资源档案管理",
                        hidden: true
                    }
                },
                {
                    path: "/resources/delete",
                    component: () => import("@/views/resources/delete.vue"),
                    meta: {
                        title: "人力资源档案管理",
                        hidden: true
                    }
                },
                {
                    path: "/resources/detail",
                    component: () => import("@/views/resources/detail.vue"),
                    meta: {
                        title: "人力资源档案管理",
                        hidden: true
                    }
                }
            ]
        },
        {
            path: "*",
            redirect: "/404",
            meta: { hidden: true }
        }
    ]
});

export default router;
