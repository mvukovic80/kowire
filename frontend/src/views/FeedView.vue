<template>
  <div class="feed-page">
    <TweetComposer @posted="handlePosted" />

    <div v-if="loading && tweets.length === 0" class="state-message">Loading your feed…</div>

    <div v-else-if="tweets.length === 0" class="empty-state">
      <h2 class="empty-title">Nothing here yet</h2>
      <p class="empty-body">
        Follow people from <RouterLink to="/explore">Explore</RouterLink> to see their posts,
        or send the first one yourself.
      </p>
    </div>

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

    <p v-if="errorMessage" class="error-text feed-error">{{ errorMessage }}</p>

    <button v-if="hasMore && !loading" class="btn btn-ghost load-more" @click="loadMore">
      Load more
    </button>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import TweetComposer from '../components/TweetComposer.vue'
import TweetCard from '../components/TweetCard.vue'
import * as tweetService from '../services/tweetService'
import { useAuthStore } from '../store/auth'

const authStore = useAuthStore()

const tweets = ref([])
const page = ref(0)
const pageSize = 20
const hasMore = ref(true)
const loading = ref(false)
const errorMessage = ref('')

async function loadFeed(reset = false) {
  loading.value = true
  errorMessage.value = ''
  try {
    const targetPage = reset ? 0 : page.value
    const results = await tweetService.getFeed(targetPage, pageSize)

    tweets.value = reset ? results : [...tweets.value, ...results]
    hasMore.value = results.length === pageSize
    page.value = targetPage + 1
  } catch (err) {
    errorMessage.value = err.response?.data?.message || 'Could not load your feed.'
  } finally {
    loading.value = false
  }
}

function loadMore() {
  loadFeed(false)
}

function handlePosted(tweet) {
  tweets.value = [tweet, ...tweets.value]
}

async function handleToggleLike(tweetId) {
  const index = tweets.value.findIndex((t) => t.id === tweetId)
  if (index === -1) return
  try {
    const updated = await tweetService.toggleLike(tweetId)
    tweets.value[index] = updated
  } catch {
    // Leave the tweet's like state unchanged if the request fails
  }
}

async function handleDelete(tweetId) {
  try {
    await tweetService.deleteTweet(tweetId)
    tweets.value = tweets.value.filter((t) => t.id !== tweetId)
  } catch {
    // No-op: the tweet stays visible if deletion fails
  }
}

onMounted(() => {
  loadFeed(true)
})
</script>

<style scoped>
.feed-page {
  padding-top: 4px;
}

.state-message {
  text-align: center;
  color: var(--slate);
  padding: 48px 0;
  font-size: 14px;
}

.empty-state {
  text-align: center;
  padding: 64px 20px;
}

.empty-title {
  font-family: var(--font-display);
  font-size: 24px;
  margin: 0 0 8px;
}

.empty-body {
  color: var(--slate);
  font-size: 15px;
  line-height: 1.5;
  margin: 0;
}

.tweet-list {
  display: flex;
  flex-direction: column;
}

.feed-error {
  text-align: center;
  padding: 16px 0;
}

.load-more {
  display: block;
  margin: 20px auto 0;
}
</style>
