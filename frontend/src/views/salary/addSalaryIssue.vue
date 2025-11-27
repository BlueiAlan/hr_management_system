<script lang="ts">
import { addSalaryIssue, getSalaryIssueById, updateSalaryIssue, generateSalaryIssueNumber } from '@/api/salaryIssues'
import { getOrgList } from '@/api/organizations'
import { getEmployeeList } from '@/api/employee'
import { getSalaryStandardList, getSalaryStandardById } from '@/api/salaryStandards'
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
      selectedEmployeeIds: [], // 选中的员工ID列表
      showEmployeeDialog: false, // 显示员工选择对话框
      totalEmployees: 0,
      totalAmount: 0
    }
  },
  created() {
    this.loadOrgs()
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
      if (this.ruleForm.orgId) {
        await this.loadEmployees(this.ruleForm.orgId)
        // 清空已选择的员工
        this.ruleForm.details.forEach((detail: any) => {
          detail.employeeId = null
          detail.employeeInfo = null
          detail.salaryStandardId = null
          detail.salaryStandardDetails = []
          detail.rewardAmount = 0
          detail.deductionAmount = 0
          detail.finalAmount = 0
        })
        this.calculateTotal()
      } else {
        this.employeeList = []
        this.ruleForm.details.forEach((detail: any) => {
          detail.employeeId = null
          detail.employeeInfo = null
          detail.salaryStandardId = null
          detail.salaryStandardDetails = []
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
    // 根据员工职位自动匹配薪酬标准
    async handleEmployeeChange(detail: any) {
      if (!detail.employeeId) {
        detail.employeeInfo = null
        detail.salaryStandardId = null
        detail.salaryStandardDetails = []
        detail.rewardAmount = 0
        detail.deductionAmount = 0
        detail.finalAmount = 0
        this.calculateDetailAmount(detail)
        return
      }

      // 获取员工信息
      const employee = this.employeeList.find((emp: any) => emp.id === detail.employeeId)
      if (!employee) {
        return
      }

      detail.employeeInfo = {
        id: employee.id,
        archiveNumber: employee.archiveNumber,
        username: employee.username,
        positionId: employee.positionId,
        positionName: employee.positionName
      }

      // 根据员工的职位ID查找对应的薪酬标准
      if (employee.positionId) {
        const matchedStandard = this.salaryStandardList.find((std: any) => std.positionId === employee.positionId)
        if (matchedStandard) {
          detail.salaryStandardId = matchedStandard.id
          // 加载薪酬标准详情
          await this.loadSalaryStandardDetails(detail, matchedStandard.id)
        } else {
          this.$message.warning(`员工 ${employee.username} 的职位没有对应的薪酬标准`)
          detail.salaryStandardId = null
          detail.salaryStandardDetails = []
        }
      } else {
        this.$message.warning(`员工 ${employee.username} 没有设置职位`)
        detail.salaryStandardId = null
        detail.salaryStandardDetails = []
      }

      this.calculateDetailAmount(detail)
    },
    // 加载薪酬标准详情
    async loadSalaryStandardDetails(detail: any, standardId: number) {
      try {
        const res = await getSalaryStandardById(standardId)
        if (res.data.code == 200) {
          detail.salaryStandardDetails = res.data.data.details || []
        }
      } catch (error) {
        this.$message.error('获取薪酬标准详情失败')
        detail.salaryStandardDetails = []
      }
    },
    // 获取员工某个薪酬项目的金额
    getSalaryItemAmount(detail: any, itemName: string) {
      if (!detail.salaryStandardDetails || detail.salaryStandardDetails.length === 0) {
        return 0
      }
      const item = detail.salaryStandardDetails.find((d: any) => d.itemName === itemName)
      return item ? parseFloat(item.amount) || 0 : 0
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
          // 编辑模式下，加载员工和薪酬标准详情
          if (this.ruleForm.orgId) {
            await this.loadEmployees(this.ruleForm.orgId)
            // 为每个明细加载薪酬标准详情
            for (const detail of this.ruleForm.details) {
              if (detail.employeeId) {
                const employee = this.employeeList.find((emp: any) => emp.id === detail.employeeId)
                if (employee) {
                  detail.employeeInfo = {
                    id: employee.id,
                    archiveNumber: employee.archiveNumber,
                    username: employee.username,
                    positionId: employee.positionId,
                    positionName: employee.positionName
                  }
                }
              }
              if (detail.salaryStandardId) {
                await this.loadSalaryStandardDetails(detail, detail.salaryStandardId)
              }
            }
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
    removeDetail(index: number) {
      this.ruleForm.details.splice(index, 1)
      this.calculateTotal()
    },
    calculateDetailAmount(detail: any) {
      // 计算基础薪酬总额
      let baseAmount = 0
      if (detail.salaryStandardDetails && detail.salaryStandardDetails.length > 0) {
        detail.salaryStandardDetails.forEach((item: any) => {
          const amount = parseFloat(item.amount) || 0
          if (item.itemType === '收入') {
            baseAmount += amount
          } else if (item.itemType === '扣除') {
            baseAmount -= amount
          }
        })
      }

      const reward = parseFloat(detail.rewardAmount) || 0
      const deduction = parseFloat(detail.deductionAmount) || 0
      detail.finalAmount = baseAmount + reward - deduction
      // 确保金额为数字类型
      detail.rewardAmount = reward
      detail.deductionAmount = deduction
      this.calculateTotal()
    },
    calculateTotal() {
      this.totalEmployees = this.ruleForm.details.filter((d: any) => d.employeeId).length
      this.totalAmount = this.ruleForm.details.reduce((sum: number, detail: any) => {
        return sum + (parseFloat(detail.finalAmount) || 0)
      }, 0)
      this.ruleForm.totalAmount = this.totalAmount
      this.ruleForm.totalEmployees = this.totalEmployees
    },
    // 获取所有员工薪酬标准中的项目名称列表（以薪酬标准项目最多的员工为基准）
    getAllSalaryItemNames() {
      // 找到薪酬标准项目最多的员工
      let maxItemsDetail: any = null
      let maxItemsCount = 0

      this.ruleForm.details.forEach((detail: any) => {
        if (detail.salaryStandardDetails && detail.salaryStandardDetails.length > maxItemsCount) {
          maxItemsCount = detail.salaryStandardDetails.length
          maxItemsDetail = detail
        }
      })

      // 如果没有找到有薪酬标准的员工，返回空数组
      if (!maxItemsDetail || !maxItemsDetail.salaryStandardDetails || maxItemsDetail.salaryStandardDetails.length === 0) {
        return []
      }

      // 以薪酬标准项目最多的员工为基准，获取项目列表
      const incomeItems: string[] = []
      const deductionItems: string[] = []

      maxItemsDetail.salaryStandardDetails.forEach((item: any) => {
        if (item.itemName && item.itemName.trim()) {
          if (item.itemType === '收入') {
            incomeItems.push(item.itemName)
          } else if (item.itemType === '扣除') {
            deductionItems.push(item.itemName)
          }
        }
      })

      // 返回收入类型在前，扣除类型在后的列表
      return [...incomeItems, ...deductionItems]
    },
    // 判断是否有员工有薪酬标准
    hasSalaryStandards() {
      return this.ruleForm.details.some((detail: any) =>
        detail.salaryStandardDetails && detail.salaryStandardDetails.length > 0
      )
    },
    // 处理员工选择变化
    handleEmployeeSelectionChange(selection: any[]) {
      this.selectedEmployeeIds = selection.map((emp: any) => emp.id)
    },
    // 打开员工选择对话框
    openEmployeeDialog() {
      if (!this.ruleForm.orgId) {
        this.$message.warning('请先选择发放机构')
        return
      }
      if (this.employeeList.length === 0) {
        this.$message.warning('该机构下没有员工')
        return
      }
      this.selectedEmployeeIds = this.ruleForm.details.map((d: any) => d.employeeId).filter((id: any) => id)
      this.showEmployeeDialog = true
      // 等待DOM更新后设置选中状态
      this.$nextTick(() => {
        if (this.$refs.employeeTable) {
          this.employeeList.forEach((emp: any) => {
            if (this.selectedEmployeeIds.includes(emp.id)) {
              (this.$refs.employeeTable as any).toggleRowSelection(emp, true)
            }
          })
        }
      })
    },
    // 确认添加选中的员工
    async confirmAddEmployees() {
      if (this.selectedEmployeeIds.length === 0) {
        this.$message.warning('请至少选择一个员工')
        return
      }

      // 获取已存在的员工ID
      const existingEmployeeIds = this.ruleForm.details.map((d: any) => d.employeeId).filter((id: any) => id)

      // 添加新选中的员工
      const newDetails: any[] = []
      this.selectedEmployeeIds.forEach((employeeId: number) => {
        if (!existingEmployeeIds.includes(employeeId)) {
          newDetails.push({
            employeeId: employeeId,
            employeeInfo: null,
            salaryStandardId: null,
            salaryStandardDetails: [],
            rewardAmount: 0,
            deductionAmount: 0,
            finalAmount: 0
          })
        }
      })

      // 添加新员工到列表
      this.ruleForm.details.push(...newDetails)

      // 为每个新添加的员工加载信息
      for (const detail of newDetails) {
        if (detail.employeeId) {
          await this.handleEmployeeChange(detail)
        }
      }

      this.showEmployeeDialog = false
      this.selectedEmployeeIds = []
      this.calculateTotal()
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
          <el-input v-model="ruleForm.issueNumber" disabled placeholder="系统自动生成" style="width: 293px;"></el-input>
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
            value-format="yyyy-MM-dd"
            style="width: 293px;">
          </el-date-picker>
        </el-form-item>

        <el-divider>员工明细</el-divider>

        <el-form-item label=" ">
          <el-button type="primary" size="small" @click="openEmployeeDialog">+ 添加员工</el-button>
        </el-form-item>

        <div class="table-wrapper" v-if="ruleForm.details.length > 0 && hasSalaryStandards()">
          <el-table
            :data="ruleForm.details"
            border
            style="width: 100%; min-width: 800px;"
            :cell-style="{'text-align':'center', 'vertical-align': 'middle', 'padding': '8px 5px'}"
            :header-cell-style="{'text-align':'center', 'background-color': '#f5f7fa', 'font-weight': 'bold', 'vertical-align': 'middle', 'padding': '12px 5px'}">
            <el-table-column label="姓名" width="120" fixed="left" align="center">
              <template slot-scope="scope">
                <span v-if="scope.row.employeeInfo" style="display: inline-block; width: 100%; text-align: center;">{{ scope.row.employeeInfo.username }}</span>
                <span v-else style="color: #909399; display: inline-block; width: 100%; text-align: center;">-</span>
              </template>
            </el-table-column>
            <el-table-column label="职位" width="130" align="center">
              <template slot-scope="scope">
                <span v-if="scope.row.employeeInfo" style="display: inline-block; width: 100%; text-align: center;">{{ scope.row.employeeInfo.positionName || '-' }}</span>
                <span v-else style="color: #909399; display: inline-block; width: 100%; text-align: center;">-</span>
              </template>
            </el-table-column>

            <!-- 动态生成薪酬项目列 - 以薪酬标准项目最多的员工为基准 -->
            <el-table-column
              v-for="itemName in getAllSalaryItemNames()"
              :key="itemName"
              :label="itemName"
              width="120"
              align="center"
              show-overflow-tooltip>
              <template slot-scope="scope">
                <span style="color: #606266; display: inline-block; width: 100%; text-align: center;">
                  {{ getSalaryItemAmount(scope.row, itemName) > 0 ? '¥' + getSalaryItemAmount(scope.row, itemName).toFixed(2) : '-' }}
                </span>
              </template>
            </el-table-column>

            <el-table-column label="奖励金额" width="120"  align="center">
              <template slot-scope="scope">
                <div style="text-align: center; padding: 5px 0;">
                  <el-input
                    v-model.number="scope.row.rewardAmount"
                    type="number"
                    step="0.01"
                    @blur="calculateDetailAmount(scope.row)"
                    @keyup.enter="calculateDetailAmount(scope.row)"
                    style="width: 90px; margin: 0 auto;"
                    size="small"
                    placeholder="0.00"></el-input>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="应扣金额" width="120"  align="center">
              <template slot-scope="scope">
                <div style="text-align: center; padding: 5px 0;">
                  <el-input
                    v-model.number="scope.row.deductionAmount"
                    type="number"
                    step="0.01"
                    @blur="calculateDetailAmount(scope.row)"
                    @keyup.enter="calculateDetailAmount(scope.row)"
                    style="width: 90px; margin: 0 auto;"
                    size="small"
                    placeholder="0.00"></el-input>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="实发金额" width="120"  align="center">
              <template slot-scope="scope">
                <span style="color: #409EFF; font-weight: bold; display: inline-block; width: 100%; text-align: center;">
                  ¥{{ (scope.row.finalAmount || 0).toFixed(2) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100"  align="center">
              <template slot-scope="scope">
                <div style="text-align: center; padding: 0 5px;">
                  <el-button size="mini" type="danger" @click="removeDetail(scope.$index)" style="margin: 0;">删除</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div v-else-if="ruleForm.details.length === 0" class="empty-tip">
          <p style="text-align: center; color: #909399; padding: 40px 0;">请点击"添加员工"按钮选择员工</p>
        </div>
        <div v-else-if="!hasSalaryStandards()" class="empty-tip">
          <p style="text-align: center; color: #909399; padding: 40px 0;">所选员工均没有薪酬标准，无法显示薪酬明细</p>
        </div>

        <!-- 员工选择对话框 -->
        <el-dialog
          title="选择员工"
          :visible.sync="showEmployeeDialog"
          width="600px">
          <el-table
            :data="employeeList"
            border
            ref="employeeTable"
            @selection-change="handleEmployeeSelectionChange">
            <el-table-column type="selection" width="55" :reserve-selection="true"></el-table-column>
            <el-table-column prop="username" label="姓名" width="120"></el-table-column>
            <el-table-column prop="positionName" label="职位" width="150"></el-table-column>
            <el-table-column prop="archiveNumber" label="档案编号" width="150"></el-table-column>
          </el-table>
          <span slot="footer" class="dialog-footer">
            <el-button @click="showEmployeeDialog = false">取消</el-button>
            <el-button type="primary" @click="confirmAddEmployees">确定</el-button>
          </span>
        </el-dialog>

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
            class="button1" type="primary" @click="submitForm('ruleForm')">提交薪酬发放登记</el-button>
          <el-button
            v-if="optType == 'edit'"
            class="button1" type="primary" @click="submitForm('ruleForm')">保存修改</el-button>
          <el-button class="button2" type="danger" plain @click="resetForm('ruleForm')">重置</el-button>
          <el-button @click="() => this.$router.push({ path: '/salaryIssues' })">返回列表</el-button>
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
  }
}

.table-wrapper {
  overflow-x: auto;
  overflow-y: hidden;
  margin-bottom: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  width: 100%;
}

.table-wrapper ::v-deep .el-table {
  border-radius: 4px;
  min-width: 100%;
}

.table-wrapper ::v-deep .el-table__body-wrapper {
  overflow-x: auto;
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

::v-deep .el-table__header-wrapper {
  th {
    background-color: rgba(96, 98, 102, 0.24) !important;
    color: #606266 !important;
    font-weight: bold !important;
    text-align: center !important;
    padding: 12px 5px !important;
  }
}

::v-deep .el-table td {
  text-align: center !important;
  vertical-align: middle !important;
  padding: 8px 5px !important;
}

::v-deep .el-table th {
  text-align: center !important;
  vertical-align: middle !important;
  padding: 12px 5px !important;
}

::v-deep .el-table .el-button {
  margin: 0 !important;
  padding: 7px 15px !important;
}

::v-deep .el-table .el-input__inner {
  text-align: center !important;
}

::v-deep .el-table--border {
  border: 1px solid #EBEEF5;
}

::v-deep .el-table--border td,
::v-deep .el-table--border th {
  border-right: 1px solid #EBEEF5;
}

::v-deep .el-table--border::after {
  width: 0;
}

::v-deep .el-table--border::before {
  height: 0;
}

::v-deep .el-table__fixed {
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

::v-deep .el-table__fixed-right {
  right: 0 !important;
  border-left: 1px solid #EBEEF5;
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.1);
}

::v-deep .el-table__fixed-right-patch {
  border-bottom: 1px solid #EBEEF5;
}

::v-deep .el-table__fixed-left {
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

::v-deep .el-table__fixed-left-patch {
  border-bottom: 1px solid #EBEEF5;
}

/* 取消全局样式中的padding设置 */
::v-deep .el-table .cell {
  padding-left: 0 !important;
  padding-right: 0 !important;
}

::v-deep .el-table th div {
  padding-left: 0 !important;
  padding-right: 0 !important;
}

::v-deep .el-table--border td:first-child .cell {
  padding-left: 0 !important;
  padding-right: 0 !important;
}

::v-deep .el-table--border th:first-child .cell {
  padding-left: 0 !important;
  padding-right: 0 !important;
}
</style>
