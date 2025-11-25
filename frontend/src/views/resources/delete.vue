<template>
  <div class="delete-container">
    <div class="container">
      <h2 style="margin-bottom: 20px;">人力资源档案删除管理</h2>
      
      <el-table
        :data="records"
        style="width: 100%"
        :cell-style="{textAlign: 'center'}"
        :header-cell-style="{textAlign: 'center'}">
        <el-table-column prop="archiveNumber" label="档案编号" width="150"></el-table-column>
        <el-table-column prop="username" label="姓名" width="120"></el-table-column>
        <el-table-column prop="orgName" label="所属机构" width="150"></el-table-column>
        <el-table-column prop="positionName" label="职位" width="120"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === '正常'" type="success">{{ scope.row.status }}</el-tag>
            <el-tag v-else-if="scope.row.status === '已删除'" type="danger">{{ scope.row.status }}</el-tag>
            <el-tag v-else type="info">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="登记时间" width="180"></el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.status === '正常'"
              size="small"
              plain
              type="danger"
              @click="handleDelete(scope.row)">删除</el-button>
            <el-button
              v-if="scope.row.status === '已删除'"
              size="small"
              plain
              type="success"
              @click="handleRestore(scope.row)">恢复</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 20px; text-align: center;">
        <el-button @click="() => this.$router.push('/resources')" class="action-button">返回</el-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { queryResourcePage, deleteResource, restoreResource } from '@/api/resource'
import { filterAdminEmployees } from '@/utils/permission'
import { UserModule } from '@/store/modules/user'
import Cookies from 'js-cookie'

export default {
  data() {
    return {
      records: []
    }
  },

  created() {
    this.loadData()
  },

  methods: {
    loadData() {
      // 查询所有正常和已删除的档案，先查询正常的，再查询已删除的
      Promise.all([
        queryResourcePage({ pageNum: 1, pageSize: 1000, status: '正常' }),
        queryResourcePage({ pageNum: 1, pageSize: 1000, status: '已删除' })
      ]).then((results: any[]) => {
        let normalRecords = results[0].data.code == 200 ? results[0].data.data.records : []
        let deletedRecords = results[1].data.code == 200 ? results[1].data.data.records : []
        
        // 获取当前用户角色，过滤超级管理员信息
        const userInfo = Cookies.get('user_info') ? JSON.parse(Cookies.get('user_info') as string) : {}
        let currentUserRole = UserModule.role
        if (currentUserRole === undefined || currentUserRole === null) {
          currentUserRole = userInfo.role
        }
        if (currentUserRole === undefined || currentUserRole === null) {
          currentUserRole = 0
        }
        currentUserRole = Number(currentUserRole)
        
        // 非超级管理员用户看不到超级管理员的信息
        normalRecords = filterAdminEmployees(normalRecords, currentUserRole)
        deletedRecords = filterAdminEmployees(deletedRecords, currentUserRole)
        
        this.records = [...normalRecords, ...deletedRecords]
      }).catch((err) => {
        this.$message.error('获取档案列表失败')
      })
    },

    handleDelete(row: any) {
      if (row.status === '待复核') {
        this.$message.warning('状态为"待复核"的员工档案不能删除')
        return
      }
      this.$confirm('确认删除档案 ' + row.username + ' 吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteResource(row.id).then((res: any) => {
          if (res.data.code == 200) {
            this.$message.success('删除成功')
            this.loadData()
          } else {
            this.$message.error(res.data.msg || '删除失败')
          }
        }).catch((err) => {
          this.$message.error('删除失败：' + (err.response && err.response.data && err.response.data.msg ? err.response.data.msg : err.message))
        })
      }).catch(() => {
        // 用户点击取消
      })
    },

    handleRestore(row: any) {
      this.$confirm('确认恢复档案 ' + row.username + ' 吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        restoreResource(row.id).then((res: any) => {
          if (res.data.code == 200) {
            this.$message.success('恢复成功')
            this.loadData()
          } else {
            this.$message.error(res.data.msg || '恢复失败')
          }
        }).catch((err) => {
          this.$message.error('恢复失败：' + (err.response && err.response.data && err.response.data.msg ? err.response.data.msg : err.message))
        })
      }).catch(() => {
        // 用户点击取消
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.delete-container {
  margin: 30px;
  margin-top: 30px;
  .container {
    position: relative;
    z-index: 1;
    background: #fff;
    padding: 30px;
    border-radius: 4px;
  }
}

::v-deep .el-table__header-wrapper {
  th {
    background-color: rgba(96, 98, 102, 0.24) !important;
    color: #606266 !important;
    font-weight: bold !important;
  }
}

.action-button {
  background-color: transparent !important;
  color: #409EFF !important;
  border-color: #409EFF !important;
  &:hover {
    background-color: #ecf5ff !important;
    border-color: #409EFF !important;
    color: #409EFF !important;
  }
}
</style>

