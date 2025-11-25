<template>
  <div class="dashboard-container">
    <div class="container">
      <!-- 制作页面头部效果 -->
      <div class="tableBer">
        <label style="margin-right: 5px">
          员工姓名：
        </label>
        <el-input placeholder="请输入员工姓名" v-model="username" style="width: 15%;" />
        <el-button type="primary" style="margin-left: 20px;color: #FFFFFF" @click="pageQuery" class="button1" >查询</el-button>
        <el-button type="primary" style="float: right; color:#FFFFFF;" class="button1" @click="handleAddEmp">+添加员工</el-button>
      </div>

      <!-- 正常员工表格 -->
      <el-table
        :cell-style="{'text-align':'center'}"
        :header-cell-style="{'text-align':'center'}"
        :data="normalRecords"
        class="table--border"
        stripe
        style="width: 100%; margin-top: 20px;" >
          <el-table-column
            prop="username"
            label="姓名"
            width="120">
          </el-table-column>
          <el-table-column
            prop="archiveNumber"
            label="档案编号"
            width="150">
          </el-table-column>
          <el-table-column
            prop="mobile"
            label="手机号"
            width="120">
          </el-table-column>
          <el-table-column
            prop="orgName"
            label="所属机构"
            width="150">
          </el-table-column>
          <el-table-column
            prop="positionName"
            label="职位"
            width="120">
          </el-table-column>
          <el-table-column
            prop="status"
            label="状态"
            width="100">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.status === '正常'" type="success">{{ scope.row.status }}</el-tag>
              <el-tag v-else-if="scope.row.status === '待复核'" type="warning">{{ scope.row.status }}</el-tag>
              <el-tag v-else type="info">{{ scope.row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column
            prop="createdAt"
            label="登记时间"
            width="180">
          </el-table-column>
          <el-table-column
            label="操作"
            width="280">
            <template slot-scope="scope">
              <el-button 
                size="small" 
                plain 
                type="primary" 
                @click="updateEmp(scope.row)">编辑</el-button>
              <el-button
                v-if="scope.row.status === '待复核'"
                size="small"
                plain
                type="success"
                @click="reviewEmp(scope.row)">复核</el-button>
              <el-button
                size="small"
                plain
                type="danger"
                @click="deleteEmp(scope.row)">删除</el-button>
            </template>
          </el-table-column>
      </el-table>
      <el-pagination
          class="pageList"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[5, 10, 20, 30, 40, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>

      <!-- 已删除员工容器 -->
      <div v-if="deletedRecords.length > 0" class="deleted-container" style="margin-top: 40px;">
        <h3 style="margin-bottom: 20px; color: #606266;">已删除员工</h3>
        <el-table
          :cell-style="{'text-align':'center'}"
          :header-cell-style="{'text-align':'center'}"
          :data="deletedRecords"
          class="table--border"
          stripe
          style="width: 100%;" >
            <el-table-column
              prop="archiveNumber"
              label="档案编号"
              width="150">
              <template slot-scope="scope">
                <span style="color: #409EFF;">{{ scope.row.archiveNumber }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="username"
              label="姓名"
              width="120">
            </el-table-column>
            <el-table-column
              prop="deletedAt"
              label="删除时间"
              width="180">
              <template slot-scope="scope">
                <span v-if="scope.row.deletedAt">{{ formatDateTime(scope.row.deletedAt) }}</span>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="deleteMsg"
              label="删除原因"
              width="150">
              <template slot-scope="scope">
                <span>{{ scope.row.deleteMsg || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="status"
              label="状态"
              width="100">
              <template slot-scope="scope">
                <el-tag type="danger">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column
              label="操作"
              width="200">
              <template slot-scope="scope">
                <el-button
                  size="small"
                  plain
                  type="success"
                  @click="restoreEmp(scope.row)">
                  <i class="el-icon-refresh" style="margin-right: 5px;"></i>恢复</el-button>
              </template>
            </el-table-column>
        </el-table>
      </div>

    </div>
  </div>
</template>
<script lang="ts">

import { getEmployeeList, deleteEmployee, reviewEmployee, restoreEmployee } from '@/api/employee'
import { filterAdminEmployees, Role } from '@/utils/permission'
import { UserModule } from '@/store/modules/user'
import Cookies from 'js-cookie'

export default  {

  // 模型数据
  data() {
    return {
      username: '', // 员工姓名,使用双向绑定属性
      pageNum: 1,
      pageSize: 10,
      total: 0,
      records: [],
      normalRecords: [], // 正常状态的员工
      deletedRecords: [] // 已删除的员工
    }
  },


// 生命周期
  created() {
    // 页面加载完成后，发送请求
    this.pageQuery()
  },
  methods: {
    // 发送Ajax请求，访问后端数据，应该在api下的ts文件中写接口，这里只是调用
    pageQuery() {

      // 请求参数
      const params: any = {
        username: this.username,
        pageNum: this.pageNum,
        pageSize: this.pageSize
      }

      // 查询正常状态的员工（排除已删除）
      const normalParams: any = {
        username: this.username,
        pageNum: this.pageNum,
        pageSize: this.pageSize
      }
      // 不传status参数，后端会自动排除已删除的记录
      getEmployeeList(normalParams).then((res: any) => {
        if (res.data.code == 200) {
          this.records = res.data.data.records
          // 过滤掉已删除的员工（双重保险）
          let filteredRecords = this.records.filter((record: any) => record.status !== '已删除')
          
          // 获取当前用户角色，过滤超级管理员信息
          const userInfo = Cookies.get('user_info') ? JSON.parse(Cookies.get('user_info') as string) : {}
          // 确保获取到正确的角色值，优先使用 UserModule.role，其次使用 userInfo.role
          // 如果都没有，默认为 0（超级管理员），但这种情况应该不会发生
          let currentUserRole = UserModule.role
          if (currentUserRole === undefined || currentUserRole === null) {
            currentUserRole = userInfo.role
          }
          if (currentUserRole === undefined || currentUserRole === null) {
            currentUserRole = 0 // 默认值，但这种情况不应该发生
          }
          // 确保是数字类型
          currentUserRole = Number(currentUserRole)
          
          // 调试信息（生产环境可以删除）
          // console.log('当前用户角色:', currentUserRole)
          // console.log('过滤前员工数量:', filteredRecords.length)
          // console.log('员工角色列表:', filteredRecords.map((r: any) => ({ username: r.username, role: r.role })))
          
          // 非超级管理员用户看不到超级管理员的信息
          filteredRecords = filterAdminEmployees(filteredRecords, currentUserRole)
          
          // 调试信息（生产环境可以删除）
          // console.log('过滤后员工数量:', filteredRecords.length)
          // console.log('过滤后员工角色列表:', filteredRecords.map((r: any) => ({ username: r.username, role: r.role })))
          
          this.normalRecords = filteredRecords
          
          // 总数处理：由于是前端过滤，无法准确知道所有页面的超级管理员数量
          // 如果当前用户是超级管理员，使用后端返回的 total
          // 如果当前用户不是超级管理员，使用过滤后的当前页数据长度作为参考
          // 注意：这可能导致分页总数不完全准确，但数据是正确的
          if (currentUserRole === 0) {
            // 超级管理员可以看到所有数据
            this.total = res.data.data.total
          } else {
            // 非超级管理员：由于无法准确计算所有页面的超级管理员数量
            // 这里使用后端返回的 total，但实际显示的数据是过滤后的
            // 分页可能不完全准确，但数据是正确的
            this.total = res.data.data.total
          }
        }
      }).catch((err: any) => {
        this.$message.error('请求失败' + err.message)
      })

      // 查询已删除的员工（单独查询，不分页）
      const deletedParams: any = {
        username: this.username,
        pageNum: 1,
        pageSize: 1000,
        status: '已删除'
      }
      getEmployeeList(deletedParams).then((res: any) => {
        if (res.data.code == 200) {
          let deletedRecords = res.data.data.records || []
          
          // 获取当前用户角色，过滤超级管理员信息
          const userInfo = Cookies.get('user_info') ? JSON.parse(Cookies.get('user_info') as string) : {}
          const currentUserRole = UserModule.role || userInfo.role || 0
          // 非超级管理员用户看不到超级管理员的信息
          deletedRecords = filterAdminEmployees(deletedRecords, currentUserRole)
          
          this.deletedRecords = deletedRecords
        }
      }).catch((err: any) => {
        // 已删除员工查询失败不影响主流程
        console.error('查询已删除员工失败', err)
      })
    },

    handleSizeChange(pageSize) {
      this.pageSize = pageSize
      this.pageQuery()
    },

    handleCurrentChange(page) {
      this.pageNum = page
      this.pageQuery()
    },

    // 添加员工
    handleAddEmp() {
      // 路由跳转页面
      this.$router.push('/employee/add')
    },

    updateEmp(row) {
      if(row.username === 'admin') {
        this.$message.error('超级管理员账号不允许操作')
        return
      }

      // 跳转同时传参，通过地址栏传参
      this.$router.push(
        {
          path: '/employee/add',
          query: {
            id: row.id
          }
        }
      )
    },

    // 复核员工
    reviewEmp(row) {
      this.$confirm('确认复核员工 ' + row.username + ' 吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        reviewEmployee(row.id).then(res => {
          if (res.data.code == 200) {
            this.$message.success('复核成功')
            this.pageQuery()
          } else {
            this.$message.error('复核失败')
          }
        }).catch(err => {
          this.$message.error('请求失败' + err.message)
        })
      }).catch(() => {
        // 用户点击取消
      })
    },

    // 删除员工
    deleteEmp(row) {
      if(row.username === 'admin') {
        this.$message.error('超级管理员账号不允许操作')
        return
      }

      this.$prompt('请输入删除原因', '删除员工 ' + row.username, {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '请输入删除原因，如：员工离职',
        inputValidator: (value) => {
          if (!value || value.trim() === '') {
            return '删除原因不能为空'
          }
          return true
        }
      }).then(({ value }) => {
        deleteEmployee(row.id, value).then(res => {
          if (res.data.code == 200) {
            this.$message.success('删除成功')
            this.pageQuery()
          } else {
            this.$message.error('删除失败')
          }
        }).catch(err => {
          this.$message.error('请求失败' + err.message)
        })
      }).catch(() => {
        // 用户点击取消
      })
    },

    // 恢复员工
    restoreEmp(row) {
      this.$confirm('确认恢复员工 ' + row.username + ' 吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        restoreEmployee(row.id).then(res => {
          if (res.data.code == 200) {
            this.$message.success('恢复成功')
            this.pageQuery()
          } else {
            this.$message.error('恢复失败')
          }
        }).catch(err => {
          this.$message.error('请求失败' + err.message)
        })
      }).catch(() => {
        // 用户点击取消
      })
    },

    // 格式化日期时间
    formatDateTime(dateTime: string) {
      if (!dateTime) return '-'
      const date = new Date(dateTime)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}`
    }
  }
}
</script>

<style lang="scss" scoped>
.disabled-text {
  color: #bac0cd !important;
}

.table-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.table--border {
  margin-top: 20px;
}

::v-deep .el-table__header-wrapper {
  th {
    background-color: rgba(96, 98, 102, 0.24) !important;
    color: #606266 !important;
    font-weight: bold !important;
  }

}

.button1{
  background-color: #409EFF !important;
  border-color: #409EFF !important;
}
.button1:hover {
  background-color: #66b1ff !important;
  border-color: #66b1ff !important;
}

/* 操作按钮样式 - 恢复到原始plain按钮样式 */
.action-button {
  // 确保按钮保持plain样式
  background-color: transparent !important;
  color: #409EFF !important;
  border-color: #409EFF !important;

  // 悬停时的高亮效果
  &:hover {
    background-color: #ecf5ff !important;
    border-color: #409EFF !important;
    color: #409EFF !important;
  }

  // 点击后恢复到原始plain样式
  &:active, &:focus {
    background-color: transparent !important;
    color: #409EFF !important;
    border-color: #409EFF !important;
    outline: none !important;
    box-shadow: none !important;
  }
}

/* 危险按钮样式 - 恢复到原始plain按钮样式 */
.action-button.el-button--danger {
  // 确保按钮保持plain样式
  background-color: transparent !important;
  color: #F56C6C !important;
  border-color: #F56C6C !important;

  // 悬停时的高亮效果
  &:hover {
    background-color: #fef0f0 !important;
    border-color: #F56C6C !important;
    color: #F56C6C !important;
  }

  // 点击后恢复到原始plain样式
  &:active, &:focus {
    background-color: transparent !important;
    color: #F56C6C !important;
    border-color: #F56C6C !important;
    outline: none !important;
    box-shadow: none !important;
  }
}

/* 已删除员工容器样式 */
.deleted-container {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 2px solid #e4e7ed;
}

.deleted-container h3 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 20px;
}

</style>
