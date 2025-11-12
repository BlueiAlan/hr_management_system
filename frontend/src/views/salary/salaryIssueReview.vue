<template>
  <div class="review-container">
    <div class="container">
      <h2 style="margin-bottom: 20px;">薪酬发放复核</h2>

      <el-form :model="reviewForm" label-width="150px" v-if="reviewForm.id">
        <el-row>
          <el-col :span="12">
            <el-form-item label="薪酬单号">
              <el-input v-model="reviewForm.issueNumber" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发放机构">
              <el-input v-model="reviewForm.orgName" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="发放日期">
              <el-input v-model="reviewForm.issueDate" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-tag type="warning">{{ reviewForm.status }}</el-tag>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="总人数">
              <el-input v-model="reviewForm.totalEmployees" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总额">
              <span style="font-size: 18px; color: #409EFF; font-weight: bold">
                {{ formatAmount(reviewForm.totalAmount) }}
              </span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="登记人">
              <el-input v-model="reviewForm.createdBy" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="登记时间">
              <el-input :value="formatDateTime(reviewForm.createdAt)" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider></el-divider>

        <h3 style="margin-bottom: 15px;">发放明细</h3>
        <el-table
          :data="reviewForm.details"
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

        <el-form-item label="复核意见">
          <el-input 
            v-model="reviewForm.reviewOpinion" 
            type="textarea" 
            :rows="4" 
            style="width: 500px;" 
            placeholder="请输入复核意见">
          </el-input>
        </el-form-item>

        <div class="subBox" style="margin-top: 30px;">
          <el-button size="small" @click="() => this.$router.push('/salaryIssues')">取消</el-button>
          <el-button size="small" type="danger" @click="submitReview(false)" style="margin-left: 10px;">不通过</el-button>
          <el-button size="small" type="primary" @click="submitReview(true)" style="margin-left: 10px;" class="button1">通过复核</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts">
import { getSalaryIssueById, reviewSalaryIssue } from '@/api/salaryIssues'

export default {
  data() {
    return {
      reviewForm: {
        id: null,
        issueNumber: '',
        orgName: '',
        issueDate: '',
        totalEmployees: 0,
        totalAmount: 0,
        status: '',
        createdBy: '',
        createdAt: '',
        reviewOpinion: '',
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
          const data = res.data.data
          this.reviewForm = { ...data }
          if (!this.reviewForm.details) {
            this.reviewForm.details = []
          }
          // 确保reviewOpinion字段存在且可编辑
          if (this.reviewForm.reviewOpinion === undefined || this.reviewForm.reviewOpinion === null) {
            this.reviewForm.reviewOpinion = ''
          }
          // 确保id存在，以便表单显示
          if (!this.reviewForm.id) {
            this.reviewForm.id = data.id
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
    async submitReview(isApproved: boolean) {
      // 移除复核意见必填验证
      try {
        const res = await reviewSalaryIssue(
          this.reviewForm.id,
          this.reviewForm.reviewOpinion || '',
          isApproved
        )
        if (res.data.code == 200) {
          this.$message.success('复核成功')
          this.$router.push('/salaryIssues')
        } else {
          this.$message.error('复核失败')
        }
      } catch (error) {
        this.$message.error('复核失败')
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
.review-container {
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

.button1 {
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

