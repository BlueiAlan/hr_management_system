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
        <div class="table-wrapper" v-if="reviewForm.details.length > 0 && hasSalaryStandards()">
          <el-table
            :data="reviewForm.details"
            border
            style="width: 100%; min-width: 800px;"
            :cell-style="{'text-align':'center', 'vertical-align': 'middle', 'padding': '8px 5px'}"
            :header-cell-style="{'text-align':'center', 'background-color': '#f5f7fa', 'font-weight': 'bold', 'vertical-align': 'middle', 'padding': '12px 5px'}">
            <el-table-column label="姓名" width="120" fixed="left" align="center">
              <template slot-scope="scope">
                <span v-if="scope.row.employeeName" style="display: inline-block; width: 100%; text-align: center;">{{ scope.row.employeeName }}</span>
                <span v-else style="color: #909399; display: inline-block; width: 100%; text-align: center;">-</span>
              </template>
            </el-table-column>
            <el-table-column label="职位" width="130" align="center">
              <template slot-scope="scope">
                <span v-if="scope.row.positionName" style="display: inline-block; width: 100%; text-align: center;">{{ scope.row.positionName || '-' }}</span>
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

            <el-table-column label="奖励金额" width="120" align="center">
              <template slot-scope="scope">
                <span style="display: inline-block; width: 100%; text-align: center;">
                  {{ scope.row.rewardAmount ? '¥' + parseFloat(scope.row.rewardAmount).toFixed(2) : '¥0.00' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="应扣金额" width="120" align="center">
              <template slot-scope="scope">
                <span style="display: inline-block; width: 100%; text-align: center;">
                  {{ scope.row.deductionAmount ? '¥' + parseFloat(scope.row.deductionAmount).toFixed(2) : '¥0.00' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="实发金额" width="120" align="center">
              <template slot-scope="scope">
                <span style="color: #409EFF; font-weight: bold; display: inline-block; width: 100%; text-align: center;">
                  {{ scope.row.finalAmount ? '¥' + parseFloat(scope.row.finalAmount).toFixed(2) : '¥0.00' }}
                </span>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div v-else-if="reviewForm.details.length === 0" class="empty-tip">
          <p style="text-align: center; color: #909399; padding: 40px 0;">暂无发放明细</p>
        </div>
        <div v-else-if="!hasSalaryStandards()" class="empty-tip">
          <p style="text-align: center; color: #909399; padding: 40px 0;">所选员工均没有薪酬标准，无法显示薪酬明细</p>
        </div>

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
import { getSalaryStandardById } from '@/api/salaryStandards'
import { getEmployeeList } from '@/api/employee'

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
      },
      employeeList: []
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
          // 加载员工列表以获取职位信息
          await this.loadEmployeeList()
          
          // 为每个明细填充员工职位信息
          this.reviewForm.details.forEach((detail: any) => {
            if (detail.employeeId) {
              const employee = this.employeeList.find((emp: any) => emp.id === detail.employeeId)
              if (employee) {
                detail.positionName = employee.positionName || '-'
              }
            }
          })
          
          // 为每个明细加载薪酬标准详情
          for (const detail of this.reviewForm.details) {
            if (detail.salaryStandardId) {
              await this.loadSalaryStandardDetails(detail, detail.salaryStandardId)
            } else {
              // 如果没有薪酬标准ID，初始化空数组
              this.$set(detail, 'salaryStandardDetails', [])
            }
          }
          
          // 确保所有明细都有 salaryStandardDetails 字段
          this.reviewForm.details.forEach((detail: any) => {
            if (!detail.salaryStandardDetails) {
              this.$set(detail, 'salaryStandardDetails', [])
            }
          })
          
          // 调试：打印所有明细的薪酬标准信息
          console.log('加载完成后的明细数据:', this.reviewForm.details.map((d: any) => ({
            employeeName: d.employeeName,
            salaryStandardId: d.salaryStandardId,
            hasDetails: !!d.salaryStandardDetails,
            detailsLength: d.salaryStandardDetails ? d.salaryStandardDetails.length : 0
          })))
          
          // 确保reviewOpinion字段存在且可编辑
          if (this.reviewForm.reviewOpinion === undefined || this.reviewForm.reviewOpinion === null) {
            this.reviewForm.reviewOpinion = ''
          }
          // 确保id存在，以便表单显示
          if (!this.reviewForm.id) {
            this.reviewForm.id = data.id
          }
          
          // 强制更新视图
          this.$forceUpdate()
        } else {
          this.$message.error('获取薪酬发放详情失败')
          this.$router.push('/salaryIssues')
        }
      } catch (error) {
        this.$message.error('获取薪酬发放详情失败')
        this.$router.push('/salaryIssues')
      }
    },
    // 加载员工列表
    async loadEmployeeList() {
      try {
        const res = await getEmployeeList({ pageNum: 1, pageSize: 10000, status: '正常' })
        if (res.data.code == 200) {
          this.employeeList = res.data.data.records || []
        }
      } catch (error) {
        console.error('获取员工列表失败', error)
        this.employeeList = []
      }
    },
    // 加载薪酬标准详情
    async loadSalaryStandardDetails(detail: any, standardId: number) {
      try {
        const res = await getSalaryStandardById(standardId)
        if (res.data.code == 200) {
          // 使用 Vue.set 确保响应式更新
          this.$set(detail, 'salaryStandardDetails', res.data.data.details || [])
          console.log('加载薪酬标准详情成功:', detail.salaryStandardDetails)
        } else {
          console.error('获取薪酬标准详情失败，返回码:', res.data.code)
          this.$set(detail, 'salaryStandardDetails', [])
        }
      } catch (error) {
        console.error('获取薪酬标准详情失败', error)
        this.$set(detail, 'salaryStandardDetails', [])
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
    // 获取所有员工薪酬标准中的项目名称列表（以薪酬标准项目最多的员工为基准）
    getAllSalaryItemNames() {
      // 找到薪酬标准项目最多的员工
      let maxItemsDetail: any = null
      let maxItemsCount = 0

      this.reviewForm.details.forEach((detail: any) => {
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
      const hasStandards = this.reviewForm.details.some((detail: any) => {
        const has = detail.salaryStandardDetails && detail.salaryStandardDetails.length > 0
        if (!has && detail.salaryStandardId) {
          console.log('明细有薪酬标准ID但没有详情:', detail)
        }
        return has
      })
      console.log('是否有薪酬标准:', hasStandards, '明细数量:', this.reviewForm.details.length)
      return hasStandards
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

::v-deep .el-table__fixed {
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
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


