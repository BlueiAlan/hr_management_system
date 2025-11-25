/**
 * API权限检查工具
 * 用于在API调用前检查用户是否有权限调用该接口
 * 
 * 注意：这是前端权限检查，后端也应该有相应的权限验证
 */

import { Role, PermissionConfig, hasPermission } from './permission'
import { UserModule } from '@/store/modules/user'
import Cookies from 'js-cookie'
import { Message } from 'element-ui'

/**
 * API权限映射
 * 根据URL路径和HTTP方法映射到权限配置
 */
const ApiPermissionMap: Record<string, keyof typeof PermissionConfig> = {
  // 机构管理相关
  'POST:/admin/organizations': 'ORGANIZATION_SETTING',
  'PUT:/admin/organizations': 'ORGANIZATION_SETTING',
  'GET:/admin/organizations': 'ORGANIZATION_SETTING',
  'DELETE:/admin/organizations': 'ORGANIZATION_SETTING',
  
  // 职位设置相关
  'POST:/admin/positions': 'POSITION_SETTING',
  'PUT:/admin/positions': 'POSITION_SETTING',
  'GET:/admin/positions': 'POSITION_SETTING',
  'DELETE:/admin/positions': 'POSITION_SETTING',
  
  // 薪酬项目设置
  'POST:/admin/salary-items': 'SALARY_ITEM_SETTING',
  'PUT:/admin/salary-items': 'SALARY_ITEM_SETTING',
  'GET:/admin/salary-items': 'SALARY_ITEM_SETTING',
  'DELETE:/admin/salary-items': 'SALARY_ITEM_SETTING',
  
  // 人力资源档案登记
  'POST:/admin/employee': 'RESOURCE_REGISTER',
  
  // 人力资源档案查询
  'GET:/admin/employee/page': 'RESOURCE_QUERY',
  'GET:/admin/employee/': 'RESOURCE_QUERY',
  
  // 人力资源档案变更
  'PUT:/admin/employee': 'RESOURCE_UPDATE',
  
  // 人力资源档案登记复核
  'POST:/admin/employee/review': 'RESOURCE_REVIEW',
  'POST:/admin/employee/review-with-update': 'RESOURCE_REVIEW',
  
  // 人力资源档案删除
  'DELETE:/admin/employee/': 'RESOURCE_DELETE',
  
  // 薪酬标准管理
  'POST:/admin/salary-standards': 'SALARY_STANDARD',
  'PUT:/admin/salary-standards': 'SALARY_STANDARD',
  'GET:/admin/salary-standards': 'SALARY_STANDARD',
  'POST:/admin/salary-standards/review': 'SALARY_STANDARD',
  
  // 薪酬发放管理
  'POST:/admin/salary-issues': 'SALARY_ISSUE',
  'PUT:/admin/salary-issues': 'SALARY_ISSUE',
  'GET:/admin/salary-issues': 'SALARY_ISSUE',
  'POST:/admin/salary-issues/review': 'SALARY_ISSUE',
  'POST:/admin/salary-issues/issue': 'SALARY_ISSUE',
}

/**
 * 从URL中提取权限键
 * @param url API URL
 * @param method HTTP方法
 * @returns 权限键，如果没有找到则返回null
 */
function getPermissionKey(url: string, method: string): keyof typeof PermissionConfig | null {
  // 移除baseURL和查询参数
  const cleanUrl = url.replace(process.env.VUE_APP_BASE_API || '', '').split('?')[0]
  
  // 尝试精确匹配
  const exactKey = `${method.toUpperCase()}:${cleanUrl}`
  if (ApiPermissionMap[exactKey]) {
    return ApiPermissionMap[exactKey]
  }
  
  // 尝试匹配带ID的路径（如 /admin/employee/123）
  for (const [key, permission] of Object.entries(ApiPermissionMap)) {
    const [keyMethod, keyPath] = key.split(':')
    if (keyMethod === method.toUpperCase()) {
      // 检查URL是否匹配路径模式
      if (keyPath.endsWith('/') && cleanUrl.startsWith(keyPath)) {
        return permission as keyof typeof PermissionConfig
      }
      // 检查是否匹配路径前缀（用于动态ID）
      const keyPathPrefix = keyPath.replace(/\/$/, '')
      if (cleanUrl.startsWith(keyPathPrefix + '/') || cleanUrl === keyPathPrefix) {
        return permission as keyof typeof PermissionConfig
      }
    }
  }
  
  return null
}

/**
 * 检查API调用权限
 * @param url API URL
 * @param method HTTP方法
 * @returns 是否有权限，true表示有权限或不需要权限检查
 */
export function checkApiPermission(url: string, method: string = 'GET'): boolean {
  // 获取用户角色
  const userInfo = Cookies.get('user_info') ? JSON.parse(Cookies.get('user_info') as string) : {}
  const userRole = UserModule.role || userInfo.role || 0
  
  // 超级管理员拥有所有权限
  if (userRole === Role.ADMIN) {
    return true
  }
  
  // 获取权限键
  const permissionKey = getPermissionKey(url, method)
  if (!permissionKey) {
    // 如果没有找到权限映射，默认允许（可能是新接口或不需要权限的接口）
    return true
  }
  
  // 检查权限
  const allowedRoles = PermissionConfig[permissionKey]
  return hasPermission(userRole, allowedRoles)
}

/**
 * API权限检查装饰器（用于在API调用前检查权限）
 * 如果权限检查失败，会显示错误消息并抛出异常
 * 
 * @param url API URL
 * @param method HTTP方法
 * @throws 如果权限检查失败，会抛出错误
 */
export function requireApiPermission(url: string, method: string = 'GET'): void {
  if (!checkApiPermission(url, method)) {
    Message.error('您没有权限执行此操作！')
    throw new Error('Permission denied')
  }
}

