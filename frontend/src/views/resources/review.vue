<template>
  <div class="review-container">
    <div class="container">
      <h2 style="margin-bottom: 20px;">人力资源档案登记复核</h2>

      <!-- 待复核档案列表 -->
      <div class="table-container">
        <el-table
          :data="records"
          :cell-style="{textAlign: 'center'}"
          :header-cell-style="{textAlign: 'center'}">
          <el-table-column prop="archiveNumber" label="档案编号" ></el-table-column>
          <el-table-column prop="username" label="姓名" ></el-table-column>
          <el-table-column prop="orgName" label="所属机构" ></el-table-column>
          <el-table-column prop="positionName" label="职位"></el-table-column>
          <el-table-column prop="createdAt" label="登记时间" ></el-table-column>
          <el-table-column label="操作" >
            <template slot-scope="scope">
              <el-button size="small" plain type="primary" @click="handleReview(scope.row)">复核</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 复核对话框 -->
      <el-dialog title="人力资源档案复核" :visible.sync="reviewDialogVisible" :center="true">
        <el-form :model="reviewForm" label-width="150px" v-if="currentEmployee">
          <el-form-item label="档案编号">
            <el-input v-model="currentEmployee.archiveNumber" disabled style="width: 200px;"></el-input>
          </el-form-item>
          <el-form-item label="所属机构">
            <el-input v-model="currentEmployee.orgName" disabled style="width: 200px;"></el-input>
          </el-form-item>
          <el-form-item label="职位">
            <el-input v-model="currentEmployee.positionName" disabled style="width: 200px;"></el-input>
          </el-form-item>
          <el-form-item label="姓名" prop="username">
            <el-input v-model="reviewForm.username" style="width: 200px;"></el-input>
          </el-form-item>
          <el-form-item label="性别" prop="gender">
            <el-select v-model="reviewForm.gender" placeholder="请选择性别" style="width: 200px;">
              <el-option label="男" value="男"></el-option>
              <el-option label="女" value="女"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="Email">
            <el-input v-model="reviewForm.email" style="width: 200px;"></el-input>
          </el-form-item>
          <el-form-item label="电话">
            <el-input v-model="reviewForm.phone" style="width: 200px;"></el-input>
          </el-form-item>
          <el-form-item label="手机">
            <el-input v-model="reviewForm.mobile" style="width: 200px;"></el-input>
          </el-form-item>
          <el-form-item label="职称">
            <el-select v-model="reviewForm.title" placeholder="请选择职称" style="width: 200px;">
              <el-option label="初级" value="初级"></el-option>
              <el-option label="中级" value="中级"></el-option>
              <el-option label="高级" value="高级"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="薪酬标准">
            <el-select v-model="reviewForm.salaryStandardId" placeholder="请选择薪酬标准" style="width: 200px;">
              <el-option
                v-for="item in salaryStandardList"
                :key="item.id"
                :label="item.standardName"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="复核意见">
            <el-input v-model="reviewForm.reviewOpinion" type="textarea" :rows="4" style="width: 400px;"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="reviewDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReview" class="button1">通过复核</el-button>
        </div>
      </el-dialog>

      <div style="margin-top: 20px; text-align: center;">
        <el-button @click="() => this.$router.push('/resources')" class="action-button">返回</el-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { queryResourcePage, reviewResourceWithUpdate, queryResourceById } from '@/api/resource'
import { getSalaryStandardList } from '@/api/salaryStandards'

export default {
  data() {
    return {
      records: [],
      reviewDialogVisible: false,
      currentEmployee: null,
      salaryStandardList: [],
      reviewForm: {
        id: null,
        username: '',
        gender: '',
        email: '',
        phone: '',
        mobile: '',
        title: '',
        salaryStandardId: null,
        reviewOpinion: ''
      }
    }
  },

  created() {
    this.loadReviewList()
    this.loadSalaryStandards()
  },

  methods: {
    loadReviewList() {
      queryResourcePage({ status: '待复核', pageNum: 1, pageSize: 1000 }).then((res: any) => {
        if (res.data.code == 200) {
          this.records = res.data.data.records
        }
      }).catch((err) => {
        this.$message.error('获取待复核档案列表失败')
      })
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

    async handleReview(row: any) {
      try {
        const res = await queryResourceById(row.id)
        if (res.data.code == 200) {
          this.currentEmployee = res.data.data
          this.reviewForm = {
            id: row.id,
            username: res.data.data.username,
            gender: res.data.data.gender,
            email: res.data.data.email,
            phone: res.data.data.phone,
            mobile: res.data.data.mobile,
            title: res.data.data.title,
            salaryStandardId: res.data.data.salaryStandardId,
            reviewOpinion: ''
          }
          this.reviewDialogVisible = true
        }
      } catch (error) {
        this.$message.error('获取档案详情失败')
      }
    },

    async submitReview() {
      // 复核时可以修改信息，使用支持更新的复核接口
      try {
        const reviewParams = {
          id: this.reviewForm.id,
          username: this.reviewForm.username,
          gender: this.reviewForm.gender,
          email: this.reviewForm.email,
          phone: this.reviewForm.phone,
          mobile: this.reviewForm.mobile,
          title: this.reviewForm.title,
          salaryStandardId: this.reviewForm.salaryStandardId
        }

        // 调用支持更新的复核接口
        const res = await reviewResourceWithUpdate(reviewParams, this.reviewForm.reviewOpinion)
        if (res.data.code == 200) {
          this.$message.success('复核成功')
          this.reviewDialogVisible = false
          this.loadReviewList()
        } else {
          this.$message.error(res.data.msg || '复核失败')
        }
      } catch (err) {
        this.$message.error('复核失败：' + (err.response && err.response.data && err.response.data.msg ? err.response.data.msg : err.message))
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.review-container {
  margin: 30px;
  margin-top: 30px;
  .container {
    position: relative;
    z-index: 1;
    background: #fff;
    padding: 30px;
    border-radius: 4px;
  }
}

/* 表格容器样式 - 实现表格在页面中居中 */
.table-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 确保表格不会过宽并保持居中 */
::v-deep .el-table {
  max-width: 100%;
  width: fit-content;
  margin: 0 auto;
}

/* 表格单元格垂直居中 */
::v-deep .el-table td {
  vertical-align: middle !important;
}

/* 表头垂直居中 */
::v-deep .el-table th {
  vertical-align: middle !important;
}

::v-deep .el-table__header-wrapper {
  th {
    background-color: rgba(96, 98, 102, 0.24) !important;
    color: #606266 !important;
    font-weight: bold !important;
  }
}

.button1 {
  color: #FFFFFF;
  background-color: #409EFF !important;
  border-color: #409EFF !important;
}

.action-button {
  background-color: transparent !important;
  color: #409EFF !important;
  border-color: #409EFF !important;
  &:hover {
    background-color: #ecf5ff !important;
    border-color: #409EFF !important;
    color: #409EFF !important;
  }
}
</style>

