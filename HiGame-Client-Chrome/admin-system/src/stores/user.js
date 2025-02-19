import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, getUserInfo } from '../api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const username = ref('')
  const roles = ref([])

  async function loginAction(loginForm) {
    try {
      const data = await login(loginForm)
      token.value = data.token
      localStorage.setItem('token', data.token)
      await getInfo()
      return data
    } catch (error) {
      console.error('Login failed:', error)
      throw error
    }
  }

  async function getInfo() {
    try {
      const data = await getUserInfo()
      username.value = data.username
      roles.value = data.roles
      return data
    } catch (error) {
      console.error('Get user info failed:', error)
      throw error
    }
  }

  function logout() {
    token.value = ''
    username.value = ''
    roles.value = []
    localStorage.removeItem('token')
  }

  return {
    token,
    username,
    roles,
    loginAction,
    getInfo,
    logout
  }
})
