import request  from "@/utils/request";

export const getSalaryItemsList = () => {
  return request({
    url: '/admin/salary-items/list',
    method: 'get',
  })
}

export const addSalaryItem = (params: any) => {
  return request({
    url: '/admin/salary-items',
    method: 'post',
    data: params
  })
}

export const updateSalaryItem = (params: any) => {
  return request({
    url: '/admin/salary-items',
    method: 'put',
    data: params
  })
}

export const queryById = (id: number) => {
  return request({
    url: `/admin/salary-items/${id}`,
    method: 'get',
  })
}

export const deleteSalaryItem = (id: number) => {
  return request({
    url: `/admin/salary-items/${id}`,
    method: 'delete',
  })
}

