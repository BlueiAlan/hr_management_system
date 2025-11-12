<script lang="ts">
import { addSalaryStandard, getSalaryStandardById, updateSalaryStandard, generateSalaryStandardNumber } from '@/api/salaryStandards'
import { getSalaryItemsList } from '@/api/salaryItems'

export default {
  data() {
    return {
      optType: '',
      ruleForm: {
        standardNumber: '',
        standardName: '',
        creator: '',
        details: []
      },
      rules: {
        standardNumber: [
          { required: true, message: '请输入标准编号', trigger: 'blur' }
        ],
        standardName: [
          { required: true, message: '请输入标准名称', trigger: 'blur' }
        ],
        creator: [
          { required: true, message: '请输入制定人', trigger: 'blur' }
        ]
      },
      salaryItems: [],
      totalAmount: 0
    }
  },
  created() {
    this.loadSalaryItems()
    this.optType = this.$route.query.id ? 'edit' : 'add'
    if (this.optType === 'add') {
      this.generateNumber()
    } else {
      this.loadData()
    }
  },
  methods: {
    async loadSalaryItems() {
      try {
        const res = await getSalaryItemsList()
        if (res.data.code == 200) {
          this.salaryItems = res.data.data.filter((item: any) => item.isActive === 1)
        }
      } catch (error) {
        this.$message.error('获取薪酬项目列表失败')
      }
    },
    async loadData() {
      const id = this.$route.query.id
      try {
        const res = await getSalaryStandardById(id)
        if (res.data.code == 200) {
          this.ruleForm = res.data.data
          if (!this.ruleForm.details) {
            this.ruleForm.details = []
          }
          this.calculateTotal()
        }
      } catch (error) {
        this.$message.error('获取薪酬标准详情失败')
      }
    },
    async generateNumber() {
      try {
        const res = await generateSalaryStandardNumber()
        if (res.data.code == 200) {
          this.ruleForm.standardNumber = res.data.data
        }
      } catch (error) {
        // 忽略错误
      }
    },
    addDetail() {
      this.ruleForm.details.push({
        salaryItemId: null,
        amount: 0
      })
    },
    removeDetail(index: number) {
      this.ruleForm.details.splice(index, 1)
      this.calculateTotal()
    },
    calculateTotal() {
      this.totalAmount = this.ruleForm.details.reduce((sum: number, detail: any) => {
        return sum + (parseFloat(detail.amount) || 0)
      }, 0)
      this.ruleForm.totalAmount = this.totalAmount
    },
    submitForm(formName: string) {
      this.$refs[formName].validate((valid: boolean) => {
        if (valid) {
          if (this.ruleForm.details.length === 0) {
            this.$message.warning('请至少添加一个薪酬项目明细')
            return
          }
          
          const params = {
            ...this.ruleForm,
            details: this.ruleForm.details.map((detail: any) => ({
              salaryItemId: detail.salaryItemId,
              amount: parseFloat(detail.amount) || 0
            }))
          }

          if (this.optType === 'edit') {
            params.id = this.$route.query.id
            updateSalaryStandard(params).then((res: any) => {
              if (res.data.code == 200) {
                this.$message.success('更新薪酬标准成功')
                this.$router.push({ path: '/salaryStandards' })
              } else {
                this.$message.error('更新薪酬标准失败')
              }
            })
          } else {
            addSalaryStandard(params).then((res: any) => {
              if (res.data.code == 200) {
                this.$message.success('创建薪酬标准成功')
                this.$router.push({ path: '/salaryStandards' })
              } else {
                this.$message.error('创建薪酬标准失败')
              }
            })
          }
        }
      })
    },
    resetForm(formName: string) {
      this.$refs[formName].resetFields()
      this.ruleForm.details = []
      this.totalAmount = 0
    }
  }
}
</script>

<template>
  <div class="addBrand-container">
    <div class="container">
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="120px">
        <el-form-item label="标准编号" prop="standardNumber">
          <el-input v-model="ruleForm.standardNumber" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="标准名称" prop="standardName">
          <el-input v-model="ruleForm.standardName"></el-input>
        </el-form-item>
        <el-form-item label="制定人" prop="creator">
          <el-input v-model="ruleForm.creator"></el-input>
        </el-form-item>
        
        <el-divider>薪酬项目明细</el-divider>
        
        <el-form-item label=" ">
          <el-button type="primary" size="small" @click="addDetail">+ 添加薪酬项目</el-button>
        </el-form-item>
        
        <el-table :data="ruleForm.details" border style="width: 100%">
          <el-table-column label="薪酬项目" width="200">
            <template slot-scope="scope">
              <el-select v-model="scope.row.salaryItemId" placeholder="请选择薪酬项目" @change="calculateTotal">
                <el-option
                  v-for="item in salaryItems"
                  :key="item.id"
                  :label="item.itemName"
                  :value="item.id">
                </el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="金额" width="150">
            <template slot-scope="scope">
              <el-input-number 
                v-model="scope.row.amount" 
                :precision="2" 
                :min="0"
                @change="calculateTotal"
                style="width: 100%"></el-input-number>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template slot-scope="scope">
              <el-button size="mini" type="danger" @click="removeDetail(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <el-form-item label="薪酬总额">
          <span style="font-size: 18px; color: #409EFF; font-weight: bold">
            ¥{{ totalAmount.toFixed(2) }}
          </span>
        </el-form-item>
        
        <el-form-item>
          <el-button
            v-if="optType == 'add'"
            size="small"
            class="button1" type="primary" @click="submitForm('ruleForm')">立即创建</el-button>
          <el-button
            v-if="optType == 'edit'"
            size="small"
            class="button1" type="primary" @click="submitForm('ruleForm')">保存修改</el-button>
          <el-button size="small" class="button2" type="danger" plain @click="resetForm('ruleForm')">重置</el-button>
          <el-button size="small" @click="() => this.$router.push({ path: '/salaryStandards' })">返回</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped lang="scss">
.addBrand {
  &-container {
    margin: 30px;
    margin-top: 30px;
    .container {
      position: relative;
      z-index: 1;
      background: #fff;
      padding: 30px;
      border-radius: 4px;
    }
    .el-form-item {
      margin-bottom: 29px;
    }
    .el-input {
      width: 293px;
    }
  }
}

.button1{
  background-color: #409EFF !important;
  border-color: #409EFF !important;
  color: #FFFFFF !important;
}
.button1:hover {
  background-color: #66b1ff !important;
  border-color: #66b1ff !important;
  color: #FFFFFF !important;
}
.button2.el-button--danger {
  background-color: transparent !important;
  color: #F56C6C !important;
  border-color: #F56C6C !important;
}
.button2:hover {
  background-color: #fef0f0 !important;
  border-color: #F56C6C !important;
  color: #F56C6C !important;
}
</style>



