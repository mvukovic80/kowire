import api from './api'

export function getFeed(page = 0, size = 20) {
  return api.get('/tweets/feed', { params: { page, size } }).then((res) => res.data)
}

export function getExploreFeed(page = 0, size = 20) {
  return api.get('/tweets/explore', { params: { page, size } }).then((res) => res.data)
}

export function getUserTweets(username, page = 0, size = 20) {
  return api.get(`/tweets/user/${username}`, { params: { page, size } }).then((res) => res.data)
}

export function createTweet(content) {
  return api.post('/tweets', { content }).then((res) => res.data)
}

export function toggleLike(tweetId) {
  return api.post(`/tweets/${tweetId}/like`).then((res) => res.data)
}

export function deleteTweet(tweetId) {
  return api.delete(`/tweets/${tweetId}`)
}
