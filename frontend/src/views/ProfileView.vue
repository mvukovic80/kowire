<template>
  <div class="profile-page">
    <p v-if="loadingProfile" class="state-message">Loading profile…</p>

    <p v-else-if="profileError" class="state-message">{{ profileError }}</p>

    <template v-else-if="profile">
      <div class="profile-header">
        <span class="avatar-lg">{{ profile.displayName.charAt(0).toUpperCase() }}</span>

        <div class="profile-info">
          <h1 class="display-name">{{ profile.displayName }}</h1>
          <p class="handle">@{{ profile.username }}</p>
          <p v-if="profile.bio" class="bio">{{ profile.bio }}</p>

          <div class="stats">
            <span><strong>{{ profile.followingCount }}</strong> Following</span>
            <span><strong>{{ profile.followerCount }}</strong> Followers</span>
          </div>
        </div>

        <button
          v-if="!isOwnProfile"
          class="btn"
          :class="profile.isFollowedByCurrentUser ? 'btn-ghost' : 'btn-primary'"
          :disabled="followBusy"
          @click="handleFollowToggle"
        >
          {{ profile.isFollowedByCurrentUser ? 'Following' : 'Follow' }}
        </button>
      </div>

      <hr class="hairline" />

      <p v-if="loadingTweets && tweets.length === 0" class="state-message">Loading posts…</p>
      <p v-else-if="tweets.length === 0" class="state-message">
        {{ isOwnProfile ? "You haven't posted yet." : `@${profile.username} hasn't posted yet.` }}
      </p>

      <div v-else class="tweet-list">
        <TweetCard
          v-for="tweet in tweets"
          :key="tweet.id"
          :tweet="tweet"
          :current-username="authStore.user?.username"
          @toggle-like="handleToggleLike"
          @delete="handleDelete"
        />
      </div>

      <button v-if="hasMore && !loadingTweets" class="btn btn-ghost load-more" @click="loadMoreTweets">
        Load more
      </button>
    </template>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import TweetCard from '../components/TweetCard.vue'
import * as tweetService from '../services/tweetService'
import * as userService from '../services/userService'
import { useAuthStore } from '../store/auth'

const route = useRoute()
const authStore = useAuthStore()

const profile = ref(null)
const loadingProfile = ref(false)
const profileError = ref('')
const followBusy = ref(false)

const tweets = ref([])
const page = ref(0)
const pageSize = 20
const hasMore = ref(true)
const loadingTweets = ref(false)

const isOwnProfile = ref(false)

async function loadProfile(username) {
  loadingProfile.value = true
  profileError.value = ''
  try {
    profile.value = await userService.getProfile(username)
    isOwnProfile.value = authStore.user?.username === username
  } catch (err) {
    profileError.value = err.response?.data?.message || 'Could not load this profile.'
  } finally {
    loadingProfile.value = false
  }
}

async function loadTweets(username, reset = false) {
  loadingTweets.value = true
  try {
    const targetPage = reset ? 0 : page.value
    const results = await tweetService.getUserTweets(username, targetPage, pageSize)

    tweets.value = reset ? results : [...tweets.value, ...results]
    hasMore.value = results.length === pageSize
    page.value = targetPage + 1
  } catch {
    // Leave existing tweets in place if a page fails to load
  } finally {
    loadingTweets.value = false
  }
}

function loadMoreTweets() {
  loadTweets(route.params.username, false)
}

async function handleFollowToggle() {
  if (!profile.value) return
  followBusy.value = true
  try {
    if (profile.value.isFollowedByCurrentUser) {
      profile.value = await userService.unfollowUser(profile.value.username)
    } else {
      profile.value = await userService.followUser(profile.value.username)
    }
  } catch {
    // Leave follow state unchanged on failure
  } finally {
    followBusy.value = false
  }
}

async function handleToggleLike(tweetId) {
  const index = tweets.value.findIndex((t) => t.id === tweetId)
  if (index === -1) return
  try {
    const updated = await tweetService.toggleLike(tweetId)
    tweets.value[index] = updated
  } catch {
    // Leave like state unchanged on failure
  }
}

async function handleDelete(tweetId) {
  try {
    await tweetService.deleteTweet(tweetId)
    tweets.value = tweets.value.filter((t) => t.id !== tweetId)
  } catch {
    // No-op
  }
}

function loadAllForUsername(username) {
  page.value = 0
  tweets.value = []
  hasMore.value = true
  loadProfile(username)
  loadTweets(username, true)
}

watch(
  () => route.params.username,
  (newUsername) => {
    if (newUsername) loadAllForUsername(newUsername)
  }
)

onMounted(() => {
  loadAllForUsername(route.params.username)
})
</script>

<style scoped>
.profile-page {
  padding-top: 16px;
}

.state-message {
  text-align: center;
  color: var(--slate);
  padding: 32px 0;
  font-size: 14px;
}

.profile-header {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding-bottom: 20px;
}

.avatar-lg {
  flex-shrink: 0;
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: var(--wire-dim);
  color: var(--wire);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 24px;
}

.profile-info {
  flex: 1;
  min-width: 0;
}

.display-name {
  font-family: var(--font-display);
  font-size: 22px;
  margin: 0;
}

.handle {
  color: var(--slate);
  font-size: 14px;
  margin: 2px 0 8px;
}

.bio {
  font-size: 14px;
  line-height: 1.5;
  margin: 0 0 10px;
}

.stats {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: var(--slate);
}

.stats strong {
  color: var(--ink);
}

.tweet-list {
  display: flex;
  flex-direction: column;
}

.load-more {
  display: block;
  margin: 20px auto 0;
}
</style>
