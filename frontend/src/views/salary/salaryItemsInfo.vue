<script lang="ts">
import { addSalaryItem, queryById, updateSalaryItem, deleteSalaryItem } from '@/api/salaryItems'

export default {
  data() {
    return {
      ruleForm: {
        itemName: '',
        itemType: '',
        calculationRule: '',
        isActive: 1
      },
      rules: {
        itemName: [
          { required: true, message: '请输入薪酬名称', trigger: 'blur' },
          // { min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur' }
        ],
        itemType: [
          { required: true, message: '请选择薪酬类型', trigger: 'change', validator: (rule,value,callback) => {
              if (value === '收入' || value === '扣除') {
                callback()
              } else {
                callback(new Error('请选择薪酬类型'))
              }
            } },
        ],
        calculationRule: [
          { required: false, message: '请填写计算规则'}
        ]
      },
      // 添加一个对象来存储从服务器获取的机构列表
      orgList: [],
      optType: '',
    };
  },

  created() {
    this.optType = this.$route.query.id ? 'edit' : 'add';
    if (this.optType === 'edit') {
      const id = this.$route.query.id
      queryById(id).then((response) => {
        if (response.data.code == 200) {
          this.ruleForm.itemName = response.data.data.itemName;
          this.ruleForm.itemType = response.data.data.itemType;
          this.ruleForm.calculationRule = response.data.data.calculationRule;
          this.ruleForm.isActive = response.data.data.isActive;
        } else {
          this.$message.error('获取薪酬项详情失败')
        }
      })
    }
  },

  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          const params = {
            id: null,
            itemName: this.ruleForm.itemName,
            itemType: this.ruleForm.itemType,
            calculationRule: this.ruleForm.calculationRule,
            isActive: this.ruleForm.isActive,
          }
          if (this.optType === 'edit') {
            params.id = this.$route.query.id
            updateSalaryItem(params).then((response) => {
              if (response.data.code == 200) {
                this.$message.success('更新薪酬项成功');
                this.$router.push({ path: '/salaryItems' });
              } else {
                this.$message.error('更新薪酬项失败');
              }
            })
          }else {
            addSalaryItem(params).then((response) => {
              if (response.data.code == 200) {
                this.$message.success('创建薪酬项成功');
                this.$router.push({path: '/salaryItems'});
              } else {
                this.$message.error('创建薪酬项失败');
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
        <el-form-item label="薪资名称" prop="itemName">
          <el-input v-model="ruleForm.itemName"></el-input>
        </el-form-item>
        <el-form-item label="薪酬类型" prop="itemType">
          <el-select v-model="ruleForm.itemType" placeholder="请选择薪酬类型">
            <el-option label="收入" value="收入"></el-option>
            <el-option label="扣除" value="扣除"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="计算规则" prop="calculationRule" placeholder="按照示例填写计算规则">
          <el-input type="textarea" v-model="ruleForm.calculationRule" style="width: 293px;"></el-input>
          <div class="example-text">示例：基本工资 * 2% + 3</div>
        </el-form-item>
        <el-form-item>
          <el-button
            v-if="this.optType == 'add'"
            class="button1" type="primary" @click="submitForm('ruleForm')">立即创建</el-button>
          <el-button
            v-if="this.optType == 'edit'"
            class="button1" type="primary" @click="submitForm('ruleForm')">保存修改</el-button>
          <el-button class="button2" type="danger" plain @click="resetForm('ruleForm')">重置</el-button>
          <el-button  @click="() => this.$router.push({ path: '/salaryItems' })">返回</el-button>
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
.example-text {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}




</style>
