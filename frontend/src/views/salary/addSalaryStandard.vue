<script lang="ts">
import { addSalaryStandard, getSalaryStandardById, updateSalaryStandard, generateSalaryStandardNumber } from '@/api/salaryStandards'
import { getSalaryItemsList } from '@/api/salaryItems'
import Cookies from 'js-cookie'
import {Role} from "@/utils/permission";

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
      // 设置制定人为当前用户
      try {
        const userInfo = this.$store.state.user.userInfo
        if (userInfo && userInfo.name) {
          this.ruleForm.creator = userInfo.name
        } else {
          // 如果 store 中没有，尝试从 cookies 获取
          const userInfoStr = Cookies.get('user_info')
          if (userInfoStr) {
            const userInfoFromCookie = JSON.parse(userInfoStr)
            if (userInfoFromCookie && userInfoFromCookie.name) {
              this.ruleForm.creator = userInfoFromCookie.name
            }
          }
          // 如果还是没有，尝试从 store 的 username 或 name 获取
          if (!this.ruleForm.creator) {
            const username = this.$store.state.user.username || this.$store.state.user.name
            if (username) {
              this.ruleForm.creator = username
            }
          }
        }
      } catch (error) {
        console.error('获取当前用户信息失败:', error)
        // 如果都获取不到，使用默认值
        if (!this.ruleForm.creator) {
          this.ruleForm.creator = 'admin'
        }
      }
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
        amount: 0,
        itemType: null // 存储项目类型
      })
    },
    removeDetail(index: number) {
      const detail = this.ruleForm.details[index]
      // 如果删除的是基本工资，更新所有三险一金项目的金额为0
      if (detail && detail.salaryItemId) {
        const item = this.salaryItems.find((item: any) => item.id === detail.salaryItemId)
        if (item && this.isBasicSalaryItem(item.itemName)) {
          // 删除基本工资时，将所有三险一金项目的金额设为0
          const insuranceItemNames = ['养老保险', '医疗保险', '失业保险', '住房公积金']
          insuranceItemNames.forEach(name => {
            const insuranceDetail = this.findDetailByItemName(name)
            if (insuranceDetail) {
              insuranceDetail.amount = 0
            }
          })
        }
      }
      this.ruleForm.details.splice(index, 1)
      this.calculateTotal()
    },
    getItemType(salaryItemId: number) {
      const item = this.salaryItems.find((item: any) => item.id === salaryItemId)
      return item ? item.itemType : null
    },
    // 根据项目名称查找项目ID
    findItemIdByName(itemName: string) {
      const item = this.salaryItems.find((item: any) => item.itemName === itemName)
      return item ? item.id : null
    },
    // 根据项目名称查找项目
    findItemByName(itemName: string) {
      return this.salaryItems.find((item: any) => item.itemName === itemName)
    },
    // 检查某个项目是否已经在details中
    findDetailByItemName(itemName: string) {
      const itemId = this.findItemIdByName(itemName)
      if (!itemId) return null
      return this.ruleForm.details.find((detail: any) => detail.salaryItemId === itemId)
    },
    // 自动计算三险一金
    calculateInsuranceAndHousing(basicSalary: number) {
      if (!basicSalary || basicSalary <= 0) {
        return {
          pension: 0,        // 养老保险
          medical: 0,        // 医疗保险
          unemployment: 0,  // 失业保险
          housing: 0        // 住房公积金
        }
      }

      return {
        pension: parseFloat((basicSalary * 0.08).toFixed(2)),           // 养老保险 = 基本工资 * 8%
        medical: parseFloat((basicSalary * 0.02 + 3).toFixed(2)),       // 医疗保险 = 基本工资 * 2% + 3元
        unemployment: parseFloat((basicSalary * 0.005).toFixed(2)),      // 失业保险 = 基本工资 * 0.5%
        housing: parseFloat((basicSalary * 0.08).toFixed(2))            // 住房公积金 = 基本工资 * 8%
      }
    },
    // 判断是否是三险一金项目
    isInsuranceItem(itemName: string) {
      const insuranceItems = ['养老保险', '医疗保险', '失业保险', '住房公积金']
      return insuranceItems.includes(itemName)
    },
    // 判断是否是基本工资项目
    isBasicSalaryItem(itemName: string) {
      return itemName === '基本工资'
    },
    // 获取基本工资金额
    getBasicSalaryAmount() {
      const basicSalaryDetail = this.findDetailByItemName('基本工资')
      if (basicSalaryDetail) {
        return parseFloat(basicSalaryDetail.amount) || 0
      }
      return 0
    },
    // 根据项目名称获取计算方式文本
    getCalculationFormula(itemName: string) {
      const basicSalary = this.getBasicSalaryAmount()
      if (basicSalary <= 0) {
        return '请先输入基本工资'
      }
      
      const formulas: any = {
        '养老保险': '基本工资 * 8%',
        '医疗保险': '基本工资 * 2% + 3元',
        '失业保险': '基本工资 * 0.5%',
        '住房公积金': '基本工资 * 8%'
      }
      
      return formulas[itemName] || ''
    },
    // 当基本工资变化时，更新所有已选择的三险一金项目
    updateInsuranceItems() {
      const basicSalary = this.getBasicSalaryAmount()
      const insurance = this.calculateInsuranceAndHousing(basicSalary)
      
      // 定义三险一金项目名称映射
      const insuranceItems = [
        { name: '养老保险', amount: insurance.pension },
        { name: '医疗保险', amount: insurance.medical },
        { name: '失业保险', amount: insurance.unemployment },
        { name: '住房公积金', amount: insurance.housing }
      ]
      
      // 更新已选择的三险一金项目金额
      insuranceItems.forEach(({ name, amount }) => {
        const detail = this.findDetailByItemName(name)
        if (detail) {
          detail.amount = amount
        }
      })
      
      this.calculateTotal()
    },
    calculateTotal() {
      this.totalAmount = this.ruleForm.details.reduce((sum: number, detail: any) => {
        if (!detail.salaryItemId) {
          return sum
        }
        const itemType = this.getItemType(detail.salaryItemId)
        const amount = parseFloat(detail.amount) || 0
        // 收入类型：加上金额，扣除类型：减去金额
        if (itemType === '收入') {
          return sum + amount
        } else if (itemType === '扣除') {
          return sum - amount
        }
        return sum
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
    },
    // 处理薪酬项目选择变化
    handleItemChange(detail: any, index: number) {
      detail.itemType = this.getItemType(detail.salaryItemId)
      const item = this.salaryItems.find((item: any) => item.id === detail.salaryItemId)
      
      if (item) {
        // 如果选择的是三险一金项目，根据基本工资自动计算
        if (this.isInsuranceItem(item.itemName)) {
          const basicSalary = this.getBasicSalaryAmount()
          if (basicSalary > 0) {
            const insurance = this.calculateInsuranceAndHousing(basicSalary)
            const amountMap: any = {
              '养老保险': insurance.pension,
              '医疗保险': insurance.medical,
              '失业保险': insurance.unemployment,
              '住房公积金': insurance.housing
            }
            detail.amount = amountMap[item.itemName] || 0
          } else {
            detail.amount = 0
            this.$message.warning('请先输入基本工资')
          }
        }
      }
      
      this.calculateTotal()
    },
    // 处理金额变化
    handleAmountChange(detail: any, index: number) {
      const item = this.salaryItems.find((item: any) => item.id === detail.salaryItemId)
      
      // 如果是基本工资变化，更新所有已选择的三险一金项目
      if (item && this.isBasicSalaryItem(item.itemName)) {
        this.updateInsuranceItems()
      } else {
        this.calculateTotal()
      }
    },
    // 获取项目名称
    getItemName(salaryItemId: number) {
      const item = this.salaryItems.find((item: any) => item.id === salaryItemId)
      return item ? item.itemName : ''
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
          <el-input v-model="ruleForm.creator" disabled placeholder="当前登录用户"></el-input>
        </el-form-item>

        <el-divider>薪酬项目明细</el-divider>

        <el-form-item label=" ">
          <el-button type="primary" size="small" @click="addDetail">+ 添加薪酬项目</el-button>
        </el-form-item>

        <el-table :data="ruleForm.details" border style="width: 100%">
          <el-table-column label="薪酬项目" width="200">
            <template slot-scope="scope">
              <el-select v-model="scope.row.salaryItemId" placeholder="请选择薪酬项目" @change="handleItemChange(scope.row, scope.$index)">
                <el-option
                  v-for="item in salaryItems"
                  :key="item.id"
                  :label="item.itemName + ' (' + item.itemType + ')'"
                  :value="item.id">
                </el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="项目类型" width="120">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.salaryItemId" :type="getItemType(scope.row.salaryItemId) === '收入' ? 'success' : 'danger'">
                {{ getItemType(scope.row.salaryItemId) || '-' }}
              </el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="金额" width="200">
            <template slot-scope="scope">
              <div>
                <el-input-number
                  v-model="scope.row.amount"
                  :precision="2"
                  :min="0"
                  :disabled="isInsuranceItem(getItemName(scope.row.salaryItemId))"
                  @change="handleAmountChange(scope.row, scope.$index)"
                  style="width: 100%"></el-input-number>
                <div v-if="isInsuranceItem(getItemName(scope.row.salaryItemId))" style="margin-top: 5px; font-size: 12px; color: #909399;">
                  计算方式：{{ getCalculationFormula(getItemName(scope.row.salaryItemId)) }}
                </div>
              </div>
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



