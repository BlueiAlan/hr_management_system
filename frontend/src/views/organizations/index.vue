<script lang="ts">
import { getPage } from '@/api/organizations'

  export default {
    data() {
      return {
        orgLevel: this.$route.params.orgLevel,
        orgName: '',
        parentId: null,
        pageNum: 1,
        pageSize: 10,
        total: 0, // 总记录数
        totalPage: 0, // 总页数
        records: [],
        searchTimer: null, // 防抖定时器
      }
    },
    created() {
      // if (this.$route.query.orgLevel === '1') {
      //   this.orgLevel = 1;
      // } else if (this.$route.query.orgLevel === '2') {
      //   this.orgLevel = 2;
      // } else if (this.$route.query.orgLevel === '3') {
      //   this.orgLevel = 3;
      // }
      this.queryPage();
    },
    methods: {
      queryPage(){
        // 构建查询参数
        const params = {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          orgLevel: this.orgLevel,
          orgName: this.orgName,
        }
        console.log(params)
        getPage(params).then(res => {
          if (res.data.code == 200) {
            this.records = res.data.data.records;
            this.total = res.data.data.total;
            this.totalPage = res.data.data.totalPage;
          }
        }).catch(err => {
          this.$message.error("请求异常，请联系管理员")
        })
      },
      // 处理输入事件，添加防抖
      handleInput() {
        // 清除之前的定时器
        if (this.searchTimer) {
          clearTimeout(this.searchTimer);
        }
        // 设置新的定时器，延迟300毫秒执行查询
        this.searchTimer = setTimeout(() => {
          this.pageNum = 1; // 重置为第一页
          this.queryPage();
        }, 300);
      },
      // 处理分页大小变化
      handleSizeChange(val) {
        this.pageSize = val;
        this.queryPage();
      },
      // 处理当前页变化
      handleCurrentChange(val) {
        this.pageNum = val;
        this.queryPage();
      },
      // 编辑操作
      handleEdit(index, row) {
        this.$router.push({
          name: 'organizationInfo',
          params: {
            id: row.id,
            orgLevel: this.orgLevel,
          }
        })
      },
      handleAddOrg() {
        this.$router.push({
          name: 'organizationInfo',
          params: {
            id: null,
            orgLevel: this.orgLevel,
          }
        });
      }
    },
    // 组件销毁时清除定时器
    beforeDestroy() {
      if (this.searchTimer) {
        clearTimeout(this.searchTimer);
      }
    },
    computed: {
      pageTitle() {
        let title = null;
        switch (this.$route.params.orgLevel) {
          case '1':
            title = '一级机构';
            break;
          case '2':
            title = '二级机构';
            break;
          case '3':
            title = '三级机构';
            break;
        }
        return title;
      }
    }
  }
</script>

<template>
  <div class="org-container">
    <div class="container">
      <div class="header-section">
        <h2>{{pageTitle}}</h2>
      </div>
      <div style="margin-top: 29px;">
        <el-input
          placeholder="请输入机构名称"
          v-model="orgName"
          clearable
          @input="handleInput">
        </el-input>
        <el-button type="primary" style="float: right; color:#FFFFFF;" class="button1" @click="$router.push('/position')">返回</el-button>
        <div style="margin-top: 29px; width: 100%;">
          <el-button type="primary" style=" color:#FFFFFF; width: 200px" class="button1" @click="handleAddOrg()">+添加{{pageTitle}}</el-button>
        </div>
      </div>
      <!-- 表格容器，用于居中显示 -->
      <div class="table-container">
        <el-table
          :data="records"
          style="width: 80%"
          :cell-style="{textAlign: 'center'}"
          :header-cell-style="{textAlign: 'center'}">
          <el-table-column
            prop="orgName"
            label="机构名称"
          >
          </el-table-column>
          <el-table-column
            v-if="orgLevel == '2'"
            prop="parentOrgName"
            label="所属机构"
          >
          </el-table-column>
          <el-table-column
            v-if="orgLevel == '3'"
            prop="fromOrg"
            label="所属机构"
          >
          </el-table-column>
          <el-table-column label="操作">
            <template v-slot="scope">
              <el-button
                size="primary" plain
                class="action-button"
                @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
<!--              <el-button-->
<!--                size="primary" plain-->
<!--                type="danger"-->
<!--                class="action-button"-->
<!--                @click="handleDelete(scope.$index, scope.row)">删除</el-button>-->
            </template>
          </el-table-column>
        </el-table>
      </div>
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[10, 20, 30, 40, 50, 100]"
        :page-size="10"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        style="margin-top: 20px; text-align: center">
      </el-pagination>


    </div>


  </div>

</template>

<style scoped lang="scss">
.org{
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

/* 表格容器样式 - 实现表格在页面中居中 */
.table-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 表格单元格垂直居中 */
::v-deep .el-table td {
  vertical-align: middle !important;
}

/* 表头垂直居中 */
::v-deep .el-table th {
  vertical-align: middle !important;
}

/* 表头样式 - 添加灰色背景 */
::v-deep .el-table__header-wrapper {
  th {
    background-color: rgba(96, 98, 102, 0.24) !important;
    color: #606266 !important;
    font-weight: bold !important;
  }
}

/* 操作按钮样式 - 恢复到原始plain按钮样式 */
.action-button {
  // 确保按钮保持plain样式
  background-color: transparent !important;
  color: #409EFF !important;
  border-color: #409EFF !important;

  // 悬停时的高亮效果
  &:hover {
    background-color: #ecf5ff !important;
    border-color: #409EFF !important;
    color: #409EFF !important;
  }

  // 点击后恢复到原始plain样式
  &:active, &:focus {
    background-color: transparent !important;
    color: #409EFF !important;
    border-color: #409EFF !important;
    outline: none !important;
    box-shadow: none !important;
  }
}

.button1{
  background-color: #409EFF !important;
  border-color: #409EFF !important;
}
.button1:hover {
  background-color: #66b1ff !important;
  border-color: #66b1ff !important;
}
</style>
