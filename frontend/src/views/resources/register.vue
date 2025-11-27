<template>
  <div class="register-container">
    <div class="container">
      <h2 style="margin-bottom: 20px;">人力资源档案登记</h2>
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="120px">
        <!-- 个人信息部分 -->
        <div class="form-section">
          <h3 class="section-title">个人信息</h3>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="编号" prop="employeeNumber">
                <el-input v-model="ruleForm.employeeNumber" placeholder="系统自动生成" disabled style="width: 100%;"></el-input>
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
              <el-form-item label="身份证号码" prop="idCard">
                <el-input v-model="ruleForm.idCard" placeholder="输入身份证号码" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 机构与职位信息部分 -->
        <div class="form-section">
          <h3 class="section-title">机构与职位信息</h3>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="一级机构" prop="org1Id">
                <el-select v-model="ruleForm.org1Id" placeholder="选择一级机构" clearable style="width: 100%;" @change="handleOrg1Change">
                  <el-option
                    v-for="item in org1List"
                    :key="item.id"
                    :label="item.orgName"
                    :value="item.id">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="二级机构" prop="org2Id">
                <el-select v-model="ruleForm.org2Id" placeholder="选择二级机构" clearable style="width: 100%;" @change="handleOrg2Change">
                  <el-option
                    v-for="item in org2List"
                    :key="item.id"
                    :label="item.orgName"
                    :value="item.id">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="三级机构" prop="orgId">
                <el-select v-model="ruleForm.orgId" placeholder="选择三级机构" clearable style="width: 100%;" @change="handleOrg3Change">
                  <el-option
                    v-for="item in org3List"
                    :key="item.id"
                    :label="item.orgName"
                    :value="item.id">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="职位名称" prop="positionId">
                <el-select v-model="ruleForm.positionId" placeholder="选择职位名称" clearable style="width: 100%;" @change="loadSalaryStandards">
                  <el-option
                    v-for="item in positionList"
                    :key="item.id"
                    :label="item.positionName"
                    :value="item.id">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="职称" prop="title">
                <el-select v-model="ruleForm.title" placeholder="选择职称" clearable style="width: 100%;" @change="loadSalaryStandards">
                  <el-option label="初级" value="初级"></el-option>
                  <el-option label="中级" value="中级"></el-option>
                  <el-option label="高级" value="高级"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
<!--            <el-col :span="8">
              <el-form-item label="薪酬标准" prop="salaryStandardId">
                <el-select v-model="ruleForm.salaryStandardId" placeholder="选择薪酬标准" clearable style="width: 100%;">
                  <el-option
                    v-for="item in salaryStandardList"
                    :key="item.id"
                    :label="item.standardName"
                    :value="item.id">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>-->

          </el-row>
        </div>

        <!-- 更多个人信息部分 -->
        <div class="form-section">
          <h3 class="section-title">个人信息</h3>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="出生日期" prop="birthDate">
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
              <el-form-item label="民族" prop="ethnicity">
                <el-select v-model="ruleForm.ethnicity" placeholder="选择民族" clearable style="width: 100%;">
                  <el-option label="汉族" value="汉族"></el-option>
                  <el-option label="回族" value="回族"></el-option>
                  <el-option label="满族" value="满族"></el-option>
                  <el-option label="其他" value="其他"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="学历" prop="education">
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
              <el-form-item label="Email" prop="email">
                <el-input v-model="ruleForm.email" placeholder="输入Email地址" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="电话" prop="phone">
                <el-input v-model="ruleForm.phone" placeholder="输入联系电话" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="手机" prop="mobile">
                <el-input v-model="ruleForm.mobile" placeholder="输入手机号码" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="住址" prop="address">
                <el-input v-model="ruleForm.address" placeholder="输入联系地址" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="邮编" prop="postalCode">
                <el-input v-model="ruleForm.postalCode" placeholder="输入邮政编码" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="国籍" prop="nationality">
                <el-input v-model="ruleForm.nationality" placeholder="输入国籍" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="出生地" prop="birthPlace">
                <el-input v-model="ruleForm.birthPlace" placeholder="输入出生地" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="宗教信仰" prop="religion">
                <el-input v-model="ruleForm.religion" placeholder="输入宗教信仰" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="政治面貌" prop="politicalStatus">
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
<!--            <el-col :span="12">
              <el-form-item label="学历专业" prop="major">
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
            </el-col>-->
            <el-col :span="12">
              <el-form-item label="年龄" prop="age">
                <el-input-number v-model="ruleForm.age" :min="0" :max="150" style="width: 80%;"></el-input-number>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="QQ" prop="qq">
                <el-input v-model="ruleForm.qq" placeholder="输入QQ号码" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 其他信息部分 -->
        <div class="form-section">
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="爱好" prop="hobbies">
                <el-input v-model="ruleForm.hobbies" type="textarea" :rows="3" placeholder="输入爱好" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="个人履历" prop="resume">
                <el-input v-model="ruleForm.resume" type="textarea" :rows="5" placeholder="输入个人履历" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="家庭关系信息" prop="familyInfo">
                <el-input v-model="ruleForm.familyInfo" type="textarea" :rows="5" placeholder="输入家庭关系信息" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="备注" prop="remarks">
                <el-input v-model="ruleForm.remarks" type="textarea" :rows="3" placeholder="输入备注" style="width: 100%;"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="照片" prop="photoPath">
                <ImgUpload :propImageUrl="ruleForm.photoPath" @imageChange="handleImageChange" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="subBox">
          <el-button type="primary" @click="submitForm('ruleForm')" class="button1">提交</el-button>
          <el-button @click="() => this.$router.push('/resources')" class="action-button">返回</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts">
import { registerResource } from '@/api/resource'
import { getOrgList } from '@/api/organizations'
import { getPositionList } from '@/api/positions'
import { getSalaryStandardList } from '@/api/salaryStandards'
import ImgUpload from '@/components/ImgUpload/index.vue'

export default {
  components: {
    ImgUpload
  },
  data() {
    return {
      org1List: [],
      org2List: [],
      org3List: [],
      positionList: [],
      salaryStandardList: [],
      ruleForm: {
        employeeNumber: '系统自动生成',
        org1Id: null,
        org2Id: null,
        orgId: null,
        positionId: null,
        title: null,
        salaryStandardId: null,
        username: '',
        gender: '',
        email: '',
        phone: '',
        qq: '',
        mobile: '',
        address: '',
        postalCode: '',
        nationality: '中国',
        birthPlace: '',
        birthDate: null,
        ethnicity: '',
        religion: '',
        politicalStatus: '',
        idCard: '',
        age: null,
        education: '',
        major: '',
        hobbies: '',
        resume: '',
        familyInfo: '',
        remarks: '',
        photoPath: ''
      },
      rules: {
        orgId: [{ required: true, message: '请选择三级机构', trigger: 'change' }],
        positionId: [{ required: true, message: '请选择职位', trigger: 'change' }],
        title: [{ required: true, message: '请选择职称', trigger: 'change' }],
        username: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        gender: [{ required: true, message: '请选择性别', trigger: 'change' }]
      }
    }
  },

  created() {
    this.loadOrg1List()
    this.loadPositionList()
  },

  methods: {
    async loadOrg1List() {
      try {
        const res = await getOrgList({ orgLevel: 1 })
        if (res.data.code == 200) {
          this.org1List = res.data.data
        }
      } catch (error) {
        this.$message.error('获取一级机构列表失败')
      }
    },

    async handleOrg1Change() {
      this.ruleForm.org2Id = null
      this.ruleForm.orgId = null
      this.org2List = []
      this.org3List = []
      if (this.ruleForm.org1Id) {
        try {
          const res = await getOrgList({ orgLevel: 2 })
          if (res.data.code == 200) {
            const allOrg2 = res.data.data
            this.org2List = allOrg2.filter((org: any) => org.parentId === this.ruleForm.org1Id)
          }
        } catch (error) {
          this.$message.error('获取二级机构列表失败')
        }
      }
    },

    async handleOrg2Change() {
      this.ruleForm.orgId = null
      this.org3List = []
      if (this.ruleForm.org2Id) {
        try {
          const res = await getOrgList({ orgLevel: 3 })
          if (res.data.code == 200) {
            const allOrg3 = res.data.data
            this.org3List = allOrg3.filter((org: any) => org.parentId === this.ruleForm.org2Id)
          }
        } catch (error) {
          this.$message.error('获取三级机构列表失败')
        }
      }
    },

    handleOrg3Change() {
      this.loadSalaryStandards()
    },

    async loadPositionList() {
      try {
        const res = await getPositionList({ pageNum: 1, pageSize: 1000 })
        if (res.data.code == 200) {
          this.positionList = res.data.data.records
        }
      } catch (error) {
        this.$message.error('获取职位列表失败')
      }
    },

    async loadSalaryStandards() {
      if (!this.ruleForm.positionId || !this.ruleForm.title) {
        this.salaryStandardList = []
        return
      }
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
          if (!this.ruleForm.orgId) {
            this.$message.error('请选择三级机构')
            return
          }
          const params = { ...this.ruleForm }
          // 删除不需要提交的字段
          delete params.org1Id
          delete params.org2Id
          delete params.employeeNumber // 不发送编号，由后端自动生成
          // 如果idCard为空，则不发送该字段，避免唯一性约束问题
          if (!params.idCard || params.idCard.trim() === '') {
            delete params.idCard
          }
          registerResource(params).then((res: any) => {
            if (res.data.code == 200) {
              this.$message.success('人力资源档案登记成功')
              this.$router.push('/resources')
            } else {
              this.$message.error(res.data.msg || '登记失败')
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
.register-container {
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

