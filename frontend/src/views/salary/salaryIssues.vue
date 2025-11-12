<template>
  <div class="dashboard-container">
    <div class="container">
      <div class="tableBar">
        <label style="margin-right: 5px">单号：</label>
        <el-input v-model="issueNumber" placeholder="请输入单号" style="width: 15%"></el-input>
        <el-button type="primary" style="margin-left: 25px; color: #FFFFFF" @click="pageQuery()" class="button1">查询</el-button>
        <el-button type="primary" style="float: right; color:#FFFFFF;" class="button1" @click="handleAdd()">+添加薪酬发放</el-button>
      </div>

      <div class="table-container">
        <el-table
          :data="records"
          style="width: 100%"
          :cell-style="{textAlign: 'center'}"
          :header-cell-style="{textAlign: 'center'}">
          <el-table-column
            prop="issueNumber"
            label="薪酬单号"
            width="150">
          </el-table-column>
          <el-table-column
            prop="orgName"
            label="发放机构"
            width="200">
          </el-table-column>
          <el-table-column
            prop="issueDate"
            label="发放日期"
            width="120">
          </el-table-column>
          <el-table-column
            prop="totalEmployees"
            label="总人数"
            width="100">
          </el-table-column>
          <el-table-column
            prop="totalAmount"
            label="总额"
            width="150">
            <template slot-scope="scope">
              {{ scope.row.totalAmount ? '¥' + scope.row.totalAmount.toFixed(2) : '¥0.00' }}
            </template>
          </el-table-column>
          <el-table-column
            prop="status"
            label="状态"
            width="100">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.status === '已发放'" type="success">{{ scope.row.status }}</el-tag>
              <el-tag v-else-if="scope.row.status === '已复核'" type="info">{{ scope.row.status }}</el-tag>
              <el-tag v-else-if="scope.row.status === '待复核'" type="warning">{{ scope.row.status }}</el-tag>
              <el-tag v-else-if="scope.row.status === '不通过'" type="danger">{{ scope.row.status }}</el-tag>
              <el-tag v-else type="info">{{ scope.row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column
            prop="createdBy"
            label="登记人"
            width="120">
          </el-table-column>
          <el-table-column
            prop="createdAt"
            label="登记时间"
            width="180">
          </el-table-column>
          <el-table-column label="操作" width="320">
            <template v-slot="scope">
              <el-button
                v-if="scope.row.status === '待复核'"
                size="small" plain
                class="action-button"
                @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
              <el-button
                v-if="scope.row.status === '待复核'"
                size="small" plain
                type="success"
                class="action-button"
                @click="handleReview(scope.$index, scope.row)">复核</el-button>
              <el-button
                v-if="scope.row.status === '已复核'"
                size="small" plain
                type="warning"
                class="action-button"
                @click="handleIssue(scope.$index, scope.row)">发放</el-button>
              <el-button
                size="small" plain
                class="action-button"
                @click="handleDetail(scope.$index, scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[10, 20, 30, 40, 50, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        style="margin-top: 20px; text-align: center">
      </el-pagination>
    </div>
  </div>
</template>

<script lang="ts">
import { getSalaryIssueList, reviewSalaryIssue, issueSalary } from '@/api/salaryIssues'

export default {
  data(){
    return {
      issueNumber: "",
      pageNum: 1,
      pageSize: 10,
      total: 0,
      records: []
    }
  },
  created() {
    this.pageQuery();
  },
  methods: {
    pageQuery() {
      const params = {
        issueNumber: this.issueNumber,
        pageNum: this.pageNum,
        pageSize: this.pageSize
      }
      getSalaryIssueList(params).then(res => {
        if (res.data.code == 200) {
          this.records = res.data.data.records
          this.total = res.data.data.total
        }
      }).catch(err => {
        this.$message.error('请求出错，请联系管理员')
      })
    },
    handleSizeChange(val: number) {
      this.pageSize = val
      this.pageQuery()
    },
    handleCurrentChange(val: number) {
      this.pageNum = val
      this.pageQuery()
    },
    handleAdd() {
      this.$router.push({ path: '/salaryIssues/add' })
    },
    handleEdit(index, row) {
      this.$router.push({ path: '/salaryIssues/add', query: { id: row.id } })
    },
    handleReview(index, row) {
      this.$router.push({ path: '/salaryIssues/review', query: { id: row.id } })
    },
    handleIssue(index, row) {
      this.$confirm('确认发放薪酬单 ' + row.issueNumber + ' 吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        issueSalary(row.id).then(res => {
          if (res.data.code == 200) {
            this.$message.success('发放成功')
            this.pageQuery()
          } else {
            this.$message.error('发放失败')
          }
        })
      }).catch(() => {})
    },
    handleDetail(index, row) {
      this.$router.push({ path: '/salaryIssues/detail', query: { id: row.id } })
    }
  }
}
</script>

<style scoped lang="scss">
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

.button1{
  background-color: #409EFF !important;
  border-color: #409EFF !important;
}

.action-button {
  background-color: transparent !important;
  color: #409EFF !important;
  border-color: #409EFF !important;
}
</style>




