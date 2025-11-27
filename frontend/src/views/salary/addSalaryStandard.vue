<script lang="ts">
import { addSalaryStandard, getSalaryStandardById, updateSalaryStandard, generateSalaryStandardNumber } from '@/api/salaryStandards'
import { getSalaryItemsList } from '@/api/salaryItems'
import { getPositionList } from '@/api/positions'
import Cookies from 'js-cookie'

export default {
  data() {
    return {
      optType: '',
      ruleForm: {
        standardNumber: '',
        standardName: '',
        creator: '',
        positionId: null,
        details: []
      },
      rules: {
        standardNumber: [
          { required: true, message: '请输入标准编号', trigger: 'blur' }
        ],
        standardName: [
          { required: true, message: '请输入标准名称', trigger: 'blur' }
        ],
        positionId: [
          { required: true, message: '请选择适用职位', trigger: 'change' }
        ]
      },
      salaryItems: [],
      positionList: [],
      totalAmount: 0,
      // 三险一金的计算规则
      insuranceCalculationRules: {
        '养老保险': { formula: '基本工资 * 8%', calculate: (baseSalary: number) => baseSalary * 0.08 },
        '医疗保险': { formula: '基本工资 * 2% + 3', calculate: (baseSalary: number) => baseSalary * 0.02 + 3 },
        '失业保险': { formula: '基本工资 * 0.5%', calculate: (baseSalary: number) => baseSalary * 0.005 },
        '住房公积金': { formula: '基本工资 * 8%', calculate: (baseSalary: number) => baseSalary * 0.08 }
      }
    }
  },
  created() {
    this.loadSalaryItems()
    this.loadPositionList()
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
    async loadPositionList() {
      try {
        const res = await getPositionList({ pageNum: 1, pageSize: 1000 })
        if (res.data.code == 200) {
          this.positionList = res.data.data.records || []
        }
      } catch (error) {
        this.$message.error('获取职位列表失败')
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
        itemType: null, // 存储项目类型
        calculationFormula: '' // 存储计算方式
      })
    },
    removeDetail(index: number) {
      this.ruleForm.details.splice(index, 1)
      this.calculateTotal()
    },
    getItemType(salaryItemId: number) {
      const item = this.salaryItems.find((item: any) => item.id === salaryItemId)
      return item ? item.itemType : null
    },
    // 获取薪酬项目名称
    getItemName(salaryItemId: number) {
      const item = this.salaryItems.find((item: any) => item.id === salaryItemId)
      return item ? item.itemName : null
    },
    // 检查是否是三险一金项目
    isInsuranceItem(itemName: string) {
      return itemName && this.insuranceCalculationRules.hasOwnProperty(itemName)
    },
    // 获取基本工资金额
    getBaseSalary() {
      const baseSalaryItem = this.ruleForm.details.find((detail: any) => {
        const itemName = this.getItemName(detail.salaryItemId)
        return itemName === '基本工资'
      })
      return baseSalaryItem ? parseFloat(baseSalaryItem.amount) || 0 : 0
    },
    // 计算三险一金金额
    calculateInsuranceAmount(itemName: string) {
      const baseSalary = this.getBaseSalary()
      if (baseSalary <= 0) {
        return 0
      }
      const rule = this.insuranceCalculationRules[itemName]
      if (rule) {
        return rule.calculate(baseSalary)
      }
      return 0
    },
    // 获取计算方式公式
    getCalculationFormula(itemName: string) {
      const rule = this.insuranceCalculationRules[itemName]
      return rule ? rule.formula : ''
    },
    // 处理薪酬项目选择变化
    handleItemChange(detail: any) {
      detail.itemType = this.getItemType(detail.salaryItemId)
      const itemName = this.getItemName(detail.salaryItemId)
      
      // 如果是三险一金项目，自动计算金额
      if (this.isInsuranceItem(itemName)) {
        const calculatedAmount = this.calculateInsuranceAmount(itemName)
        detail.amount = parseFloat(calculatedAmount.toFixed(2))
        detail.calculationFormula = this.getCalculationFormula(itemName)
      } else {
        detail.calculationFormula = ''
      }
      
      this.calculateTotal()
    },
    // 处理金额变化
    handleAmountChange(detail: any) {
      const itemName = this.getItemName(detail.salaryItemId)
      
      // 如果修改的是基本工资，需要重新计算所有三险一金
      if (itemName === '基本工资') {
        this.calculateTotal()
      } else if (!this.isInsuranceItem(itemName)) {
        // 如果不是三险一金项目，正常计算总额
        this.calculateTotal()
      }
      // 如果是三险一金项目，金额是禁用的，不会触发这个函数
    },
    calculateTotal() {
      // 重新计算所有三险一金项目的金额
      this.ruleForm.details.forEach((detail: any) => {
        if (detail.salaryItemId) {
          const itemName = this.getItemName(detail.salaryItemId)
          if (this.isInsuranceItem(itemName)) {
            const calculatedAmount = this.calculateInsuranceAmount(itemName)
            detail.amount = parseFloat(calculatedAmount.toFixed(2))
            detail.calculationFormula = this.getCalculationFormula(itemName)
          }
        }
      })
      
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
        <el-form-item label="适用职位" prop="positionId">
          <el-select v-model="ruleForm.positionId" placeholder="请选择职位" clearable>
            <el-option
              v-for="item in positionList"
              :key="item.id"
              :label="item.positionName"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>

        <el-divider>薪酬项目明细</el-divider>

        <el-form-item label=" ">
          <el-button type="primary" size="small" @click="addDetail">+ 添加薪酬项目</el-button>
        </el-form-item>

        <el-table :data="ruleForm.details" border style="width: 100%">
          <el-table-column label="薪酬项目" width="200">
            <template slot-scope="scope">
              <el-select v-model="scope.row.salaryItemId" placeholder="请选择薪酬项目" @change="handleItemChange(scope.row)">
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
                  :disabled="scope.row.salaryItemId && isInsuranceItem(getItemName(scope.row.salaryItemId))"
                  @change="handleAmountChange(scope.row)"
                  style="width: 100%"></el-input-number>
                <div v-if="scope.row.salaryItemId && isInsuranceItem(getItemName(scope.row.salaryItemId))" 
                     style="font-size: 12px; color: #909399; margin-top: 5px;">
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



