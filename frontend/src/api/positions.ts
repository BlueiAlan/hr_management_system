import request from '@/utils/request'

export const getPositionList = (params: any) => {
  return request({
    url: '/admin/positions/page',
    method: 'get',
    params
    // params: params
  })
}

export const addPosition = (params: any) => {
  return request({
    url: '/admin/positions',
    method: 'post',
    data: params
  })
}

export const queryById = (id: number) => {
  return request({
    url: `/admin/positions/${id}`,
    method: 'get',
  })
}

export const updatePosition = (params: any) => {
  return request({
    url: '/admin/positions',
    method: 'put',
    data: params
  })
}

export const deletePosition = (id: number) => {
  return request({
    url: `/admin/positions/${id}`,
    method: 'delete',
  })
}
