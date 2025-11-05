import request from '@/utils/request'

export const getSalaryIssueList = (params: any) => {
  return request({
    url: '/admin/salary-issues/page',
    method: 'get',
    params
  })
}

export const addSalaryIssue = (params: any) => {
  return request({
    url: '/admin/salary-issues',
    method: 'post',
    data: params
  })
}

export const getSalaryIssueById = (id: number) => {
  return request({
    url: `/admin/salary-issues/${id}`,
    method: 'get',
  })
}

export const updateSalaryIssue = (params: any) => {
  return request({
    url: '/admin/salary-issues',
    method: 'put',
    data: params
  })
}

export const reviewSalaryIssue = (id: number, reviewOpinion: string, isApproved: boolean) => {
  return request({
    url: `/admin/salary-issues/review/${id}`,
    method: 'post',
    params: { reviewOpinion, isApproved }
  })
}

export const issueSalary = (id: number) => {
  return request({
    url: `/admin/salary-issues/issue/${id}`,
    method: 'post',
  })
}

export const generateSalaryIssueNumber = () => {
  return request({
    url: '/admin/salary-issues/generate-number',
    method: 'get',
  })
}

