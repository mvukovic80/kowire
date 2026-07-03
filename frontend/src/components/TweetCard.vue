<template>
  <article class="tweet">
    <div class="tweet-main">
      <RouterLink :to="`/profile/${tweet.authorUsername}`" class="avatar" :aria-label="tweet.authorDisplayName">
        {{ initials }}
      </RouterLink>

      <div class="tweet-body">
        <div class="tweet-meta">
          <RouterLink :to="`/profile/${tweet.authorUsername}`" class="author-name">
            {{ tweet.authorDisplayName }}
          </RouterLink>
          <span class="author-handle">@{{ tweet.authorUsername }}</span>
          <span class="dot">·</span>
          <span class="timestamp">{{ relativeTime }}</span>
        </div>

        <p class="tweet-content">{{ tweet.content }}</p>

        <div class="tweet-actions">
          <button
            class="action-btn like-btn"
            :class="{ liked: tweet.likedByCurrentUser }"
            :aria-pressed="tweet.likedByCurrentUser"
            @click="$emit('toggle-like', tweet.id)"
          >
            <svg width="16" height="16" viewBox="0 0 24 24" :fill="tweet.likedByCurrentUser ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="1.8">
              <path d="M12 20.5s-7.5-4.6-10-9.3C0.6 8.1 1.8 4.8 4.8 3.7c2.2-0.8 4.4 0 5.8 1.8l1.4 1.7 1.4-1.7c1.4-1.8 3.6-2.6 5.8-1.8 3 1.1 4.2 4.4 2.8 7.5-2.5 4.7-10 9.3-10 9.3z" stroke-linejoin="round"/>
            </svg>
            <span>{{ tweet.likeCount }}</span>
          </button>

          <button v-if="isOwnTweet" class="action-btn delete-btn" @click="$emit('delete', tweet.id)">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="3 6 5 6 21 6"/>
              <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6m3-3h8a1 1 0 0 1 1 1v2H7V4a1 1 0 0 1 1-1z"/>
            </svg>
            <span>Delete</span>
          </button>
        </div>
      </div>
    </div>
  </article>
</template>

<script setup>
import { computed } from 'vue'
import { RouterLink } from 'vue-router'

const props = defineProps({
  tweet: { type: Object, required: true },
  currentUsername: { type: String, default: '' }
})

defineEmits(['toggle-like', 'delete'])

const initials = computed(() => {
  const name = props.tweet.authorDisplayName || props.tweet.authorUsername || '?'
  return name
    .split(' ')
    .map((part) => part[0])
    .slice(0, 2)
    .join('')
    .toUpperCase()
})

const isOwnTweet = computed(() => props.tweet.authorUsername === props.currentUsername)

const relativeTime = computed(() => {
  const created = new Date(props.tweet.createdAt)
  const now = new Date()
  const seconds = Math.floor((now - created) / 1000)

  if (seconds < 60) return 'just now'
  const minutes = Math.floor(seconds / 60)
  if (minutes < 60) return `${minutes}m`
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours}h`
  const days = Math.floor(hours / 24)
  if (days < 7) return `${days}d`
  return created.toLocaleDateString(undefined, { month: 'short', day: 'numeric' })
})
</script>

<style scoped>
.tweet {
  padding: 16px 0;
  border-bottom: 1px solid var(--hairline);
}

.tweet-main {
  display: flex;
  gap: 12px;
}

.avatar {
  flex-shrink: 0;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: var(--wire-dim);
  color: var(--wire);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
}

.tweet-body {
  flex: 1;
  min-width: 0;
}

.tweet-meta {
  display: flex;
  align-items: baseline;
  gap: 6px;
  flex-wrap: wrap;
  font-size: 14px;
}

.author-name {
  font-weight: 700;
  color: var(--ink);
}

.author-handle,
.timestamp {
  color: var(--slate);
}

.dot {
  color: var(--slate);
}

.tweet-content {
  margin: 4px 0 10px;
  font-size: 15px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-word;
}

.tweet-actions {
  display: flex;
  gap: 18px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: none;
  border: none;
  cursor: pointer;
  color: var(--slate);
  font-size: 13px;
  font-weight: 600;
  padding: 4px 6px;
  border-radius: 6px;
}

.like-btn:hover {
  color: var(--danger);
  background: var(--danger-dim);
}

.like-btn.liked {
  color: var(--danger);
}

.delete-btn:hover {
  color: var(--danger);
  background: var(--danger-dim);
}
</style>
