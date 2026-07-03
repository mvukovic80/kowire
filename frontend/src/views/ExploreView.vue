<template>
  <div class="explore-page">
    <div class="search-bar">
      <input
        v-model="searchQuery"
        type="text"
        placeholder="Search people by username"
        @input="handleSearchInput"
      />
    </div>

    <div v-if="searchQuery.trim()" class="search-results">
      <p v-if="searching" class="state-message">Searching…</p>
      <p v-else-if="searchResults.length === 0" class="state-message">No one matches "{{ searchQuery }}"</p>

      <RouterLink
        v-for="result in searchResults"
        :key="result.id"
        :to="`/profile/${result.username}`"
        class="search-result"
      >
        <span class="avatar">{{ result.displayName.charAt(0).toUpperCase() }}</span>
        <span class="result-meta">
          <span class="result-name">{{ result.displayName }}</span>
          <span class="result-handle">@{{ result.username }}</span>
        </span>
      </RouterLink>
    </div>

    <template v-else>
      <h2 class="section-title">Latest</h2>

      <p v-if="loading && tweets.length === 0" class="state-message">Loading…</p>

      <p v-else-if="tweets.length === 0" class="state-message">No posts yet. Be the first to send one.</p>

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

      <button v-if="hasMore && !loading" class="btn btn-ghost load-more" @click="loadMore">
        Load more
      </button>
    </template>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import TweetCard from '../components/TweetCard.vue'
import * as tweetService from '../services/tweetService'
import * as userService from '../services/userService'
import { useAuthStore } from '../store/auth'

const authStore = useAuthStore()

const tweets = ref([])
const page = ref(0)
const pageSize = 20
const hasMore = ref(true)
const loading = ref(false)

const searchQuery = ref('')
const searchResults = ref([])
const searching = ref(false)
let searchDebounce = null

async function loadExplore(reset = false) {
  loading.value = true
  try {
    const targetPage = reset ? 0 : page.value
    const results = await tweetService.getExploreFeed(targetPage, pageSize)

    tweets.value = reset ? results : [...tweets.value, ...results]
    hasMore.value = results.length === pageSize
    page.value = targetPage + 1
  } catch {
    // Leave existing tweets in place if a page fails to load
  } finally {
    loading.value = false
  }
}

function loadMore() {
  loadExplore(false)
}

function handleSearchInput() {
  clearTimeout(searchDebounce)
  if (!searchQuery.value.trim()) {
    searchResults.value = []
    return
  }
  searching.value = true
  searchDebounce = setTimeout(async () => {
    try {
      searchResults.value = await userService.searchUsers(searchQuery.value.trim())
    } catch {
      searchResults.value = []
    } finally {
      searching.value = false
    }
  }, 300)
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

onMounted(() => {
  loadExplore(true)
})
</script>

<style scoped>
.explore-page {
  padding-top: 16px;
}

.search-bar {
  margin-bottom: 20px;
}

.section-title {
  font-family: var(--font-display);
  font-size: 20px;
  margin: 0 0 8px;
}

.state-message {
  text-align: center;
  color: var(--slate);
  padding: 32px 0;
  font-size: 14px;
}

.search-results {
  display: flex;
  flex-direction: column;
}

.search-result {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 4px;
  border-bottom: 1px solid var(--hairline);
  color: var(--ink);
}

.search-result:hover {
  background: var(--wire-dim);
}

.avatar {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--wire-dim);
  color: var(--wire);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 15px;
}

.result-meta {
  display: flex;
  flex-direction: column;
}

.result-name {
  font-weight: 600;
  font-size: 14px;
}

.result-handle {
  color: var(--slate);
  font-size: 13px;
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
