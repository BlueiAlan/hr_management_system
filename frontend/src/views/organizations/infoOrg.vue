<script lang="ts">
import {Vue, Component} from 'vue-property-decorator';
import {getOrgList, queryById, addOrg, updateOrg} from "@/api/organizations";

  export default{
    data(){
      return{
        pageTitle: this.$route.params.orgLevel == 2 ? '一级机构' : '二级机构',
        ruleForm:{
          orgLevel: this.$route.params.orgLevel,
          orgName: '',
          parentId: null,
          id: null,
        },
        orgList: [],
        optType: '',
      }
    },
    created() {
      console.log(this.optType);
      console.log(this.$route.query.id);
      this.optType = this.$route.params.id ? 'edit' : 'add';
      if (this.optType === 'edit') {
        const id = this.$route.params.id;
        queryById(id).then((response) => {
          if (response.data.code == 200) {
            this.ruleForm.orgName = response.data.data.orgName;
            this.ruleForm.parentId = response.data.data.parentId;
            this.ruleForm.id = response.data.data.id;
            this.ruleForm.orgLevel = response.data.data.orgLevel;
            queryById(id).then((response) => {
              if (response.data.code == 200) {
                this.ruleForm.parentId = response.data.data.parentId;
              } else {
                this.$message.error('获取机构详情失败')
              }
            })
          } else {
            this.$message.error('获取机构详情失败')
          }
        })
      }
      this.queryOrgList();
    },
    methods: {
      async queryOrgList() {
        const params = {
          orgLevel: this.$route.params.orgLevel - 1,
        }

        try {
          // 使用await等待异步请求结果，确保在获取到数据后再继续执行后续代码
          const response = await getOrgList(params);
          this.orgList = response.data.data;
          console.log('异步请求结果成功');
        } catch (error) {
          // 可以在这里添加错误处理，比如显示提示信息
          this.$message.error('获取机构列表失败，请稍后重试');
        }
      },
      submitForm(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            const params = {
              id: null,
              orgLevel: this.$route.params.orgLevel,
              orgName: this.ruleForm.orgName,
              parentId: this.ruleForm.parentId,
            }
            if (this.optType === 'edit') {
              params.id = this.$route.query.id
              updateOrg(params).then((response) => {
                if (response.data.code == 200) {
                  this.$message.success('更新机构成功');
                  this.$router.push({ path: `/organizations/${this.ruleForm.orgLevel}` });
                } else {
                  this.$message.error('更新机构失败');
                }
              })
            }else {
              addOrg(params).then((response) => {
                if (response.data.code == 200) {
                  this.$message.success('创建机构成功');
                  this.$router.push({path: `/organizations/${this.ruleForm.orgLevel}`});
                } else {
                  this.$message.error('创建机构失败');
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
  <div class="info-container">
    <div class="container">
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
        <el-form-item
          v-if="this.ruleForm.orgLevel == 2 || this.ruleForm.orgLevel == 3 "
          prop="orgId">
          <el-select v-model="ruleForm.parentId" :placeholder="`请选择${pageTitle}`">
            <el-option
              v-for="org in orgList"
              :key="org.id"
              :label="org.orgName"
              :value="org.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="orgName">
          <el-input v-model="ruleForm.orgName" placeholder="请输入机构名称"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button
            v-if="this.optType == 'add'"
            class="button1" type="primary" @click="submitForm('ruleForm')">立即创建</el-button>
          <el-button
            v-if="this.optType == 'edit'"
            class="button1" type="primary" @click="submitForm('ruleForm')">保存修改</el-button>
          <el-button class="button2" type="danger" plain @click="resetForm('ruleForm')">重置</el-button>
          <el-button  @click="() => this.$router.push({ path: `/organizations/${this.ruleForm.orgLevel}` })">返回</el-button>
        </el-form-item>
      </el-form>

    </div>

  </div>
</template>

<style scoped lang="scss">
.info {
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
