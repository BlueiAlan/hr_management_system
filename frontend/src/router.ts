import Vue from "vue";
import Router from "vue-router";
import Layout from "@/layout/index.vue";
import { Role } from "@/utils/permission";
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
            redirect: "/resources", // dashboard被注释，临时重定向到resources，实际会由路由守卫根据角色动态重定向
            children: [
                /*                {
                                    path: "dashboard",
                                    component: () => import("@/views/positions/index.vue"),
                                    name: "Dashboard",
                                    meta: {
                                        title: "系统设置",
                                        icon: "dashboard",
                                        affix: true,
                                        roles: [Role.HR_MANAGER, Role.ADMIN], // 职位设置：人事经理、超级管理员
                                        // notNeedAuth: true
                                    }
                                },
                              {
                                path: "/employee",
                                component: () => import("@/views/employee/index.vue"),
                                meta: {
                                  title: "员工管理",
                                  icon: "icon-employee",
                                  affix: true,
                                  roles: [Role.HR_SPECIALIST, Role.HR_MANAGER, Role.ADMIN] // 员工查询：人事专员、人事经理、超级管理员
                                }
                              },
                              {
                                path: "/employee/add",
                                component: () => import("@/views/employee/addEmployee.vue"),
                                meta: {
                                  title: "员工管理",
                                  hidden: true,
                                  roles: [Role.HR_SPECIALIST, Role.HR_MANAGER, Role.ADMIN] // 员工管理：人事专员、人事经理、超级管理员
                                }
                              },*/
                {
                    path: "/positions/add",
                    component: () => import("@/views/positions/addPositions.vue"),
                    meta: {
                        // title: "添加/修改职位",
                        title: "系统设置",
                        hidden: true,
                        roles: [Role.HR_MANAGER, Role.ADMIN] // 职位设置：人事经理、超级管理员
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
                        roles: [Role.HR_MANAGER, Role.ADMIN], // 机构关系设置：人事经理、超级管理员
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
                        hidden: true,
                        roles: [Role.HR_MANAGER, Role.ADMIN] // 机构关系设置：人事经理、超级管理员
                    }
                },
                {
                    path: "/salaryItems",
                    component: () => import("@/views/salary/salaryItems.vue"),
                    meta: {
                        // title: "薪资项目管理",
                        title: "系统设置",
                        hidden: true,
                        roles: [Role.SALARY_MANAGER, Role.ADMIN] // 薪酬项目设置：薪酬经理、超级管理员
                    }
                },
                {
                    path: "/salaryItems/info",
                    component: () => import("@/views/salary/salaryItemsInfo.vue"),
                    meta: {
                        // title: "添加/修改薪资项目",
                        title: "系统设置",
                        hidden: true,
                        roles: [Role.SALARY_MANAGER, Role.ADMIN] // 薪酬项目设置：薪酬经理、超级管理员
                    }
                },

                {
                    path: "/salaryStandards",
                    component: () => import("@/views/salary/salaryStandards.vue"),
                    meta: {
                        title: "薪酬标准管理",
                        icon: "icon-order",
                        affix: true,
                        roles: [Role.SALARY_SPECIALIST, Role.SALARY_MANAGER, Role.ADMIN] // 薪酬标准管理：薪酬专员、薪酬经理、超级管理员
                    }
                },
                {
                    path: "/salaryStandards/add",
                    component: () => import("@/views/salary/addSalaryStandard.vue"),
                    meta: {
                        title: "薪酬标准管理",
                        hidden: true,
                        roles: [Role.SALARY_SPECIALIST, Role.SALARY_MANAGER, Role.ADMIN] // 薪酬标准管理：薪酬专员、薪酬经理、超级管理员
                    }
                },
                {
                    path: "/salaryStandards/detail",
                    component: () => import("@/views/salary/salaryStandardDetail.vue"),
                    meta: {
                        title: "薪酬标准管理",
                        hidden: true,
                        roles: [Role.SALARY_SPECIALIST, Role.SALARY_MANAGER, Role.ADMIN] // 薪酬标准管理：薪酬专员、薪酬经理、超级管理员
                    }
                },
                {
                    path: "/salaryStandards/review",
                    component: () => import("@/views/salary/salaryStandardReview.vue"),
                    meta: {
                        title: "薪酬标准管理",
                        hidden: true,
                        roles: [Role.SALARY_SPECIALIST, Role.SALARY_MANAGER, Role.ADMIN] // 薪酬标准管理：薪酬专员、薪酬经理、超级管理员
                    }
                },
                {
                    path: "/salaryIssues",
                    component: () => import("@/views/salary/salaryIssues.vue"),
                    meta: {
                        title: "薪酬发放管理",
                        icon: "icon-statistics",
                        affix: true,
                        roles: [Role.SALARY_SPECIALIST, Role.SALARY_MANAGER, Role.ADMIN] // 薪酬发放管理：薪酬专员、薪酬经理、超级管理员
                    }
                },
                {
                    path: "/salaryIssues/add",
                    component: () => import("@/views/salary/addSalaryIssue.vue"),
                    meta: {
                        title: "薪酬发放管理",
                        hidden: true,
                        roles: [Role.SALARY_SPECIALIST, Role.SALARY_MANAGER, Role.ADMIN] // 薪酬发放管理：薪酬专员、薪酬经理、超级管理员
                    }
                },
                {
                    path: "/salaryIssues/detail",
                    component: () => import("@/views/salary/salaryIssueDetail.vue"),
                    meta: {
                        title: "薪酬发放管理",
                        hidden: true,
                        roles: [Role.SALARY_SPECIALIST, Role.SALARY_MANAGER, Role.ADMIN] // 薪酬发放管理：薪酬专员、薪酬经理、超级管理员
                    }
                },
                {
                    path: "/salaryIssues/review",
                    component: () => import("@/views/salary/salaryIssueReview.vue"),
                    meta: {
                        title: "薪酬发放管理",
                        hidden: true,
                        roles: [Role.SALARY_SPECIALIST, Role.SALARY_MANAGER, Role.ADMIN] // 薪酬发放管理：薪酬专员、薪酬经理、超级管理员
                    }
                },
                {
                    path: "/resources",
                    component: () => import("@/views/resources/index.vue"),
                    meta: {
                        title: "资源档案管理",
                        icon: "icon-category",
                        affix: true,
                        roles: [Role.HR_SPECIALIST, Role.HR_MANAGER, Role.ADMIN] // 人力资源档案查询：人事专员、人事经理、超级管理员
                    }
                },
                {
                    path: "/resources/register",
                    component: () => import("@/views/resources/register.vue"),
                    meta: {
                        title: "人力资源档案管理",
                        hidden: true,
                        roles: [Role.HR_SPECIALIST, Role.ADMIN] // 人力资源档案登记：人事专员、超级管理员
                    }
                },
                {
                    path: "/resources/review",
                    component: () => import("@/views/resources/review.vue"),
                    meta: {
                        title: "人力资源档案管理",
                        hidden: true,
                        roles: [Role.HR_MANAGER, Role.ADMIN] // 人力资源档案登记复核：人事经理、超级管理员
                    }
                },
                {
                    path: "/resources/update",
                    component: () => import("@/views/resources/update.vue"),
                    meta: {
                        title: "人力资源档案管理",
                        hidden: true,
                        roles: [Role.HR_SPECIALIST, Role.ADMIN] // 人力资源档案变更：人事专员、超级管理员
                    }
                },
                {
                    path: "/resources/delete",
                    component: () => import("@/views/resources/delete.vue"),
                    meta: {
                        title: "人力资源档案管理",
                        hidden: true,
                        roles: [Role.HR_MANAGER, Role.ADMIN] // 人力资源档案删除管理：人事经理、超级管理员
                    }
                },
                {
                    path: "/resources/detail",
                    component: () => import("@/views/resources/detail.vue"),
                    meta: {
                        title: "人力资源档案管理",
                        hidden: true,
                        roles: [Role.HR_SPECIALIST, Role.HR_MANAGER, Role.ADMIN] // 人力资源档案查询：人事专员、人事经理、超级管理员
                    }
                },
                {
                    path: "/position",
                    component: () => import("@/views/positions/index.vue"),
                    meta: {
                        title: "职务档案管理",
                        icon: "icon-employee",
                        affix: true,
                        roles: [Role.HR_MANAGER, Role.ADMIN] // 职位设置：人事经理、超级管理员
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
