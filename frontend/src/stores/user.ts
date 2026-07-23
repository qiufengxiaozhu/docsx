import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('docsx_token') || '')
  const username = ref('')

  function setToken(t: string) {
    token.value = t
    localStorage.setItem('docsx_token', t)
  }

  function logout() {
    token.value = ''
    username.value = ''
    localStorage.removeItem('docsx_token')
  }

  return { token, username, setToken, logout }
})
