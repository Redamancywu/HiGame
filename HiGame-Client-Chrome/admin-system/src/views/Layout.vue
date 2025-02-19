&lt;template>
  &lt;el-container class="layout-container">
    &lt;el-aside width="200px">
      &lt;el-menu
        :router="true"
        :default-active="route.path"
        class="el-menu-vertical"
      >
        &lt;el-menu-item index="/">
          &lt;el-icon>&lt;DataLine />&lt;/el-icon>
          &lt;span>数据统计&lt;/span>
        &lt;/el-menu-item>
        &lt;el-menu-item index="/users">
          &lt;el-icon>&lt;User />&lt;/el-icon>
          &lt;span>用户管理&lt;/span>
        &lt;/el-menu-item>
      &lt;/el-menu>
    &lt;/el-aside>
    
    &lt;el-container>
      &lt;el-header>
        &lt;div class="header-left">
          &lt;el-icon @click="toggleCollapse" class="toggle-button">
            &lt;Fold v-if="!isCollapse" />
            &lt;Expand v-else />
          &lt;/el-icon>
        &lt;/div>
        &lt;div class="header-right">
          &lt;el-dropdown>
            &lt;span class="user-info">
              {{ userStore.username }}
              &lt;el-icon>&lt;CaretBottom />&lt;/el-icon>
            &lt;/span>
            &lt;template #dropdown>
              &lt;el-dropdown-menu>
                &lt;el-dropdown-item @click="handleLogout">退出登录&lt;/el-dropdown-item>
              &lt;/el-dropdown-menu>
            &lt;/template>
          &lt;/el-dropdown>
        &lt;/div>
      &lt;/el-header>
      
      &lt;el-main>
        &lt;router-view />
      &lt;/el-main>
    &lt;/el-container>
  &lt;/el-container>
&lt;/template>

&lt;script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { DataLine, User, Fold, Expand, CaretBottom } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleLogout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}
&lt;/script>

&lt;style scoped>
.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
  color: #fff;
}

.el-menu {
  border-right: none;
}

.el-header {
  background-color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.toggle-button {
  font-size: 20px;
  cursor: pointer;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
&lt;/style>
