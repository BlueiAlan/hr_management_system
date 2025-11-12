import request from '@/utils/request'
/**
 *
 * 员工管理
 *
 **/
// 登录
export const login = (data: any) =>
  request({
    'url': '/admin/employee/login',
    'method': 'post',
    data: data
  })

  // 退出
 export const userLogout = (params: any) =>
 request({
   'url': `/admin/employee/logout`,
   'method': 'post',
   params
 })

 export const getEmployeeList = (params: any) =>
  request({
    'url': '/admin/employee/page',
    'method': 'get',
    // 如果相同可以省略后只剩一个参数
    'params': params
  })


  export const startOrStopEmployee = (params: any) =>
  request({
      'url': `/admin/employee/status/${params.status}`,
      'method': 'post',
      'params': { id: params.id }
  })

  export const addEmployee = (params: any) =>
  request({
      'url': '/admin/employee',
      'method': 'post',
      // 请求体传参，用data
      'data': params
  })

  export const queryEmpById = (id: number) =>
  request({
      'url': `/admin/employee/${id}`,
      'method': 'get',
  })

  export const updateEmployee = (params: any) =>
  request({
      'url': '/admin/employee',
      'method': 'put',
      'data': params
  })

  export const deleteEmployee = (id: number, deleteMsg?: string) =>
  request({
      'url': `/admin/employee/${id}`,
      'method': 'delete',
      params: { deleteMsg }
  })

  export const restoreEmployee = (id: number) =>
  request({
      'url': `/admin/employee/restore/${id}`,
      'method': 'post',
  })

  export const reviewEmployee = (id: number, reviewOpinion?: string) =>
  request({
      'url': `/admin/employee/review/${id}`,
      'method': 'post',
      'params': { reviewOpinion }
  })