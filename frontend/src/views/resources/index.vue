<template>
  <div class="dashboard-container">
    <div class="container">
      <!-- 查询条件 -->
      <div class="tableBer">
        <div style="margin-bottom: 20px;">
          <label style="margin-right: 5px">一级机构：</label>
          <el-select v-model="org1Id" placeholder="请选择一级机构" clearable style="width: 150px;" @change="handleOrg1Change">
            <el-option
              v-for="item in org1List"
              :key="item.id"
              :label="item.orgName"
              :value="item.id">
            </el-option>
          </el-select>

          <label style="margin-left: 20px; margin-right: 5px">二级机构：</label>
          <el-select v-model="org2Id" placeholder="请选择二级机构" clearable style="width: 150px;" @change="handleOrg2Change">
            <el-option
              v-for="item in org2List"
              :key="item.id"
              :label="item.orgName"
              :value="item.id">
            </el-option>
          </el-select>

          <label style="margin-left: 20px; margin-right: 5px">三级机构：</label>
          <el-select v-model="org3Id" placeholder="请选择三级机构" clearable style="width: 150px;">
            <el-option
              v-for="item in org3List"
              :key="item.id"
              :label="item.orgName"
              :value="item.id">
            </el-option>
          </el-select>

          <label style="margin-left: 20px; margin-right: 5px">职位：</label>
          <el-select v-model="positionId" placeholder="请选择职位" clearable style="width: 150px;">
            <el-option
              v-for="item in positionList"
              :key="item.id"
              :label="item.positionName"
              :value="item.id">
            </el-option>
          </el-select>
        </div>

        <div style="margin-bottom: 20px;">
          <label style="margin-right: 5px">建档时间：</label>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            style="width: 300px;">
          </el-date-picker>

          <el-button type="primary" style="margin-left: 20px; color: #FFFFFF" @click="pageQuery" class="button1">查询</el-button>
          <el-button type="primary" style="float: right; color:#FFFFFF;" class="button1" @click="handleRegister">+人力资源档案登记</el-button>
        </div>
      </div>

      <!-- 表格 -->
      <el-table
        :cell-style="{'text-align':'center'}"
        :header-cell-style="{'text-align':'center'}"
        :data="records"
        class="table--border"
        stripe
        style="width: 100%;">
        <el-table-column
          prop="archiveNumber"
          label="档案编号"
          width="150">
        </el-table-column>
        <el-table-column
          prop="username"
          label="姓名"
          width="120">
        </el-table-column>
        <el-table-column
          prop="gender"
          label="性别"
          width="80">
        </el-table-column>
        <el-table-column
          prop="mobile"
          label="手机号"
          width="120">
        </el-table-column>
        <el-table-column
          prop="orgName"
          label="所属机构"
          width="150">
        </el-table-column>
        <el-table-column
          prop="positionName"
          label="职位"
          width="120">
        </el-table-column>
        <el-table-column
          prop="title"
          label="职称"
          width="100">
        </el-table-column>
        <el-table-column
          prop="status"
          label="状态"
          width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === '正常'" type="success">{{ scope.row.status }}</el-tag>
            <el-tag v-else-if="scope.row.status === '待复核'" type="warning">{{ scope.row.status }}</el-tag>
            <el-tag v-else-if="scope.row.status === '不通过'" type="danger">{{ scope.row.status }}</el-tag>
            <el-tag v-else type="info">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="createdAt"
          label="登记时间"
          width="180">
        </el-table-column>
        <el-table-column
          label="操作"
          width="300">
          <template slot-scope="scope">
            <el-button size="small" plain type="primary" @click="viewDetail(scope.row)">查看</el-button>
            <el-button size="small" plain type="warning" @click="handleUpdate(scope.row)">变更</el-button>
            <el-button
              v-if="scope.row.status === '待复核'"
              size="small"
              plain
              type="success"
              @click="handleReview(scope.row)">复核</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        class="pageList"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[5, 10, 20, 30, 40, 50]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script lang="ts">
import { queryResourcePage } from '@/api/resource'
import { getOrgList } from '@/api/organizations'
import { getPositionList } from '@/api/positions'

export default {
  data() {
    return {
      org1Id: null,
      org2Id: null,
      org3Id: null,
      positionId: null,
      dateRange: null,
      org1List: [],
      org2List: [],
      org3List: [],
      positionList: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      records: []
    }
  },

  created() {
    this.loadOrg1List()
    this.loadPositionList()
    this.pageQuery()
  },

  methods: {
    // 加载一级机构列表
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

    // 一级机构变化
    async handleOrg1Change() {
      this.org2Id = null
      this.org3Id = null
      this.org2List = []
      this.org3List = []
      if (this.org1Id) {
        try {
          const res = await getOrgList({ orgLevel: 2 })
          if (res.data.code == 200) {
            // 过滤出属于当前一级机构的二级机构
            const allOrg2 = res.data.data
            this.org2List = allOrg2.filter((org: any) => {
              // 需要查询二级机构的parentId是否等于当前一级机构ID
              return org.parentId === this.org1Id
            })
          }
        } catch (error) {
          this.$message.error('获取二级机构列表失败')
        }
      }
    },

    // 二级机构变化
    async handleOrg2Change() {
      this.org3Id = null
      this.org3List = []
      if (this.org2Id) {
        try {
          const res = await getOrgList({ orgLevel: 3 })
          if (res.data.code == 200) {
            // 过滤出属于当前二级机构的三级机构
            const allOrg3 = res.data.data
            this.org3List = allOrg3.filter((org: any) => {
              return org.parentId === this.org2Id
            })
          }
        } catch (error) {
          this.$message.error('获取三级机构列表失败')
        }
      }
    },

    // 加载职位列表
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

    // 查询
    pageQuery() {
      const params: any = {
        pageNum: this.pageNum,
        pageSize: this.pageSize
      }

      if (this.org3Id) {
        params.orgId = this.org3Id
      }
      if (this.positionId) {
        params.positionId = this.positionId
      }
      if (this.dateRange && this.dateRange.length === 2) {
        params.startDate = this.dateRange[0]
        params.endDate = this.dateRange[1]
      }

      queryResourcePage(params).then((res: any) => {
        if (res.data.code == 200) {
          this.records = res.data.data.records
          this.total = res.data.data.total
        }
      }).catch((err) => {
        this.$message.error('请求失败：' + err.message)
      })
    },

    handleSizeChange(pageSize: number) {
      this.pageSize = pageSize
      this.pageQuery()
    },

    handleCurrentChange(page: number) {
      this.pageNum = page
      this.pageQuery()
    },

    // 人力资源档案登记
    handleRegister() {
      this.$router.push('/resources/register')
    },

    // 查看详情
    viewDetail(row: any) {
      this.$router.push({
        path: '/resources/detail',
        query: { id: row.id }
      })
    },

    // 变更
    handleUpdate(row: any) {
      if (row.status === '待复核') {
        this.$message.warning('待复核状态的档案不能变更，请先复核')
        return
      }
      this.$router.push({
        path: '/resources/update',
        query: { id: row.id }
      })
    },

    // 复核
    handleReview(row: any) {
      this.$router.push({
        path: '/resources/review',
        query: { id: row.id }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.table--border {
  margin-top: 20px;
}

::v-deep .el-table__header-wrapper {
  th {
    background-color: rgba(96, 98, 102, 0.24) !important;
    color: #606266 !important;
    font-weight: bold !important;
  }
}

.button1 {
  background-color: #409EFF !important;
  border-color: #409EFF !important;
}

.button1:hover {
  background-color: #66b1ff !important;
  border-color: #66b1ff !important;
}
</style>
