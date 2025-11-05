<template>
  <div class="dashboard-container">
    <div class="container">
      <div class="tableBar">
        <label style="margin-right: 5px">标准编号：</label>
        <el-input v-model="standardNumber" placeholder="请输入标准编号" style="width: 15%"></el-input>
        <label style="margin-left: 20px; margin-right: 5px">标准名称：</label>
        <el-input v-model="standardName" placeholder="请输入标准名称" style="width: 15%"></el-input>
        <el-button type="primary" style="margin-left: 25px; color: #FFFFFF" @click="pageQuery()" class="button1">查询</el-button>
        <el-button type="primary" style="float: right; color:#FFFFFF;" class="button1" @click="handleAdd()">+添加薪酬标准</el-button>
      </div>

      <div class="table-container">
        <el-table
          :data="records"
          style="width: 100%"
          :cell-style="{textAlign: 'center'}"
          :header-cell-style="{textAlign: 'center'}">
          <el-table-column
            prop="standardNumber"
            label="标准编号"
            width="150">
          </el-table-column>
          <el-table-column
            prop="standardName"
            label="标准名称"
            width="200">
          </el-table-column>
          <el-table-column
            prop="creator"
            label="制定人"
            width="120">
          </el-table-column>
          <el-table-column
            prop="registrar"
            label="登记人"
            width="120">
          </el-table-column>
          <el-table-column
            prop="totalAmount"
            label="薪酬总额"
            width="120">
            <template slot-scope="scope">
              {{ scope.row.totalAmount ? '¥' + scope.row.totalAmount.toFixed(2) : '¥0.00' }}
            </template>
          </el-table-column>
          <el-table-column
            prop="status"
            label="状态"
            width="100">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.status === '已生效'" type="success">{{ scope.row.status }}</el-tag>
              <el-tag v-else-if="scope.row.status === '待复核'" type="warning">{{ scope.row.status }}</el-tag>
              <el-tag v-else type="info">{{ scope.row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column
            prop="registerTime"
            label="登记时间"
            width="180">
          </el-table-column>
          <el-table-column label="操作" width="280">
            <template v-slot="scope">
              <el-button
                size="primary" plain
                class="action-button"
                @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
              <el-button
                v-if="scope.row.status === '待复核'"
                size="primary" plain
                type="success"
                class="action-button"
                @click="handleReview(scope.$index, scope.row)">复核</el-button>
              <el-button
                size="primary" plain
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
import { getSalaryStandardList } from '@/api/salaryStandards'

export default {
  data(){
    return {
      standardNumber: "",
      standardName: "",
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
        standardNumber: this.standardNumber,
        standardName: this.standardName,
        pageNum: this.pageNum,
        pageSize: this.pageSize
      }
      getSalaryStandardList(params).then(res => {
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
      this.$router.push({ path: '/salaryStandards/add' })
    },
    handleEdit(index, row) {
      this.$router.push({ path: '/salaryStandards/add', query: { id: row.id } })
    },
    handleReview(index, row) {
      this.$router.push({ path: '/salaryStandards/review', query: { id: row.id } })
    },
    handleDetail(index, row) {
      this.$router.push({ path: '/salaryStandards/detail', query: { id: row.id } })
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

