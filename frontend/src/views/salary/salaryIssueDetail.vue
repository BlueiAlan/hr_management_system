<template>
  <div class="detail-container">
    <div class="container">
      <h2 style="margin-bottom: 20px;">薪酬发放详情</h2>
      <el-form :model="ruleForm" label-width="150px" v-if="ruleForm.id">
        <el-row>
          <el-col :span="12">
            <el-form-item label="薪酬单号">
              <el-input v-model="ruleForm.issueNumber" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发放机构">
              <el-input v-model="ruleForm.orgName" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="发放日期">
              <el-input v-model="ruleForm.issueDate" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-tag v-if="ruleForm.status === '已发放'" type="success">{{ ruleForm.status }}</el-tag>
              <el-tag v-else-if="ruleForm.status === '已复核'" type="info">{{ ruleForm.status }}</el-tag>
              <el-tag v-else-if="ruleForm.status === '待复核'" type="warning">{{ ruleForm.status }}</el-tag>
              <el-tag v-else type="info">{{ ruleForm.status }}</el-tag>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="总人数">
              <el-input v-model="ruleForm.totalEmployees" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总额">
              <el-input :value="formatAmount(ruleForm.totalAmount)" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="登记人">
              <el-input v-model="ruleForm.createdBy" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="登记时间">
              <el-input :value="formatDateTime(ruleForm.createdAt)" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="ruleForm.reviewedBy">
          <el-col :span="12">
            <el-form-item label="复核人">
              <el-input v-model="ruleForm.reviewedBy" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="复核时间">
              <el-input :value="formatDateTime(ruleForm.reviewedAt)" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider></el-divider>

        <h3 style="margin-bottom: 15px;">发放明细</h3>
        <el-table
          :data="ruleForm.details"
          style="width: 100%"
          :cell-style="{textAlign: 'center'}"
          :header-cell-style="{textAlign: 'center'}">
          <el-table-column
            prop="employeeName"
            label="员工姓名"
            width="150">
          </el-table-column>
          <el-table-column
            prop="archiveNumber"
            label="档案编号"
            width="150">
          </el-table-column>
          <el-table-column
            prop="salaryStandardName"
            label="薪酬标准"
            width="200">
          </el-table-column>
          <el-table-column
            prop="rewardAmount"
            label="奖励金额"
            width="150">
            <template slot-scope="scope">
              {{ scope.row.rewardAmount ? '¥' + parseFloat(scope.row.rewardAmount).toFixed(2) : '¥0.00' }}
            </template>
          </el-table-column>
          <el-table-column
            prop="deductionAmount"
            label="应扣金额"
            width="150">
            <template slot-scope="scope">
              {{ scope.row.deductionAmount ? '¥' + parseFloat(scope.row.deductionAmount).toFixed(2) : '¥0.00' }}
            </template>
          </el-table-column>
          <el-table-column
            prop="finalAmount"
            label="最终金额"
            width="150">
            <template slot-scope="scope">
              {{ scope.row.finalAmount ? '¥' + parseFloat(scope.row.finalAmount).toFixed(2) : '¥0.00' }}
            </template>
          </el-table-column>
        </el-table>

        <div class="subBox" style="margin-top: 30px;">
          <el-button @click="() => this.$router.push('/salaryIssues')" class="action-button">返回</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts">
import { getSalaryIssueById } from '@/api/salaryIssues'

export default {
  data() {
    return {
      ruleForm: {
        id: null,
        issueNumber: '',
        orgId: null,
        orgName: '',
        issueDate: '',
        totalEmployees: 0,
        totalAmount: 0,
        status: '',
        createdBy: '',
        createdAt: '',
        reviewedBy: '',
        reviewedAt: '',
        details: []
      }
    }
  },

  created() {
    this.loadData()
  },

  methods: {
    async loadData() {
      const id = this.$route.query.id
      if (!id) {
        this.$message.error('缺少薪酬发放ID')
        this.$router.push('/salaryIssues')
        return
      }
      try {
        const res = await getSalaryIssueById(id)
        if (res.data.code == 200) {
          this.ruleForm = { ...res.data.data }
          if (!this.ruleForm.details) {
            this.ruleForm.details = []
          }
        } else {
          this.$message.error('获取薪酬发放详情失败')
          this.$router.push('/salaryIssues')
        }
      } catch (error) {
        this.$message.error('获取薪酬发放详情失败')
        this.$router.push('/salaryIssues')
      }
    },
    formatAmount(amount: any) {
      if (!amount) return '¥0.00'
      return '¥' + parseFloat(amount).toFixed(2)
    },
    formatDateTime(dateTime: any) {
      if (!dateTime) return ''
      if (typeof dateTime === 'string') {
        return dateTime.replace('T', ' ').substring(0, 19)
      }
      return dateTime
    }
  }
}
</script>

<style lang="scss" scoped>
.detail-container {
  margin: 30px;
  margin-top: 30px;
}

.container {
  background: #fff;
  padding: 30px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.subBox {
  text-align: center;
  margin-top: 20px;
}

.action-button {
  background-color: #409EFF !important;
  border-color: #409EFF !important;
  color: #FFFFFF !important;
}

::v-deep .el-table__header-wrapper {
  th {
    background-color: rgba(96, 98, 102, 0.24) !important;
    color: #606266 !important;
    font-weight: bold !important;
  }
}
</style>








