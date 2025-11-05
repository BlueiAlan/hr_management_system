import request from '@/utils/request'

export const getPositionList = (params: any) => {
  return request({
    url: '/positions/page',
    method: 'get',
    params
    // params: params
  })
}

export const addPosition = (params: any) => {
  return request({
    url: '/positions',
    method: 'post',
    data: params
  })
}

export const queryById = (id: number) => {
  return request({
    url: `/positions/${id}`,
    method: 'get',
  })
}

export const updatePosition = (params: any) => {
  return request({
    url: '/positions',
    method: 'put',
    data: params
  })
}

export const deletePosition = (id: number) => {
  return request({
    url: `/positions/${id}`,
    method: 'delete',
  })
}
