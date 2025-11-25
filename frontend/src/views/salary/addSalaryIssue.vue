<script lang="ts">
import { addSalaryIssue, getSalaryIssueById, updateSalaryIssue, generateSalaryIssueNumber } from '@/api/salaryIssues'
import { getOrgList } from '@/api/organizations'
import { getEmployeeList } from '@/api/employee'
import { getSalaryStandardList } from '@/api/salaryStandards'
import { filterAdminEmployees } from '@/utils/permission'
import { UserModule } from '@/store/modules/user'
import Cookies from 'js-cookie'

export default {
  data() {
    return {
      optType: '',
      ruleForm: {
        issueNumber: '',
        orgId: null,
        issueDate: '',
        details: []
      },
      rules: {
        issueNumber: [
          { required: true, message: '请输入单号', trigger: 'blur' }
        ],
        orgId: [
          { required: true, message: '请选择发放机构', trigger: 'change' }
        ],
        issueDate: [
          { required: true, message: '请选择发放日期', trigger: 'change' }
        ]
      },
      orgList: [],
      employeeList: [],
      salaryStandardList: [],
      totalEmployees: 0,
      totalAmount: 0
    }
  },
  created() {
    this.loadOrgs()
    // 初始不加载员工，等选择机构后再加载
    this.loadSalaryStandards()
    this.optType = this.$route.query.id ? 'edit' : 'add'
    if (this.optType === 'add') {
      this.generateNumber()
      this.ruleForm.issueDate = this.getCurrentDate()
    } else {
      this.loadData()
    }
  },
  methods: {
    getCurrentDate() {
      const date = new Date()
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    async loadOrgs() {
      try {
        const res = await getOrgList({ orgLevel: 3 })
        if (res.data.code == 200) {
          this.orgList = res.data.data
        }
      } catch (error) {
        this.$message.error('获取机构列表失败')
      }
    },
    async loadEmployees(orgId?: number) {
      try {
        const params: any = { 
          pageNum: 1, 
          pageSize: 1000,
          status: '正常'
        }
        if (orgId) {
          params.orgId = orgId
        }
        const res = await getEmployeeList(params)
        if (res.data.code == 200) {
          let employeeList = res.data.data.records.filter((emp: any) => emp.status === '正常')
          
          // 获取当前用户角色，过滤超级管理员信息
          const userInfo = Cookies.get('user_info') ? JSON.parse(Cookies.get('user_info') as string) : {}
          const currentUserRole = UserModule.role || userInfo.role || 0
          // 非超级管理员用户看不到超级管理员的信息
          employeeList = filterAdminEmployees(employeeList, currentUserRole)
          
          this.employeeList = employeeList
        }
      } catch (error) {
        this.$message.error('获取员工列表失败')
      }
    },
    async handleOrgChange() {
      // 机构变化时，重新加载该机构的员工
      if (this.ruleForm.orgId) {
        await this.loadEmployees(this.ruleForm.orgId)
        // 清空已选择的员工（如果该员工不在新机构下）
        this.ruleForm.details.forEach((detail: any) => {
          if (detail.employeeId) {
            const employee = this.employeeList.find((emp: any) => emp.id === detail.employeeId)
            if (!employee) {
              // 如果当前选择的员工不在新机构下，清空该明细的员工选择
              detail.employeeId = null
              detail.salaryStandardId = null
              detail.rewardAmount = 0
              detail.deductionAmount = 0
              detail.finalAmount = 0
            }
          }
        })
        this.calculateTotal()
      } else {
        // 如果没有选择机构，清空员工列表
        this.employeeList = []
        // 清空所有明细
        this.ruleForm.details.forEach((detail: any) => {
          detail.employeeId = null
          detail.salaryStandardId = null
          detail.rewardAmount = 0
          detail.deductionAmount = 0
          detail.finalAmount = 0
        })
        this.calculateTotal()
      }
    },
    async loadSalaryStandards() {
      try {
        const res = await getSalaryStandardList({ pageNum: 1, pageSize: 1000, status: '已生效' })
        if (res.data.code == 200) {
          this.salaryStandardList = res.data.data.records
        }
      } catch (error) {
        this.$message.error('获取薪酬标准列表失败')
      }
    },
    async loadData() {
      const id = this.$route.query.id
      try {
        const res = await getSalaryIssueById(id)
        if (res.data.code == 200) {
          this.ruleForm = res.data.data
          if (!this.ruleForm.details) {
            this.ruleForm.details = []
          }
          // 编辑模式下，如果有机构ID，加载该机构的员工
          if (this.ruleForm.orgId) {
            await this.loadEmployees(this.ruleForm.orgId)
          }
          this.calculateTotal()
        }
      } catch (error) {
        this.$message.error('获取薪酬发放详情失败')
      }
    },
    async generateNumber() {
      try {
        const res = await generateSalaryIssueNumber()
        if (res.data.code == 200) {
          this.ruleForm.issueNumber = res.data.data
        }
      } catch (error) {
        // 忽略错误
      }
    },
    addDetail() {
      this.ruleForm.details.push({
        employeeId: null,
        salaryStandardId: null,
        rewardAmount: 0,
        deductionAmount: 0,
        finalAmount: 0
      })
    },
    removeDetail(index: number) {
      this.ruleForm.details.splice(index, 1)
      this.calculateTotal()
    },
    calculateDetailAmount(detail: any) {
      if (detail.salaryStandardId) {
        const standard = this.salaryStandardList.find((s: any) => s.id === detail.salaryStandardId)
        if (standard && standard.totalAmount) {
          const baseAmount = parseFloat(standard.totalAmount) || 0
          const reward = parseFloat(detail.rewardAmount) || 0
          const deduction = parseFloat(detail.deductionAmount) || 0
          detail.finalAmount = baseAmount + reward - deduction
        } else {
          const reward = parseFloat(detail.rewardAmount) || 0
          const deduction = parseFloat(detail.deductionAmount) || 0
          detail.finalAmount = reward - deduction
        }
      } else {
        const reward = parseFloat(detail.rewardAmount) || 0
        const deduction = parseFloat(detail.deductionAmount) || 0
        detail.finalAmount = reward - deduction
      }
      this.calculateTotal()
    },
    calculateTotal() {
      this.totalEmployees = this.ruleForm.details.length
      this.totalAmount = this.ruleForm.details.reduce((sum: number, detail: any) => {
        return sum + (parseFloat(detail.finalAmount) || 0)
      }, 0)
      this.ruleForm.totalAmount = this.totalAmount
      this.ruleForm.totalEmployees = this.totalEmployees
    },
    submitForm(formName: string) {
      this.$refs[formName].validate((valid: boolean) => {
        if (valid) {
          if (this.ruleForm.details.length === 0) {
            this.$message.warning('请至少添加一个员工明细')
            return
          }
          
          // 验证明细数据
          for (let i = 0; i < this.ruleForm.details.length; i++) {
            const detail = this.ruleForm.details[i]
            if (!detail.employeeId) {
              this.$message.warning(`第 ${i + 1} 行：请选择员工`)
              return
            }
            if (!detail.salaryStandardId) {
              this.$message.warning(`第 ${i + 1} 行：请选择薪酬标准`)
              return
            }
          }
          
          const params: any = {
            orgId: this.ruleForm.orgId,
            issueDate: this.ruleForm.issueDate,
            details: this.ruleForm.details.map((detail: any) => ({
              employeeId: detail.employeeId,
              salaryStandardId: detail.salaryStandardId,
              rewardAmount: parseFloat(detail.rewardAmount) || 0,
              deductionAmount: parseFloat(detail.deductionAmount) || 0,
              finalAmount: parseFloat(detail.finalAmount) || 0
            }))
          }
          
          // 如果是编辑模式，保留单号；如果是新增模式，不发送单号（由后端自动生成）
          if (this.optType === 'edit' && this.ruleForm.issueNumber) {
            params.issueNumber = this.ruleForm.issueNumber
          }

          if (this.optType === 'edit') {
            params.id = this.$route.query.id
            updateSalaryIssue(params).then((res: any) => {
              if (res.data.code == 200) {
                this.$message.success('更新薪酬发放成功')
                this.$router.push({ path: '/salaryIssues' })
              } else {
                this.$message.error(res.data.msg || '更新薪酬发放失败')
              }
            }).catch((err: any) => {
              console.error('更新薪酬发放失败:', err)
              this.$message.error('更新薪酬发放失败：' + (err.response && err.response.data && err.response.data.msg ? err.response.data.msg : err.message || '未知错误'))
            })
          } else {
            addSalaryIssue(params).then((res: any) => {
              if (res.data.code == 200) {
                this.$message.success('创建薪酬发放成功')
                this.$router.push({ path: '/salaryIssues' })
              } else {
                this.$message.error(res.data.msg || '创建薪酬发放失败')
              }
            }).catch((err: any) => {
              console.error('创建薪酬发放失败:', err)
              this.$message.error('创建薪酬发放失败：' + (err.response && err.response.data && err.response.data.msg ? err.response.data.msg : err.message || '未知错误'))
            })
          }
        } else {
          this.$message.warning('请填写完整的表单信息')
        }
      })
    },
    resetForm(formName: string) {
      this.$refs[formName].resetFields()
      this.ruleForm.details = []
      this.totalEmployees = 0
      this.totalAmount = 0
    }
  }
}
</script>

<template>
  <div class="addBrand-container">
    <div class="container">
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="120px">
        <el-form-item label="薪酬单号" prop="issueNumber">
          <el-input v-model="ruleForm.issueNumber" disabled placeholder="系统自动生成"></el-input>
        </el-form-item>
        <el-form-item label="发放机构" prop="orgId">
          <el-select v-model="ruleForm.orgId" placeholder="请选择发放机构" style="width: 293px;" @change="handleOrgChange">
            <el-option
              v-for="org in orgList"
              :key="org.id"
              :label="org.orgName"
              :value="org.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="发放日期" prop="issueDate">
          <el-date-picker
            v-model="ruleForm.issueDate"
            type="date"
            placeholder="选择日期"
            style="width: 293px;">
          </el-date-picker>
        </el-form-item>
        
        <el-divider>员工明细</el-divider>
        
        <el-form-item label=" ">
          <el-button type="primary" size="small" @click="addDetail">+ 添加员工</el-button>
        </el-form-item>
        
        <el-table :data="ruleForm.details" border style="width: 100%">
          <el-table-column label="员工" width="200">
            <template slot-scope="scope">
              <el-select v-model="scope.row.employeeId" placeholder="请选择员工" @change="calculateDetailAmount(scope.row)">
                <el-option
                  v-for="emp in employeeList"
                  :key="emp.id"
                  :label="emp.username + ' (' + emp.archiveNumber + ')'"
                  :value="emp.id">
                </el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="薪酬标准" width="200">
            <template slot-scope="scope">
              <el-select v-model="scope.row.salaryStandardId" placeholder="请选择薪酬标准" @change="calculateDetailAmount(scope.row)">
                <el-option
                  v-for="std in salaryStandardList"
                  :key="std.id"
                  :label="std.standardName"
                  :value="std.id">
                </el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="奖励金额" width="150">
            <template slot-scope="scope">
              <el-input-number 
                v-model="scope.row.rewardAmount" 
                :precision="2" 
                :min="0"
                @change="calculateDetailAmount(scope.row)"
                style="width: 100%"></el-input-number>
            </template>
          </el-table-column>
          <el-table-column label="应扣金额" width="150">
            <template slot-scope="scope">
              <el-input-number 
                v-model="scope.row.deductionAmount" 
                :precision="2" 
                :min="0"
                @change="calculateDetailAmount(scope.row)"
                style="width: 100%"></el-input-number>
            </template>
          </el-table-column>
          <el-table-column label="实发金额" width="150">
            <template slot-scope="scope">
              <span style="color: #409EFF; font-weight: bold">
                ¥{{ (scope.row.finalAmount || 0).toFixed(2) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template slot-scope="scope">
              <el-button size="small" type="danger" @click="removeDetail(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <el-form-item label="总人数">
          <span style="font-size: 16px; color: #606266">{{ totalEmployees }} 人</span>
        </el-form-item>
        <el-form-item label="总额">
          <span style="font-size: 18px; color: #409EFF; font-weight: bold">
            ¥{{ totalAmount.toFixed(2) }}
          </span>
        </el-form-item>
        
        <el-form-item>
          <el-button
            v-if="optType == 'add'"
            class="button1" type="primary" @click="submitForm('ruleForm')">立即创建</el-button>
          <el-button
            v-if="optType == 'edit'"
            class="button1" type="primary" @click="submitForm('ruleForm')">保存修改</el-button>
          <el-button class="button2" type="danger" plain @click="resetForm('ruleForm')">重置</el-button>
          <el-button @click="() => this.$router.push({ path: '/salaryIssues' })">返回</el-button>
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

