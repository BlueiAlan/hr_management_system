<template>
  <div>
    <!-- <div
      v-if=" !item.meta || !item.meta.hidden "
      :class="['menu-wrapper', isCollapse ? 'simple-mode' : 'full-mode', {'first-level': isFirstLevel}]"
    > -->
    <div
      v-if="!item.meta || !item.meta.hidden"
      :class="['menu-wrapper', 'full-mode', { 'first-level': isFirstLevel }]"
    >
      <template v-if="theOnlyOneChild && !theOnlyOneChild.children">
        <sidebar-item-link
          v-if="theOnlyOneChild.meta"
          :to="resolvePath(theOnlyOneChild.path)"
        >
          <el-menu-item
            :index="resolvePath(theOnlyOneChild.path)"
            :class="{ 'submenu-title-noDropdown': isFirstLevel }"
          >
            <!-- <i v-if="theOnlyOneChild.meta.title==='工作台'" class="iconfont icon img-icon-sel" /> -->
            <!-- <svg-icon v-if="theOnlyOneChild.meta.title==='工作台'" name="dashboard" width="20" height="20"></svg-icon> -->
            <i
              v-if="theOnlyOneChild.meta.icon"
              class="iconfont"
              :class="theOnlyOneChild.meta.icon"
            />
            <span v-if="theOnlyOneChild.meta.title" slot="title">{{
              theOnlyOneChild.meta.title
            }}</span>
          </el-menu-item>
        </sidebar-item-link>
      </template>
      <el-submenu v-else :index="resolvePath(item.path)" popper-append-to-body>
        <template slot="title">
          <i
            v-if="item.meta && item.meta.icon"
            class="iconfont"
            :class="item.meta.icon"
          />
          <span v-if="item.meta && item.meta.title" slot="title">{{
            item.meta.title
          }}</span>
        </template>
        <template v-if="item.children">
          <sidebar-item
            v-for="child in filteredChildren"
            :key="child.path"
            :item="child"
            :is-collapse="isCollapse"
            :is-first-level="false"
            :base-path="resolvePath(child.path)"
            class="nest-menu"
          />
        </template>
      </el-submenu>
    </div>
  </div>
</template>

<script lang="ts">
import path from 'path'
import { Component, Prop, Vue } from 'vue-property-decorator'
import { UserModule } from '@/store/modules/user'
import { Route, RouteConfig } from 'vue-router'
import { isExternal } from '@/utils/validate'
import SidebarItemLink from './SidebarItemLink.vue'
import Cookies from 'js-cookie'
import { hasPermission, Role } from '@/utils/permission'

@Component({
  name: 'SidebarItem',
  components: {
    SidebarItemLink,
  },
})
export default class extends Vue {
  @Prop({ required: true }) private item!: RouteConfig
  @Prop({ default: false }) private isCollapse!: boolean
  @Prop({ default: true }) private isFirstLevel!: boolean
  @Prop({ default: '' }) private basePath!: string

  get showingChildNumber() {
    if (this.item.children) {
      const showingChildren = this.item.children.filter((item) => {
        if (item.meta && item.meta.hidden) {
          return false
        }
        // 权限检查
        return this.hasItemPermission(item)
      })
      return showingChildren.length
    }
    return 0
  }

  get roles() {
    return UserModule.roles
  }

  // 检查菜单项是否有权限
  private hasItemPermission(item: RouteConfig): boolean {
    // 如果路由没有定义权限要求，默认显示
    if (!item.meta || !item.meta.roles || !Array.isArray(item.meta.roles)) {
      return true
    }
    
    // 获取用户角色
    const userInfo = Cookies.get('user_info') ? JSON.parse(Cookies.get('user_info') as string) : {}
    const role = UserModule.role || userInfo.role || 0
    
    // 超级管理员拥有所有权限
    if (role === Role.ADMIN) {
      return true
    }
    
    // 检查用户角色是否在允许的角色列表中
    return hasPermission(role, item.meta.roles as number[])
  }

  // 过滤子菜单项（根据权限）
  get filteredChildren() {
    if (!this.item.children) {
      return []
    }
    return this.item.children.filter((child) => {
      // 隐藏的菜单项不显示
      if (child.meta && child.meta.hidden) {
        return false
      }
      // 权限检查
      return this.hasItemPermission(child)
    })
  }

  get theOnlyOneChild() {
    if (this.showingChildNumber > 0) {
      return null
    }
    if (this.item.children) {
      for (let child of this.item.children) {
        if (!child.meta || !child.meta.hidden) {
          return child
        }
      }
    }
    // If there is no children, return itself with path removed,
    // because this.basePath already conatins item's path information
    return { ...this.item, path: '' }
  }

  private resolvePath(routePath: string) {
    if (isExternal(routePath)) {
      return routePath
    }
    if (isExternal(this.basePath)) {
      return this.basePath
    }
    return path.resolve(this.basePath, routePath)
  }
}
</script>
