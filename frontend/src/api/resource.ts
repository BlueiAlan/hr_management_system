import request from '@/utils/request'

/**
 * 人力资源档案管理API
 */

// 人力资源档案登记
export const registerResource = (data: any) => {
  return request({
    url: '/admin/employee',
    method: 'post',
    data: data
  })
}

// 人力资源档案查询（分页）
export const queryResourcePage = (params: any) => {
  return request({
    url: '/admin/employee/page',
    method: 'get',
    params: params
  })
}

// 根据ID查询人力资源档案详情
export const queryResourceById = (id: number) => {
  return request({
    url: `/admin/employee/${id}`,
    method: 'get'
  })
}

// 人力资源档案变更
export const updateResource = (data: any) => {
  return request({
    url: '/admin/employee',
    method: 'put',
    data: data
  })
}

// 人力资源档案登记复核
export const reviewResource = (id: number, reviewOpinion?: string) => {
  return request({
    url: `/admin/employee/review/${id}`,
    method: 'post',
    params: { reviewOpinion }
  })
}

// 人力资源档案登记复核（支持更新信息）
export const reviewResourceWithUpdate = (data: any, reviewOpinion?: string) => {
  return request({
    url: '/admin/employee/review-with-update',
    method: 'post',
    params: { reviewOpinion },
    data: data
  })
}

// 人力资源档案删除
export const deleteResource = (id: number) => {
  return request({
    url: `/admin/employee/${id}`,
    method: 'delete'
  })
}

// 人力资源档案恢复
export const restoreResource = (id: number) => {
  return request({
    url: `/admin/employee/restore/${id}`,
    method: 'post'
  })
}

