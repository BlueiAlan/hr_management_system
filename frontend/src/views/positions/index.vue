<template>
  <div class="dashboard-container">
    <div class="container">
      <div class="tableBar">
        <label style="margin-right: 5px" >职位名称：</label>
        <el-input v-model="name"  placeholder="请输入职位名称" style="width: 15%"></el-input>
        <el-button type="primary" style="margin-left: 25px; color: #FFFFFF" @click="pageQuery() " class="button1">查询职位</el-button>
        <el-button type="primary" style="float: right; color:#FFFFFF;" class="button1" @click="handleAddPosition()">+添加职位</el-button>
        <!-- 添加change事件监听 -->
        <el-select v-model="value" clearable placeholder="所属机构" style="float: right; margin-right: 10px" @change="handleOrgSelect">
          <el-option
            v-for="item in options"
            :key="item.orgLevel"
            :label="item.label"
            :value="item.orgLevel">
          </el-option>
        </el-select>
        <el-button  style="float: right; margin-right: 25px; width: 120px" class="" @click="() => {this.$router.push({path: '/salaryItems'})}">薪酬项目列表</el-button>

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
          prop="positionName"
          label="职位名称"
          >
        </el-table-column>
        <el-table-column
          prop="fromOrg"
          label="所属机构"
          >
        </el-table-column>
        <el-table-column label="操作">
          <template v-slot="scope">
            <el-button
              size="primary" plain
              class="action-button"
              @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
            <el-button
              size="primary" plain
              type="danger"
              class="action-button"
              @click="handleDelete(scope.$index, scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pageNum"
      :page-sizes="[10, 20, 30, 40, 50, 100]"
      :page-size="10"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
    style="margin-top: 20px; text-align: center">
    </el-pagination>
  </div>
</template>

<script lang="ts">

import { getPositionList, deletePosition } from '@/api/positions'

export default {

  data(){
    return {
      name: "",
      pageNum: 1,
      pageSize: 10,
      total: 0, // 总记录数
      totalPage: 0, // 总页数
      records: [],
      options: [{
        orgLevel: 1,
        label: '一级机构'
      },
      {
        orgLevel: 2,
        label: '二级机构'
      },
        {
          orgLevel: 3,
          label: '三级机构'
        }
      ]
    }
  },
  created() {
    this.pageQuery();
  },

  methods: {
    pageQuery() {
      // 构建查询参数
      const params = {
        positionName: this.name,
        pageNum: this.pageNum,
        pageSize: this.pageSize
      }
      // 发送ajax请求，访问后端服务，获取分页数据
      getPositionList(params).then(res => {
        if (res.data.code == 200) {
          // 处理成功响应
          this.records = res.data.data.records
          this.total = res.data.data.total
          this.totalPage = res.data.data.totalPage
        }
      }).catch(err => {
        this.$message.error('请求出错，请联系管理员')
      })
    },
    // 分页大小改变时触发
    handleSizeChange(val: number) {
      this.pageSize = val
      this.pageQuery()
    },
    // 分页当前页改变时触发
    handleCurrentChange(val: number) {
      this.pageNum = val
      this.pageQuery()
    },
    // 处理添加职位按钮点击事件
    handleAddPosition() {
      this.$router.push({ path: '/positions/add' })
    },
    handleEdit(index, row) {
      this.$router.push({ path: '/positions/add', query: { id: row.id } })
    },
    handleDelete(index, row) {
      this.$confirm('确认删除职位 ' + row.positionName + ' 吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 发送删除请求
        deletePosition(row.id).then(res => {
          if (res.data.code == 200) {
            this.$message.success('删除成功')
            // 刷新列表
            this.pageQuery()
          } else {
            this.$message.error('删除失败')
          }
        })
      }).catch(() => {
        // 用户点击取消
      })
    },

    // 新增：处理机构选择的方法
    handleOrgSelect(orgLevel) {
      // 根据选择的机构级别跳转到不同页面
      // 这里可以根据实际需求修改路由路径
      switch(orgLevel) {
        case 1:
          this.$router.push({ path: '/organizations/1' });
          break;
        case 2:
          this.$router.push({ path: '/organizations/2' });
          break;
        case 3:
          this.$router.push({ path: '/organizations/3' });
          break;
        default:
          console.log('未知机构级别');
      }
    },
  }
}

</script>

<style scoped lang="scss">
.disabled-text {
  color: #BAC0CD !important;
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
  vertical-align: middle !important;
}

/* 表头垂直居中 */
::v-deep .el-table th {
  vertical-align: middle !important;
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
</style>
