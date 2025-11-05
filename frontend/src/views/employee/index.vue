<template>
  <div class="dashboard-container">
    <div class="container">
      <!-- 制作页面头部效果 -->
      <div class="tableBer">
        <label style="margin-right: 5px">
          员工姓名：
        </label>
        <el-input placeholder="请输入员工姓名" v-model="username" style="width: 15%;" />
        <el-button type="primary" style="margin-left: 20px" @click="pageQuery">查询</el-button>
        <el-button type="primary" style="float: right" @click="handleAddEmp">+添加员工</el-button>
      </div>

        <el-table
        :cell-style="{'text-align':'center'}"
        :header-cell-style="{'text-align':'center'}"
        :data="records"
        stripe
        style="width: 100%;" >
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
              <el-button size="small" plain type="primary" @click="updateEmp(scope.row)">编辑</el-button>
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
      
    </div>
  </div>
</template>
<script lang="ts">

import { getEmployeeList, deleteEmployee, reviewEmployee } from '@/api/employee'
export default  {

  // 模型数据
  data() {
    return {
      username: '', // 员工姓名,使用双向绑定属性
      pageNum: 1,
      pageSize: 10,
      total: 0,
      records: []
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
      const params = {
        username: this.username,
        pageNum: this.pageNum,
        pageSize: this.pageSize
      }
      
      getEmployeeList(params).then((res: any) => {
        if (res.data.code == 200) {
          this.records = res.data.data.records
          this.total = res.data.data.total
        }
      }).catch((err: any) => {
        this.$message.error('请求失败' + err.message)
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

      this.$confirm('确认删除员工 ' + row.username + ' 吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteEmployee(row.id).then(res => {
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

::v-deep .el-table__header-wrapper {
  th {
    background-color: rgba(96, 98, 102, 0.24) !important;
    color: #606266 !important;
    font-weight: bold !important;
  }
}

</style>
