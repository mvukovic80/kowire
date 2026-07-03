import api from './api'

export function getProfile(username) {
  return api.get(`/users/${username}`).then((res) => res.data)
}

export function searchUsers(query) {
  return api.get('/users/search', { params: { query } }).then((res) => res.data)
}

export function followUser(username) {
  return api.post(`/users/${username}/follow`).then((res) => res.data)
}

export function unfollowUser(username) {
  return api.delete(`/users/${username}/follow`).then((res) => res.data)
}
