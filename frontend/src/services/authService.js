import api from './api'

export function register(payload) {
  return api.post('/auth/register', payload).then((res) => res.data)
}

export function login(payload) {
  return api.post('/auth/login', payload).then((res) => res.data)
}

export function fetchCurrentUser() {
  return api.get('/users/me').then((res) => res.data)
}
