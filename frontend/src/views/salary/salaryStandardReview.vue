<template>
  <div class="review-container">
    <div class="container">
      <h2 style="margin-bottom: 20px;">薪酬标准复核</h2>

      <el-form :model="reviewForm" label-width="150px" v-if="reviewForm.id">
        <el-row>
          <el-col :span="12">
            <el-form-item label="标准编号">
              <el-input v-model="reviewForm.standardNumber" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标准名称">
              <el-input v-model="reviewForm.standardName" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="制定人">
              <el-input v-model="reviewForm.creator" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="登记人">
              <el-input v-model="reviewForm.registrar" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="登记时间">
              <el-input :value="formatDate(reviewForm.registerTime)" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-tag type="warning">{{ reviewForm.status }}</el-tag>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider></el-divider>

        <h3 style="margin-bottom: 15px;">薪酬项目明细</h3>
        <el-table
          :data="reviewForm.details"
          style="width: 100%"
          :cell-style="{textAlign: 'center'}"
          :header-cell-style="{textAlign: 'center'}">
          <el-table-column
            prop="itemName"
            label="薪酬项目"
            width="200">
          </el-table-column>
          <el-table-column
            prop="itemType"
            label="项目类型"
            width="150">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.itemType === '收入'" type="success">{{ scope.row.itemType }}</el-tag>
              <el-tag v-else type="danger">{{ scope.row.itemType }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column
            prop="amount"
            label="金额"
            width="150">
            <template slot-scope="scope">
              {{ scope.row.amount ? '¥' + parseFloat(scope.row.amount).toFixed(2) : '¥0.00' }}
            </template>
          </el-table-column>
        </el-table>

        <el-row style="margin-top: 20px;">
          <el-col :span="12">
            <el-form-item label="薪酬总额">
              <span style="font-size: 18px; color: #409EFF; font-weight: bold">
                {{ formatAmount(reviewForm.totalAmount) }}
              </span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="复核意见">
          <el-input v-model="reviewForm.reviewOpinion" type="textarea" :rows="4" style="width: 500px;" placeholder="请输入复核意见"></el-input>
        </el-form-item>

        <div class="subBox" style="margin-top: 30px;">
          <el-button size="small" @click="() => this.$router.push('/salaryStandards')">取消</el-button>
          <el-button size="small" type="danger" @click="submitReview(false)" style="margin-left: 10px;">不通过</el-button>
          <el-button size="small" type="primary" @click="submitReview(true)" style="margin-left: 10px;" class="button1">通过复核</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts">
import { getSalaryStandardById, reviewSalaryStandard } from '@/api/salaryStandards'

export default {
  data() {
    return {
      reviewForm: {
        id: null,
        standardNumber: '',
        standardName: '',
        creator: '',
        registrar: '',
        registerTime: '',
        totalAmount: 0,
        status: '',
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
        this.$message.error('缺少薪酬标准ID')
        this.$router.push('/salaryStandards')
        return
      }
      try {
        const res = await getSalaryStandardById(id)
        if (res.data.code == 200) {
          this.reviewForm = { ...res.data.data }
          if (!this.reviewForm.details) {
            this.reviewForm.details = []
          }
          if (!this.reviewForm.reviewOpinion) {
            this.reviewForm.reviewOpinion = ''
          }
        } else {
          this.$message.error('获取薪酬标准详情失败')
          this.$router.push('/salaryStandards')
        }
      } catch (error) {
        this.$message.error('获取薪酬标准详情失败')
        this.$router.push('/salaryStandards')
      }
    },
    async submitReview(isApproved: boolean) {
      if (!this.reviewForm.reviewOpinion) {
        this.$message.warning('请输入复核意见')
        return
      }
      try {
        const res = await reviewSalaryStandard(
          this.reviewForm.id,
          this.reviewForm.reviewOpinion,
          isApproved
        )
        if (res.data.code == 200) {
          this.$message.success('复核成功')
          this.$router.push('/salaryStandards')
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
    formatDate(date: any) {
      if (!date) return ''
      if (typeof date === 'string') {
        return date.substring(0, 10)
      }
      return date
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







