<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator'
import { AppModule } from '@/store/modules/app'
import { UserModule } from '@/store/modules/user'
import SidebarItem from './SidebarItem.vue'
import variables from '@/styles/_variables.scss'
import { getSidebarStatus, setSidebarStatus } from '@/utils/cookies'
import Cookies from 'js-cookie'
@Component({
  name: 'SideBar',
  components: {
    SidebarItem
  }
})
export default class extends Vue {
  private restKey: number = 0
  get name() {
    return (UserModule.userInfo as any).name
      ? (UserModule.userInfo as any).name
      : JSON.parse(Cookies.get('user_info') as string).name
  }
  get defOpen() {
    // const urlArr = this.$route.path.split('/')
    // const openStr = urlArr.length > 2 ? `/${urlArr[1]}` : '/'
    let path = ['/']
    this.routes.forEach((n: any, i: number) => {
      if (n.meta.roles && n.meta.roles[0] === this.roles[0]) {
        path.splice(0, 1, n.path)
      }
    })
    return path
  }

  get defAct() {
    let path = this.$route.path
    return path
  }

  get sidebar() {
    return AppModule.sidebar
  }

  get roles() {
    return UserModule.roles
  }

  get routes() {
    let routes = JSON.parse(
      JSON.stringify([...(this.$router as any).options.routes])
    )
    console.log('-=-=routes=-=-=', routes)
    console.log('-=-=routes=-=-=', this.roles[0])
    let menuList = []
    let menu = routes.find(item => item.path === '/')
    if (menu) {
      menuList = menu.children
      // 使用role字段判断权限
      const userInfo = Cookies.get('user_info') ? JSON.parse(Cookies.get('user_info') as string) : {}
      const role = UserModule.role || userInfo.role || 0

      // 根据权限过滤菜单项
      menuList = menuList.filter((item: any) => {
        // 如果路由没有定义权限要求，默认显示（兼容旧代码）
        if (!item.meta || !item.meta.roles || !Array.isArray(item.meta.roles)) {
          return true
        }
        
        // 检查用户是否有访问该菜单的权限
        // 超级管理员(0)拥有所有权限
        if (role === 0) {
          return true
        }
        
        // 检查用户角色是否在允许的角色列表中
        return item.meta.roles.includes(role)
      })
    }
    console.log('-=-=routes=-wwww=-=', routes)
    return menuList
  }

  get variables() {
    return variables
  }

  get isCollapse() {
    return !this.sidebar.opened
  }
  private async logout() {
    this.$store.dispatch('LogOut').then(() => {
      // location.href = '/'
      this.$router.replace({ path: '/login' })
    })
    // this.$router.push(`/login?redirect=${this.$route.fullPath}`)
  }
}
</script>

<template>
  <div>
    <div class="logo">
      <!-- <img
        src="./../../../assets/logo.png"
        width="122.5"
        alt=""
      > -->
      <!-- <img
        src="@/assets/login/login-logo.png"
        alt=""
        style="width: 120px; height: 31px"
      /> -->
      <div v-if="!isCollapse"
           class="sidebar-logo">
<!--        <img src="@/assets/login/logo.png"-->
<!--             style="width: 120px; height: 31px">-->
        <div class="logoText" style="color: #FFFFFF ;font-size: 20px ;text-align: center">
          人力资源管理系统
        </div>
      </div>
      <div v-else
           class="sidebar-logo-mini">
        <img src="@/assets/login/mini-logo.jpg">
      </div>
    </div>
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu :default-openeds="defOpen"
               :default-active="defAct"
               :collapse="isCollapse"
               :background-color="variables.menuBg"
               :text-color="variables.menuText"
               :active-text-color="variables.menuActiveText"
               :unique-opened="false"
               :collapse-transition="false"
               mode="vertical">
        <sidebar-item v-for="route in routes"
                      :key="route.path"
                      :item="route"
                      :base-path="route.path"
                      :is-collapse="isCollapse" />
        <!-- <div class="sub-menu">
          <div class="avatarName">
            {{ name }}
          </div>
          <div class="img">
            <img
              src="./../../../assets/icons/btn_close@2x.png"
              class="outLogin"
              alt="退出"
              @click="logout"
            />
          </div>
        </div> -->
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<!--<script lang="ts">-->
<!--import { Component, Prop, Vue } from 'vue-property-decorator'-->
<!--import { AppModule } from '@/store/modules/app'-->
<!--import { UserModule } from '@/store/modules/user'-->
<!--import SidebarItem from './SidebarItem.vue'-->
<!--import variables from '@/styles/_variables.scss'-->
<!--import { getSidebarStatus, setSidebarStatus } from '@/utils/cookies'-->
<!--import Cookies from 'js-cookie'-->
<!--@Component({-->
<!--  name: 'SideBar',-->
<!--  components: {-->
<!--    SidebarItem-->
<!--  }-->
<!--})-->
<!--export default class extends Vue {-->
<!--  private restKey: number = 0-->
<!--  get name() {-->
<!--    return (UserModule.userInfo as any).name-->
<!--      ? (UserModule.userInfo as any).name-->
<!--      : JSON.parse(Cookies.get('user_info') as any).name-->
<!--  }-->
<!--  get defOpen() {-->
<!--    // const urlArr = this.$route.path.split('/')-->
<!--    // const openStr = urlArr.length > 2 ? `/${urlArr[1]}` : '/'-->
<!--    let path = ['/']-->
<!--    this.routes.forEach((n: any, i: number) => {-->
<!--      if (n.meta.roles && n.meta.roles[0] === this.roles[0]) {-->
<!--        path.splice(0, 1, n.path)-->
<!--      }-->
<!--    })-->
<!--    return path-->
<!--  }-->

<!--  get defAct() {-->
<!--    let path = this.$route.path-->
<!--    return path-->
<!--  }-->

<!--  get sidebar() {-->
<!--    return AppModule.sidebar-->
<!--  }-->

<!--  get roles() {-->
<!--    return UserModule.roles-->
<!--  }-->

<!--  get routes() {-->
<!--    let routes = JSON.parse(-->
<!--      JSON.stringify([...(this.$router as any).options.routes])-->
<!--    )-->
<!--    console.log('-=-=routes=-=-=', routes)-->
<!--    console.log('-=-=routes=-=-=', this.roles[0])-->
<!--    let menuList = []-->
<!--    let menu = routes.find(item => item.path === '/')-->
<!--    if (menu) {-->
<!--      menuList = menu.children-->
<!--    }-->
<!--    console.log('-=-=routes=-wwww=-=', routes)-->
<!--    return menuList-->
<!--  }-->

<!--  get variables() {-->
<!--    return variables-->
<!--  }-->

<!--  get isCollapse() {-->
<!--    return !this.sidebar.opened-->
<!--  }-->
<!--  private async logout() {-->
<!--    this.$store.dispatch('LogOut').then(() => {-->
<!--      // location.href = '/'-->
<!--      this.$router.replace({ path: '/login' })-->
<!--    })-->
<!--    // this.$router.push(`/login?redirect=${this.$route.fullPath}`)-->
<!--  }-->
<!--}-->
<!--</script>-->

<style lang="scss" scoped>
.logo {
  text-align: center;
  background-color: rgb(52, 55, 68);
  padding: 15px 0 0;
  height: 60px;
  img {
    display: inline-block;
  }
}
.sidebar-logo-mini {
  img {
    width: 30px;
    height: 30px;
  }
}
.el-scrollbar {
  height: 100%;
  background-color: rgb(52, 55, 68);
}

.el-menu {
  border: none;
  height: calc(95vh - 23px);
  width: 100% !important;
  padding: 47px 15px 0;
}
</style>
