import { defineStore } from 'pinia'
import * as authService from '../services/authService'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('wire_token') || null,
    user: JSON.parse(localStorage.getItem('wire_user') || 'null')
  }),

  getters: {
    isAuthenticated: (state) => !!state.token
  },

  actions: {
    setSession(token, user) {
      this.token = token
      this.user = user
      localStorage.setItem('wire_token', token)
      localStorage.setItem('wire_user', JSON.stringify(user))
    },

    async register(payload) {
      const data = await authService.register(payload)
      this.setSession(data.token, data.user)
      return data
    },

    async login(payload) {
      const data = await authService.login(payload)
      this.setSession(data.token, data.user)
      return data
    },

    async refreshCurrentUser() {
      const user = await authService.fetchCurrentUser()
      this.user = user
      localStorage.setItem('wire_user', JSON.stringify(user))
      return user
    },

    logout() {
      this.token = null
      this.user = null
      localStorage.removeItem('wire_token')
      localStorage.removeItem('wire_user')
    }
  }
})
