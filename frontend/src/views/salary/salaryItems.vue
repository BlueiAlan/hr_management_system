<script lang="ts">
import { getSalaryItemsList, deleteSalaryItem } from "@/api/salaryItems";

  export default {
    data() {
      return {
        records: [],
      }
    },
    created() {
      this.querySalaryItemsList()
    },
    methods: {
      querySalaryItemsList() {
        getSalaryItemsList().then(res => {
          if (res.data.code == 200) {
            this.records = res.data.data
          } else {
            this.$message.error('查询失败')
          }
        })
      },
      // 处理添加薪酬项目按钮点击事件
      handleAddSalaryItem() {
        this.$router.push({
          path: '/salaryItems/info',
        })
      },
      handleEdit(index, row) {
        this.$router.push({
          path: '/salaryItems/info',
          query: {
            id: row.id
          }
        })
      },
      handleDelete(index, row) {
        this.$confirm('确认删除薪酬项目 ' + row.positionName + ' 吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          // 发送删除请求
          deleteSalaryItem(row.id).then(res => {
            if (res.data.code == 200) {
              this.$message.success('删除成功')
              // 刷新列表
              this.querySalaryItemsList()
            } else {
              this.$message.error('删除失败')
            }
          })
        }).catch(() => {
          // 用户点击取消
        })
  }}
  }

</script>

<template>
  <div class="dashboard-container" >
    <div class="container" >
      <div class="header">
        <h2>薪资项目列表</h2>
<!--        <el-button type="primary" style="float: right; color:#FFFFFF;" class="button1" @click="handleAddSalaryItem()">+添加薪酬项目</el-button>-->
<!--        <el-button style="float: right;" @click="() => this.$router.push({ path: '/dashboard' })">返回</el-button>-->
        <div style="display: flex; gap: 10px;">
          <!-- 先放置添加项目按钮 -->
          <el-button type="primary" style="color:#FFFFFF;" class="button1" @click="handleAddSalaryItem">+添加薪酬项目</el-button>
          <!-- 再放置返回按钮，这样返回按钮就在添加项目按钮的右侧 -->
          <el-button @click="() => this.$router.push({ path: '/dashboard' })">返回</el-button>
        </div>
      </div>
      <!-- 表格容器，用于居中显示 -->
      <div class="table-container">
        <el-table
          :data="records"
          style="width: 80%"
          :cell-style="{textAlign: 'center'}"
          :header-cell-style="{textAlign: 'center'}">
          <el-table-column
            prop="itemNum"
            label="项目编号"
          >
          </el-table-column>
          <el-table-column
            prop="itemName"
            label="项目名称"
          >
          </el-table-column>
          <el-table-column label="操作">
            <template v-slot="scope">
              <el-button
                size="primary" plain
                class="action-button"
                icon="el-icon-edit"
                @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
              <el-button
                size="primary" plain
                type="danger"
                icon="el-icon-delete"
                class="action-button"
                @click="handleDelete(scope.$index, scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

    </div>
  </div>


</template>

<style scoped lang="scss">
.dashboard-container {
  width: 80% !important;
  margin: 0 auto !important;
}
.disabled-text {
  color: #BAC0CD !important;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 表头样式 - 添加灰色背景 */
::v-deep .el-table__header-wrapper {
  th {
    background-color: rgba(96, 98, 102, 0.24) !important;
    color: #606266 !important;
    font-weight: bold !important;
  }
}

/* 表格容器样式 - 实现表格在页面中居中 */
.table-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 表格单元格垂直居中 */
::v-deep .el-table td {
  font-size: 15px !important;
  vertical-align: middle !important;
}

/* 表头垂直居中 */
::v-deep .el-table th {
  vertical-align: middle !important;
}

/* 编辑按钮样式 - 橙色背景，白色文字和图标 */
.edit-button {
  background-color: #FF7A45 !important; /* 橙色 */
  border-color: #FF7A45 !important;
  color: #FFFFFF !important; /* 白色文字 */
}

.edit-button:hover {
  background-color: #FF9C6E !important; /* 橙色悬停效果 */
  border-color: #FF9C6E !important;
  color: #FFFFFF !important;
}

/* 确保编辑按钮的图标也是白色 */
.edit-button .el-icon-edit {
  color: #FFFFFF !important;
}

/* 删除按钮样式 - 红色背景，白色文字和图标 */
.delete-button {
  background-color: #F56C6C !important; /* 红色 */
  border-color: #F56C6C !important;
  color: #FFFFFF !important; /* 白色文字 */
}

.delete-button:hover {
  background-color: #F78989 !important; /* 红色悬停效果 */
  border-color: #F78989 !important;
  color: #FFFFFF !important;
}

/* 确保删除按钮的图标也是白色 */
.delete-button .el-icon-delete {
  color: #FFFFFF !important;
}

.button1{
  background-color: #409EFF !important;
  border-color: #409EFF !important;
}
.button1:hover {
  background-color: #66b1ff !important;
  border-color: #66b1ff !important;
}

</style>
