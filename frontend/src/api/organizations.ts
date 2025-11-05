import request  from "@/utils/request";

export const getOrgList = (params: any) => {
  return request({
    url: '/admin/organizations/list',
    method: 'post',
    data: params
  })
}

export const getPage = (params: any) => {
  return request({
    url: '/admin/organizations/page',
    method: 'get',
    params
  })
}

export const queryById = (id: number) => {
  return request({
    url: `/admin/organizations/${id}`,
    method: 'get',
  })
}

export const addOrg = (params: any) => {
  return request({
    url: '/admin/organizations',
    method: 'post',
    data: params
  })
}

export const updateOrg = (params: any) => {
  return request({
    url: '/admin/organizations',
    method: 'put',
    data: params
  })
}
