<template>
  <div class="detail-container">
    <div class="container">
      <h2 style="margin-bottom: 20px;">人力资源档案详情</h2>
      <el-form :model="ruleForm" label-width="120px" v-if="ruleForm.id">
        <!-- 个人信息部分 -->
        <div class="form-section">
          <h3 class="section-title">个人信息</h3>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="档案编号">
                <el-input v-model="ruleForm.archiveNumber" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="姓名">
                <el-input v-model="ruleForm.username" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="性别">
                <el-input v-model="ruleForm.gender" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="身份证号码">
                <el-input v-model="ruleForm.idCard" disabled style="width: 100%;"></el-input>
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
                <el-input v-model="ruleForm.orgName" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="职位">
                <el-input v-model="ruleForm.positionName" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="职称">
                <el-input v-model="ruleForm.title" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="薪酬标准">
                <el-input v-model="ruleForm.salaryStandardName" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="状态">
                <el-tag v-if="ruleForm.status === '正常'" type="success">{{ ruleForm.status }}</el-tag>
                <el-tag v-else-if="ruleForm.status === '待复核'" type="warning">{{ ruleForm.status }}</el-tag>
                <el-tag v-else type="info">{{ ruleForm.status }}</el-tag>
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
                <el-input v-model="ruleForm.birthDate" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="民族">
                <el-input v-model="ruleForm.ethnicity" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="学历">
                <el-input v-model="ruleForm.education" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="Email">
                <el-input v-model="ruleForm.email" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="电话">
                <el-input v-model="ruleForm.phone" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="手机">
                <el-input v-model="ruleForm.mobile" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="住址">
                <el-input v-model="ruleForm.address" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="邮编">
                <el-input v-model="ruleForm.postalCode" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="国籍">
                <el-input v-model="ruleForm.nationality" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="出生地">
                <el-input v-model="ruleForm.birthPlace" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="宗教信仰">
                <el-input v-model="ruleForm.religion" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="政治面貌">
                <el-input v-model="ruleForm.politicalStatus" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="学历专业">
                <el-input v-model="ruleForm.major" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="年龄">
                <el-input v-model="ruleForm.age" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="QQ">
                <el-input v-model="ruleForm.qq" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 其他信息部分 -->
        <div class="form-section">
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="爱好">
                <el-input v-model="ruleForm.hobbies" type="textarea" :rows="3" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="个人履历">
                <el-input v-model="ruleForm.resume" type="textarea" :rows="5" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="家庭关系信息">
                <el-input v-model="ruleForm.familyInfo" type="textarea" :rows="5" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="备注">
                <el-input v-model="ruleForm.remarks" type="textarea" :rows="3" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="照片">
                <img v-if="ruleForm.photoPath" :src="ruleForm.photoPath" style="max-width: 200px; max-height: 200px;" />
                <span v-else>暂无照片</span>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="subBox">
          <el-button @click="() => this.$router.push('/resources')" class="action-button">返回</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts">
import { queryResourceById } from '@/api/resource'

export default {
  data() {
    return {
      ruleForm: {
        id: null,
        archiveNumber: '',
        username: '',
        gender: '',
        orgName: '',
        positionName: '',
        title: '',
        salaryStandardName: '',
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
        idCard: '',
        age: null,
        education: '',
        major: '',
        qq: '',
        hobbies: '',
        resume: '',
        familyInfo: '',
        remarks: '',
        photoPath: '',
        status: ''
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
        this.$message.error('缺少档案ID')
        this.$router.push('/resources')
        return
      }
      try {
        const res = await queryResourceById(id)
        if (res.data.code == 200) {
          this.ruleForm = { ...res.data.data }
        } else {
          this.$message.error('获取档案详情失败')
          this.$router.push('/resources')
        }
      } catch (error) {
        this.$message.error('获取档案详情失败')
        this.$router.push('/resources')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.detail-container {
  margin: 30px;
  margin-top: 30px;
  .container {
    position: relative;
    z-index: 1;
    background: #fff;
    padding: 30px;
    border-radius: 4px;
    .subBox {
      padding-top: 30px;
      text-align: center;
      border-top: solid 1px #e5e5e5;
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
