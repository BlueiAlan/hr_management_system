<template>
  <div class="addBrand-container">
    <div class="container">
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="180px">
        <el-form-item label="账号" prop="username">
          <el-input v-model="ruleForm.username"></el-input>
        </el-form-item>
        <el-form-item label="员工姓名" prop="name">
          <el-input v-model="ruleForm.name"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="ruleForm.phone"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="sex">
            <el-radio v-model="ruleForm.sex" label="1">男</el-radio>
            <el-radio v-model="ruleForm.sex" label="2">女</el-radio>
        </el-form-item>
        <el-form-item label="身份证号" prop="idNumber">
          <el-input v-model="ruleForm.idNumber"></el-input>
        </el-form-item>
        <div class="subBox">
          <el-button type="primary" @click="submitForm('ruleForm',false)">保存</el-button>
          <el-button 
            v-if="this.optType === 'add'" 
            type="primary" 
            @click="submitForm('ruleForm',true)">保存并继续添加员工
          </el-button>
          <el-button @click="() => this.$router.push('/employee')">返回</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts">

import {addEmployee,queryEmpById,updateEmployee} from '@/api/employee'
export default {
  data() {
    return {
      optType: '', // add 添加 edit 编辑

      // 表单数据
      ruleForm: {
        username: '',
        name: '',
        sex: '1',
        phone: '',
        idNumber: ''
      },
      // 表单验证规则
      rules: {
        name: [
          { required: true, message: '请输入员工姓名', trigger: 'blur' },
          {min: 2, max: 8, message: '长度在 2 到 8 个字符', trigger: 'blur' }
        ],
        username: [
          { required: true, message: '请输入员工账号', trigger: 'blur' },
        ],
        phone: [
          {required: true, message: '请输入手机号', trigger: 'blur' },
          {validator: (rule, value, callback) => {
            if (value === '' || (!(/^1[3-9]\d{9}$/.test(value)))){
              callback(new Error('请输入正确的手机号'))
            } else {
              // 结束校验
              callback()
            }
          }, trigger: 'blur' }
        ],
        idNumber: [
          {required: true, message: '请输入身份证号', trigger: 'blur' },
          {validator: (rule, value, callback) => {
            if (value === '' || (!(/ (^\d{15}$)|(^\d{18}$)|(^\d{17}(X|x)$) /).test(value))){
              callback(new Error('请输入正确的身份证号'))
            } else {
              // 结束校验
              callback()
            }
          }, trigger: 'blur' }
        ]
      }
    }
  },

  created() {
    // 获取路由参数 id 判断是添加还是编辑
    this.optType = this.$route.query.id ? 'update': 'add'
    if(this.optType === 'update') {
      // 编辑，查询员工信息
      queryEmpById(this.$route.query.id).then((res => {
        if(res.data.code === 1) {
          this.ruleForm = res.data.data
        }else {
          this.$message.error('员工信息修改失败')
        }
      })).catch(err => {
        this.$message.error('请求失败' + err.message)
      })
    }
  },

  methods: {
    // 提交表单
    submitForm(formName: string, isContinue: boolean) {
      // 进行表单校验
      this.$refs[formName].validate((valid => {
        if (valid){
          // 通过，发起ajax请求
          if(this.optType === 'add') {
            addEmployee(this.ruleForm).then((res => {
              if(res.data.code === 1) {
                this.$message.success('员工添加成功')
                if(isContinue) {
                  this.ruleForm = {
                    username: '',
                    name: '',
                    sex: '1',
                    phone: '',
                    idNumber: ''
                  }
                }else {
                  // 路由跳转页面
                  this.$router.push('/employee')
                }
              }else {
                this.$message.error('员工添加失败')
              }
            }))
          }else {
            // 编辑员工
            updateEmployee(this.ruleForm).then((res => {
              if(res.data.code === 1) {
                this.$message.success('员工信息修改成功')
                // 路由跳转页面
                this.$router.push('/employee')
              }else {
                this.$message.error('员工信息修改失败')
              }
            }))
          }

        }
      }))
    }  
  }
}
</script>


<style lang="scss" scoped>
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
</style>
