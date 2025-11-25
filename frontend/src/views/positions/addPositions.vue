<script lang="ts">
import { getOrgList } from '@/api/organizations'
import { addPosition, queryById, updatePosition } from '@/api/positions'

export default {
  data() {
    return {
      ruleForm: {
        positionsName: '',
        orgId: null,
        desc: ''
      },
      rules: {
        positionsName: [
          { required: true, message: '请输入职位名称', trigger: 'blur' },
          // { min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur' }
        ],
        orgId: [
          { required: true, message: '请选择所属机构', trigger: 'change', validator: (rule,value,callback) => {
            if (value === null) {
              callback(new Error('请选择所属机构'))
            } else {
              callback()
            }
          } },
        ],
        desc: [
          { required: false, message: '请填写职位描述'}
        ]
      },
      // 添加一个对象来存储从服务器获取的机构列表
      orgList: [],
      optType: '',
    };
  },

  created() {
    this.queryOrgList();
    this.optType = this.$route.query.id ? 'edit' : 'add';
    if (this.optType === 'edit') {
      const id = this.$route.query.id
      queryById(id).then((response) => {
        if (response.data.code == 200) {
          this.ruleForm.positionsName = response.data.data.positionName;
          this.ruleForm.orgId = response.data.data.orgId;
          this.ruleForm.desc = response.data.data.description;
        } else {
          this.$message.error('获取职位详情失败')
        }
      })
    }
  },

  methods: {
    async queryOrgList() {
      const params = {
        orgLevel: 3,
      }

      try {
        // 使用await等待异步请求结果，确保在获取到数据后再继续执行后续代码
        const response = await getOrgList(params);
        this.orgList = response.data.data;
      } catch (error) {
        console.error('获取机构列表失败:', error);
        // 可以在这里添加错误处理，比如显示提示信息
        this.$message.error('获取机构列表失败，请稍后重试');
      }
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          const params = {
            id: null,
            positionName: this.ruleForm.positionsName,
            orgId: this.ruleForm.orgId,
            description: this.ruleForm.desc,
          }
          if (this.optType === 'edit') {
            params.id = this.$route.query.id
            updatePosition(params).then((response) => {
            if (response.data.code == 200) {
              this.$message.success('更新职位成功');
              this.$router.push({ path: '/position' });
            } else {
              this.$message.error('更新职位失败');
            }
          })
          }else {
            addPosition(params).then((response) => {
              if (response.data.code == 200) {
                this.$message.success('创建职位成功');
                this.$router.push({path: '/position'});
              } else {
                this.$message.error('创建职位失败');
              }
            })
          }} else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    }
  }
}
</script>

<template>
  <div class="addBrand-container">
    <div class="container">
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
        <el-form-item label="职位名称" prop="positionsName">
          <el-input v-model="ruleForm.positionsName"></el-input>
        </el-form-item>
        <el-form-item label="所属机构" prop="orgId">
          <el-select v-model="ruleForm.orgId" placeholder="请选择所属机构">
            <el-option
              v-for="org in orgList"
              :key="org.id"
              :label="org.orgName"
              :value="org.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="职位描述" prop="desc">
          <el-input type="textarea" v-model="ruleForm.desc" style="width: 293px;"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button
            v-if="this.optType == 'add'"
            class="button1" type="primary" @click="submitForm('ruleForm')">立即创建</el-button>
          <el-button
            v-if="this.optType == 'edit'"
            class="button1" type="primary" @click="submitForm('ruleForm')">保存修改</el-button>
          <el-button class="button2" type="danger" plain @click="resetForm('ruleForm')">重置</el-button>
          <el-button  @click="() => this.$router.push({ path: '/position' })">返回</el-button>
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
    .HeadLable {
      background-color: transparent;
      margin-bottom: 0px;
      padding-left: 0px;
    }
    .container {
      position: relative;
      z-index: 1;
      background: #fff;
      padding: 30px;
      border-radius: 4px;
      // min-height: 500px;
      .subBox {
        padding-top: 30px;
        text-align: center;
        border-top: solid 1px $gray-5;
      }
    }
    .idNumber {
      margin-bottom: 39px;
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
  // 确保按钮保持plain样式
  background-color: transparent !important;
  color: #F56C6C !important;
  border-color: #F56C6C !important;
}

.button2:hover {
  background-color: #fef0f0 !important;
  border-color: #F56C6C !important;
  color: #F56C6C !important;
}
.button2:active, .button2:hover {
  background-color: transparent !important;
  color: #F56C6C !important;
  border-color: #F56C6C !important;
  outline: none !important;
  box-shadow: none !important;
}


</style>
