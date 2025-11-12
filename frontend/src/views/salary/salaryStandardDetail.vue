<template>
  <div class="detail-container">
    <div class="container">
      <h2 style="margin-bottom: 20px;">薪酬标准详情</h2>
      <el-form :model="ruleForm" label-width="150px" v-if="ruleForm.id">
        <el-row>
          <el-col :span="12">
            <el-form-item label="标准编号">
              <el-input v-model="ruleForm.standardNumber" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标准名称">
              <el-input v-model="ruleForm.standardName" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="制定人">
              <el-input v-model="ruleForm.creator" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="登记人">
              <el-input v-model="ruleForm.registrar" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="登记时间">
              <el-input :value="formatDate(ruleForm.registerTime)" disabled style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-tag v-if="ruleForm.status === '已生效'" type="success">{{ ruleForm.status }}</el-tag>
              <el-tag v-else-if="ruleForm.status === '待复核'" type="warning">{{ ruleForm.status }}</el-tag>
              <el-tag v-else type="info">{{ ruleForm.status }}</el-tag>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="薪酬总额">
              <el-input :value="formatAmount(ruleForm.totalAmount)" disabled style="width: 200px;"></el-input>
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
        <el-row v-if="ruleForm.reviewOpinion">
          <el-col :span="24">
            <el-form-item label="复核意见">
              <el-input v-model="ruleForm.reviewOpinion" type="textarea" :rows="3" disabled style="width: 500px;"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider></el-divider>

        <h3 style="margin-bottom: 15px;">薪酬项目明细</h3>
        <el-table
          :data="ruleForm.details"
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

        <div class="subBox" style="margin-top: 30px;">
          <el-button size="small" @click="() => this.$router.push('/salaryStandards')" class="action-button">返回</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts">
import { getSalaryStandardById } from '@/api/salaryStandards'

export default {
  data() {
    return {
      ruleForm: {
        id: null,
        standardNumber: '',
        standardName: '',
        creator: '',
        registrar: '',
        registerTime: '',
        totalAmount: 0,
        status: '',
        reviewedBy: '',
        reviewedAt: '',
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
          this.ruleForm = { ...res.data.data }
          if (!this.ruleForm.details) {
            this.ruleForm.details = []
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


