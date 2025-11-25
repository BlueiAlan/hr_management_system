/**
 * 权限管理工具类
 * 根据 v3.md 中的权限分布进行权限控制
 * 
 * 角色定义：
 * 0 - 超级管理员 (admin)
 * 1 - 人事专员
 * 2 - 人事经理
 * 3 - 薪酬专员
 * 4 - 薪酬经理
 */

// 角色常量
export enum Role {
    ADMIN = 0,           // 超级管理员
    HR_SPECIALIST = 1,   // 人事专员
    HR_MANAGER = 2,      // 人事经理
    SALARY_SPECIALIST = 3, // 薪酬专员
    SALARY_MANAGER = 4   // 薪酬经理
}

// 角色名称映射
export const RoleNames: Record<number, string> = {
    [Role.ADMIN]: '超级管理员',
    [Role.HR_SPECIALIST]: '人事专员',
    [Role.HR_MANAGER]: '人事经理',
    [Role.SALARY_SPECIALIST]: '薪酬专员',
    [Role.SALARY_MANAGER]: '薪酬经理'
}

/**
 * 权限配置映射
 * 根据 v3.md 中的权限分布定义
 */
export const PermissionConfig = {
    // 系统管理 - 机构关系设置
    ORGANIZATION_SETTING: [Role.HR_MANAGER, Role.ADMIN],

    // 系统管理 - 职位设置
    POSITION_SETTING: [Role.HR_MANAGER, Role.ADMIN],

    // 系统管理 - 薪酬项目设置
    SALARY_ITEM_SETTING: [Role.SALARY_MANAGER, Role.ADMIN],

    // 人力资源档案管理 - 人力资源档案登记
    RESOURCE_REGISTER: [Role.HR_SPECIALIST, Role.ADMIN],

    // 人力资源档案管理 - 人力资源档案登记复核
    RESOURCE_REVIEW: [Role.HR_MANAGER, Role.ADMIN],

    // 人力资源档案管理 - 人力资源档案查询
    RESOURCE_QUERY: [Role.HR_SPECIALIST, Role.HR_MANAGER, Role.ADMIN],

    // 人力资源档案管理 - 人力资源档案变更
    RESOURCE_UPDATE: [Role.HR_SPECIALIST, Role.ADMIN],

    // 人力资源档案管理 - 人力资源档案删除管理
    RESOURCE_DELETE: [Role.HR_MANAGER, Role.ADMIN],

    // 薪酬管理 - 薪酬标准管理
    SALARY_STANDARD: [Role.SALARY_SPECIALIST, Role.SALARY_MANAGER, Role.ADMIN],

    // 薪酬管理 - 薪酬发放管理
    SALARY_ISSUE: [Role.SALARY_SPECIALIST, Role.SALARY_MANAGER, Role.ADMIN],

    // 员工管理（查询）
    EMPLOYEE_QUERY: [Role.HR_SPECIALIST, Role.HR_MANAGER, Role.ADMIN]
}

/**
 * 检查用户是否有指定权限
 * @param userRole 用户角色
 * @param allowedRoles 允许的角色数组
 * @returns 是否有权限
 */
export function hasPermission(userRole: number, allowedRoles: number[]): boolean {
    // 超级管理员拥有所有权限
    if (userRole === Role.ADMIN) {
        return true
    }
    return allowedRoles.includes(userRole)
}

/**
 * 检查用户是否有指定权限（通过权限键）
 * @param userRole 用户角色
 * @param permissionKey 权限键（PermissionConfig 的 key）
 * @returns 是否有权限
 */
export function checkPermission(userRole: number, permissionKey: keyof typeof PermissionConfig): boolean {
    const allowedRoles = PermissionConfig[permissionKey]
    return hasPermission(userRole, allowedRoles)
}

/**
 * 检查用户是否有任意一个权限
 * @param userRole 用户角色
 * @param permissionKeys 权限键数组
 * @returns 是否有权限
 */
export function hasAnyPermission(userRole: number, permissionKeys: Array<keyof typeof PermissionConfig>): boolean {
    return permissionKeys.some(key => checkPermission(userRole, key))
}

/**
 * 检查用户是否有所有权限
 * @param userRole 用户角色
 * @param permissionKeys 权限键数组
 * @returns 是否有权限
 */
export function hasAllPermissions(userRole: number, permissionKeys: Array<keyof typeof PermissionConfig>): boolean {
    return permissionKeys.every(key => checkPermission(userRole, key))
}

/**
 * 根据路由路径获取需要的权限
 * @param path 路由路径
 * @returns 权限键数组
 */
export function getRequiredPermissions(path: string): Array<keyof typeof PermissionConfig> {
    const permissions: Array<keyof typeof PermissionConfig> = []

    // 系统管理相关
    if (path.includes('/organizations') || path === '/dashboard' || path.includes('/positions')) {
        if (path.includes('/organizations')) {
            permissions.push('ORGANIZATION_SETTING')
        }
        if (path.includes('/positions') || path === '/dashboard') {
            permissions.push('POSITION_SETTING')
        }
    }

    // 薪酬项目设置
    if (path.includes('/salaryItems')) {
        permissions.push('SALARY_ITEM_SETTING')
    }

    // 人力资源档案管理
    if (path.includes('/resources')) {
        if (path.includes('/register')) {
            permissions.push('RESOURCE_REGISTER')
        } else if (path.includes('/review')) {
            permissions.push('RESOURCE_REVIEW')
        } else if (path.includes('/update')) {
            permissions.push('RESOURCE_UPDATE')
        } else if (path.includes('/delete')) {
            permissions.push('RESOURCE_DELETE')
        } else {
            // 查询页面
            permissions.push('RESOURCE_QUERY')
        }
    }

    // 员工管理
    if (path.includes('/employee')) {
        permissions.push('EMPLOYEE_QUERY')
    }

    // 薪酬标准管理
    if (path.includes('/salaryStandards')) {
        permissions.push('SALARY_STANDARD')
    }

    // 薪酬发放管理
    if (path.includes('/salaryIssues')) {
        permissions.push('SALARY_ISSUE')
    }

    return permissions
}

/**
 * 检查路由访问权限
 * @param userRole 用户角色
 * @param path 路由路径
 * @returns 是否有权限
 */
export function checkRoutePermission(userRole: number, path: string): boolean {
    const requiredPermissions = getRequiredPermissions(path)
    if (requiredPermissions.length === 0) {
        // 没有权限要求，允许访问
        return true
    }
    return hasAnyPermission(userRole, requiredPermissions)
}

/**
 * 过滤员工列表中的超级管理员信息
 * 非超级管理员用户无法看到超级管理员的信息
 * 
 * @param employees 员工列表
 * @param currentUserRole 当前登录用户的角色（必须传入）
 * @returns 过滤后的员工列表
 */
export function filterAdminEmployees<T extends { role?: number }>(
    employees: T[],
    currentUserRole: number
): T[] {
    // 确保 currentUserRole 是数字类型
    const userRole = Number(currentUserRole)

    // 如果当前用户是超级管理员，可以看到所有员工
    if (userRole === Role.ADMIN || userRole === 0) {
        return employees
    }

    // 非超级管理员用户，过滤掉所有超级管理员（role === 0）
    return employees.filter(employee => {
        // 如果 role 不存在、为 null 或 undefined，保留该记录（可能是普通员工，role字段可能未设置）
        if (employee.role == null || employee.role === undefined) {
            return true
        }

        // 将 role 转换为数字进行比较，确保类型一致（处理字符串"0"的情况）
        const roleValue = Number(employee.role)

        // 如果是 NaN（转换失败），保留该记录
        if (isNaN(roleValue)) {
            return true
        }

        // 过滤掉超级管理员（role === 0），只保留非超级管理员
        // 使用严格判断：roleValue 必须等于 0 才过滤掉
        const isAdmin = roleValue === 0 || roleValue === Role.ADMIN
        return !isAdmin
    })
}

