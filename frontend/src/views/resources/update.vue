<template>
  <div class="update-container">
    <div class="container">
      <h2 style="margin-bottom: 20px;">人力资源档案变更</h2>
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="120px" v-if="ruleForm.id">
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
              <el-form-item label="姓名" prop="username">
                <el-input v-model="ruleForm.username" placeholder="输入员工姓名" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="性别" prop="gender">
                <el-select v-model="ruleForm.gender" placeholder="选择性别" style="width: 100%;">
                  <el-option label="男" value="男"></el-option>
                  <el-option label="女" value="女"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="身份证号码">
                <el-input v-model="ruleForm.idCard" placeholder="输入身份证号码" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 机构与职位信息部分 -->
        <div class="form-section">
          <h3 class="section-title">机构与职位信息</h3>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="所属机构">
                <el-input v-model="ruleForm.orgName" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="职位">
                <el-input v-model="ruleForm.positionName" disabled style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="职称">
                <el-select v-model="ruleForm.title" placeholder="选择职称" clearable style="width: 100%;" @change="loadSalaryStandards">
                  <el-option label="初级" value="初级"></el-option>
                  <el-option label="中级" value="中级"></el-option>
                  <el-option label="高级" value="高级"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="薪酬标准">
                <el-select v-model="ruleForm.salaryStandardId" placeholder="选择薪酬标准" clearable style="width: 100%;">
                  <el-option
                    v-for="item in salaryStandardList"
                    :key="item.id"
                    :label="item.standardName"
                    :value="item.id">
                  </el-option>
                </el-select>
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
                <el-date-picker
                  v-model="ruleForm.birthDate"
                  type="date"
                  placeholder="年/月/日"
                  value-format="yyyy-MM-dd"
                  @change="calculateAge"
                  style="width: 100%;">
                </el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="民族">
                <el-select v-model="ruleForm.ethnicity" placeholder="选择民族" clearable style="width: 100%;">
                  <el-option label="汉族" value="汉族"></el-option>
                  <el-option label="回族" value="回族"></el-option>
                  <el-option label="满族" value="满族"></el-option>
                  <el-option label="其他" value="其他"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="学历">
                <el-select v-model="ruleForm.education" placeholder="选择学历" clearable style="width: 100%;">
                  <el-option label="小学" value="小学"></el-option>
                  <el-option label="初中" value="初中"></el-option>
                  <el-option label="高中" value="高中"></el-option>
                  <el-option label="大专" value="大专"></el-option>
                  <el-option label="本科" value="本科"></el-option>
                  <el-option label="硕士" value="硕士"></el-option>
                  <el-option label="博士" value="博士"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="Email">
                <el-input v-model="ruleForm.email" placeholder="输入Email地址" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="电话">
                <el-input v-model="ruleForm.phone" placeholder="输入联系电话" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="手机">
                <el-input v-model="ruleForm.mobile" placeholder="输入手机号码" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="住址">
                <el-input v-model="ruleForm.address" placeholder="输入联系地址" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="邮编">
                <el-input v-model="ruleForm.postalCode" placeholder="输入邮政编码" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="国籍">
                <el-input v-model="ruleForm.nationality" placeholder="输入国籍" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="出生地">
                <el-input v-model="ruleForm.birthPlace" placeholder="输入出生地" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="宗教信仰">
                <el-input v-model="ruleForm.religion" placeholder="输入宗教信仰" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="政治面貌">
                <el-select v-model="ruleForm.politicalStatus" placeholder="选择政治面貌" clearable style="width: 100%;">
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
                <el-select v-model="ruleForm.major" placeholder="选择学历专业" clearable style="width: 100%;">
                  <el-option label="计算机科学与技术" value="计算机科学与技术"></el-option>
                  <el-option label="软件工程" value="软件工程"></el-option>
                  <el-option label="信息管理与信息系统" value="信息管理与信息系统"></el-option>
                  <el-option label="工商管理" value="工商管理"></el-option>
                  <el-option label="人力资源管理" value="人力资源管理"></el-option>
                  <el-option label="会计学" value="会计学"></el-option>
                  <el-option label="金融学" value="金融学"></el-option>
                  <el-option label="市场营销" value="市场营销"></el-option>
                  <el-option label="英语" value="英语"></el-option>
                  <el-option label="汉语言文学" value="汉语言文学"></el-option>
                  <el-option label="其他" value="其他"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="年龄">
                <el-input-number v-model="ruleForm.age" :min="0" :max="150" style="width: 100%;"></el-input-number>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="QQ">
                <el-input v-model="ruleForm.qq" placeholder="输入QQ号码" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 其他信息部分 -->
        <div class="form-section">
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="爱好">
                <el-input v-model="ruleForm.hobbies" type="textarea" :rows="3" placeholder="输入爱好" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="个人履历">
                <el-input v-model="ruleForm.resume" type="textarea" :rows="5" placeholder="输入个人履历" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="家庭关系信息">
                <el-input v-model="ruleForm.familyInfo" type="textarea" :rows="5" placeholder="输入家庭关系信息" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="备注">
                <el-input v-model="ruleForm.remarks" type="textarea" :rows="3" placeholder="输入备注" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="照片">
                <ImgUpload :propImageUrl="ruleForm.photoPath" @imageChange="handleImageChange" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="subBox">
          <el-button type="primary" @click="submitForm('ruleForm')" class="button1">提交变更</el-button>
          <el-button @click="() => this.$router.push('/resources')" class="action-button">返回</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts">
import { queryResourceById, updateResource } from '@/api/resource'
import { getSalaryStandardList } from '@/api/salaryStandards'
import ImgUpload from '@/components/ImgUpload/index.vue'

export default {
  components: {
    ImgUpload
  },
  data() {
    return {
      salaryStandardList: [],
      ruleForm: {
        id: null,
        archiveNumber: '',
        orgName: '',
        positionName: '',
        username: '',
        gender: '',
        email: '',
        phone: '',
        qq: '',
        mobile: '',
        address: '',
        postalCode: '',
        nationality: '',
        birthPlace: '',
        birthDate: null,
        ethnicity: '',
        religion: '',
        politicalStatus: '',
        idCard: '',
        age: null,
        education: '',
        major: '',
        title: '',
        salaryStandardId: null,
        hobbies: '',
        resume: '',
        familyInfo: '',
        remarks: '',
        photoPath: ''
      },
      rules: {
        username: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
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
          this.ruleForm = { ...res.data.data }
          // 如果国籍为空，设置默认值
          if (!this.ruleForm.nationality) {
            this.ruleForm.nationality = '中国'
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

    handleImageChange(imageUrl: string) {
      this.ruleForm.photoPath = imageUrl
    },

    calculateAge() {
      if (this.ruleForm.birthDate) {
        const birthDate = new Date(this.ruleForm.birthDate)
        const today = new Date()
        let age = today.getFullYear() - birthDate.getFullYear()
        const monthDiff = today.getMonth() - birthDate.getMonth()
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
          age--
        }
        this.ruleForm.age = age
      }
    },

    submitForm(formName: string) {
      this.$refs[formName].validate((valid: boolean) => {
        if (valid) {
          const params = { ...this.ruleForm }
          delete params.orgName
          delete params.positionName
          updateResource(params).then((res: any) => {
            if (res.data.code == 200) {
              this.$message.success('人力资源档案变更成功，等待复核')
              this.$router.push('/resources')
            } else {
              this.$message.error(res.data.msg || '变更失败')
            }
          }).catch((err) => {
            this.$message.error('请求失败：' + (err.response && err.response.data && err.response.data.msg ? err.response.data.msg : err.message))
          })
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.update-container {
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

.button1 {
  color: #FFFFFF;
  background-color: #409EFF !important;
  border-color: #409EFF !important;
}

.button1:hover {
  color: #FFFFFF;
  background-color: #66b1ff !important;
  border-color: #66b1ff !important;
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
