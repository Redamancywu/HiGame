&lt;template&gt;
  &lt;el-container class="layout-container"&gt;
    &lt;el-aside width="200px"&gt;
      &lt;el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        :router="true"
        background-color="#001529"
        text-color="#fff"
      &gt;
        &lt;el-menu-item index="/dashboard"&gt;
          &lt;el-icon&gt;&lt;DataLine /&gt;&lt;/el-icon&gt;
          &lt;span&gt;数据面板&lt;/span&gt;
        &lt;/el-menu-item&gt;
        &lt;el-menu-item index="/users"&gt;
          &lt;el-icon&gt;&lt;User /&gt;&lt;/el-icon&gt;
          &lt;span&gt;用户管理&lt;/span&gt;
        &lt;/el-menu-item&gt;
        &lt;el-menu-item index="/devices"&gt;
          &lt;el-icon&gt;&lt;Monitor /&gt;&lt;/el-icon&gt;
          &lt;span&gt;设备管理&lt;/span&gt;
        &lt;/el-menu-item&gt;
        &lt;el-menu-item index="/settings"&gt;
          &lt;el-icon&gt;&lt;Setting /&gt;&lt;/el-icon&gt;
          &lt;span&gt;系统设置&lt;/span&gt;
        &lt;/el-menu-item&gt;
      &lt;/el-menu&gt;
    &lt;/el-aside&gt;
    &lt;el-container&gt;
      &lt;el-header&gt;
        &lt;div class="header-content"&gt;
          &lt;div class="header-left"&gt;
            &lt;el-breadcrumb separator="/"&gt;
              &lt;el-breadcrumb-item :to="{ path: '/' }"&gt;首页&lt;/el-breadcrumb-item&gt;
              &lt;el-breadcrumb-item&gt;{{ currentPath }}&lt;/el-breadcrumb-item&gt;
            &lt;/el-breadcrumb&gt;
          &lt;/div&gt;
          &lt;div class="header-right"&gt;
            &lt;el-dropdown&gt;
              &lt;span class="user-info"&gt;
                &lt;el-avatar :size="32" :src="userAvatar" /&gt;
                &lt;span&gt;{{ userName }}&lt;/span&gt;
              &lt;/span&gt;
              &lt;template #dropdown&gt;
                &lt;el-dropdown-menu&gt;
                  &lt;el-dropdown-item&gt;个人信息&lt;/el-dropdown-item&gt;
                  &lt;el-dropdown-item&gt;修改密码&lt;/el-dropdown-item&gt;
                  &lt;el-dropdown-item divided&gt;退出登录&lt;/el-dropdown-item&gt;
                &lt;/el-dropdown-menu&gt;
              &lt;/template&gt;
            &lt;/el-dropdown&gt;
          &lt;/div&gt;
        &lt;/div&gt;
      &lt;/el-header&gt;
      &lt;el-main&gt;
        &lt;router-view&gt;&lt;/router-view&gt;
      &lt;/el-main&gt;
    &lt;/el-container&gt;
  &lt;/el-container&gt;
&lt;/template&gt;

&lt;script setup&gt;
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { DataLine, User, Monitor, Setting } from '@element-plus/icons-vue'

const route = useRoute()
const activeMenu = computed(() =&gt; route.path)
const currentPath = computed(() =&gt; {
  const pathMap = {
    '/dashboard': '数据面板',
    '/users': '用户管理',
    '/devices': '设备管理',
    '/settings': '系统设置'
  }
  return pathMap[route.path] || '首页'
})

const userAvatar = ref('https://api.higame.com/static/default-avatar.png')
const userName = ref('管理员')
&lt;/script&gt;

&lt;style scoped&gt;
.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: #001529;
}

.el-menu-vertical {
  border-right: none;
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
}

.header-content {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-info span {
  margin-left: 8px;
  color: #333;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
&lt;/style&gt;
