import request from '@/utils/request'

export const getSalaryStandardList = (params: any) => {
  return request({
    url: '/admin/salary-standards/page',
    method: 'get',
    params
  })
}

export const addSalaryStandard = (params: any) => {
  return request({
    url: '/admin/salary-standards',
    method: 'post',
    data: params
  })
}

export const getSalaryStandardById = (id: number) => {
  return request({
    url: `/admin/salary-standards/${id}`,
    method: 'get',
  })
}

export const updateSalaryStandard = (params: any) => {
  return request({
    url: '/admin/salary-standards',
    method: 'put',
    data: params
  })
}

export const reviewSalaryStandard = (id: number, reviewOpinion: string, isApproved: boolean) => {
  return request({
    url: `/admin/salary-standards/review/${id}`,
    method: 'post',
    params: { reviewOpinion, isApproved }
  })
}

export const generateSalaryStandardNumber = () => {
  return request({
    url: '/admin/salary-standards/generate-number',
    method: 'get',
  })
}

