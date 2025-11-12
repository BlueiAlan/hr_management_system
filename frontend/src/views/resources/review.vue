<template>
  <div class="review-container">
    <div class="container">
      <h2 style="margin-bottom: 20px;">人力资源档案登记复核</h2>

      <el-form :model="reviewForm" label-width="120px" v-if="reviewForm.id">
        <!-- 个人信息部分 -->
        <div class="form-section">
          <h3 class="section-title">个人信息</h3>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="档案编号">
                <el-input v-model="reviewForm.archiveNumber" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="姓名">
                <el-input v-model="reviewForm.username" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="性别">
                <el-select v-model="reviewForm.gender" placeholder="请选择性别" style="width: 100%;">
                  <el-option label="男" value="男"></el-option>
                  <el-option label="女" value="女"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="身份证号码">
                <el-input v-model="reviewForm.idCard" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 机构与职位信息部分 -->
        <div class="form-section">
          <h3 class="section-title">机构与职位信息</h3>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="所属机构">
                <el-input v-model="reviewForm.orgName" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="职位">
                <el-input v-model="reviewForm.positionName" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="职称">
                <el-select v-model="reviewForm.title" placeholder="请选择职称" style="width: 100%;">
                  <el-option label="初级" value="初级"></el-option>
                  <el-option label="中级" value="中级"></el-option>
                  <el-option label="高级" value="高级"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="薪酬标准">
                <el-select v-model="reviewForm.salaryStandardId" placeholder="请选择薪酬标准" style="width: 100%;">
                  <el-option
                    v-for="item in salaryStandardList"
                    :key="item.id"
                    :label="item.standardName"
                    :value="item.id">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="状态">
                <el-tag type="warning">{{ reviewForm.status }}</el-tag>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 更多个人信息部分 -->
        <div class="form-section">
          <h3 class="section-title">个人信息</h3>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="出生日期">
                <el-input v-model="reviewForm.birthDate" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="民族">
                <el-input v-model="reviewForm.ethnicity" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="学历">
                <el-input v-model="reviewForm.education" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="Email">
                <el-input v-model="reviewForm.email" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="电话">
                <el-input v-model="reviewForm.phone" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="手机">
                <el-input v-model="reviewForm.mobile" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="住址">
                <el-input v-model="reviewForm.address" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="邮编">
                <el-input v-model="reviewForm.postalCode" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="国籍">
                <el-input v-model="reviewForm.nationality" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="出生地">
                <el-input v-model="reviewForm.birthPlace" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="宗教信仰">
                <el-input v-model="reviewForm.religion" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="政治面貌">
                <el-select v-model="reviewForm.politicalStatus" placeholder="选择政治面貌" clearable style="width: 100%;">
                  <el-option label="群众" value="群众"></el-option>
                  <el-option label="共青团员" value="共青团员"></el-option>
                  <el-option label="中共党员" value="中共党员"></el-option>
                  <el-option label="民主党派" value="民主党派"></el-option>
                  <el-option label="无党派人士" value="无党派人士"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="学历专业">
                <el-select v-model="reviewForm.major" placeholder="选择学历专业" clearable style="width: 100%;">
                  <el-option label="计算机科学与技术" value="计算机科学与技术"></el-option>
                  <el-option label="软件工程" value="软件工程"></el-option>
                  <el-option label="信息管理与信息系统" value="信息管理与信息系统"></el-option>
                  <el-option label="工商管理" value="工商管理"></el-option>
                  <el-option label="人力资源管理" value="人力资源管理"></el-option>
                  <el-option label="会计学" value="会计学"></el-option>
                  <el-option label="金融学" value="金融学"></el-option>
                  <el-option label="市场营销" value="市场营销"></el-option>
                  <el-option label="其他" value="其他"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="年龄">
                <el-input v-model="reviewForm.age" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="QQ">
                <el-input v-model="reviewForm.qq" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 其他信息部分 -->
        <div class="form-section">
          <h3 class="section-title">其他信息</h3>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="爱好">
                <el-input v-model="reviewForm.hobbies" type="textarea" :rows="3" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="个人履历">
                <el-input v-model="reviewForm.resume" type="textarea" :rows="5" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="家庭关系信息">
                <el-input v-model="reviewForm.familyInfo" type="textarea" :rows="5" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="备注">
                <el-input v-model="reviewForm.remarks" type="textarea" :rows="3" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="subBox" style="margin-top: 30px;">
          <el-button size="small" @click="() => this.$router.push('/resources')">取消</el-button>
          <el-button size="small" type="danger" @click="submitReview(false)" style="margin-left: 10px;">不通过</el-button>
          <el-button size="small" type="primary" @click="submitReview(true)" style="margin-left: 10px;" class="button1">通过复核</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts">
import { queryResourceById, reviewResourceWithUpdate } from '@/api/resource'
import { getSalaryStandardList } from '@/api/salaryStandards'

export default {
  data() {
    return {
      salaryStandardList: [],
      reviewForm: {
        id: null,
        archiveNumber: '',
        username: '',
        gender: '',
        idCard: '',
        orgName: '',
        positionName: '',
        title: '',
        salaryStandardId: null,
        email: '',
        phone: '',
        mobile: '',
        address: '',
        postalCode: '',
        nationality: '',
        birthPlace: '',
        birthDate: '',
        ethnicity: '',
        religion: '',
        politicalStatus: '',
        age: null,
        education: '',
        major: '',
        qq: '',
        hobbies: '',
        resume: '',
        familyInfo: '',
        remarks: '',
        status: ''
      }
    }
  },

  created() {
    this.loadData()
    this.loadSalaryStandards()
  },

  methods: {
    async loadData() {
      const id = this.$route.query.id
      if (!id) {
        this.$message.error('缺少档案ID')
        this.$router.push('/resources')
        return
      }
      try {
        const res = await queryResourceById(id)
        if (res.data.code == 200) {
          this.reviewForm = { ...res.data.data }
          // 确保id存在，以便表单显示
          if (!this.reviewForm.id) {
            this.reviewForm.id = res.data.data.id
          }
        } else {
          this.$message.error('获取档案详情失败')
          this.$router.push('/resources')
        }
      } catch (error) {
        this.$message.error('获取档案详情失败')
        this.$router.push('/resources')
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

    async submitReview(isApproved: boolean) {
      // 移除复核意见必填验证
      console.log('提交复核，isApproved:', isApproved)
      try {
        const reviewParams = {
          id: this.reviewForm.id,
          username: this.reviewForm.username,
          gender: this.reviewForm.gender,
          email: this.reviewForm.email,
          phone: this.reviewForm.phone,
          mobile: this.reviewForm.mobile,
          title: this.reviewForm.title,
          salaryStandardId: this.reviewForm.salaryStandardId,
          address: this.reviewForm.address,
          postalCode: this.reviewForm.postalCode,
          nationality: this.reviewForm.nationality,
          birthPlace: this.reviewForm.birthPlace,
          birthDate: this.reviewForm.birthDate,
          ethnicity: this.reviewForm.ethnicity,
          religion: this.reviewForm.religion,
          politicalStatus: this.reviewForm.politicalStatus,
          age: this.reviewForm.age,
          education: this.reviewForm.education,
          qq: this.reviewForm.qq,
          hobbies: this.reviewForm.hobbies,
          resume: this.reviewForm.resume,
          familyInfo: this.reviewForm.familyInfo,
          remarks: this.reviewForm.remarks
        }

        console.log('调用复核API，isApproved:', isApproved)
        const res = await reviewResourceWithUpdate(reviewParams, isApproved)
        if (res.data.code == 200) {
          this.$message.success('复核成功')
          this.$router.push('/resources')
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
}

.container {
  background: #fff;
  padding: 30px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  
  .subBox {
    text-align: center;
    margin-top: 20px;
  }
  
  .el-form-item {
    margin-bottom: 22px;
  }
  
  .form-section {
    margin-bottom: 30px;
    .section-title {
      font-size: 16px;
      font-weight: bold;
      color: #333;
      margin-bottom: 20px;
      padding-bottom: 10px;
      border-bottom: 1px solid #e5e5e5;
    }
  }
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
