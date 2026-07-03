<template>
  <form class="composer" @submit.prevent="handleSubmit">
    <textarea
      v-model="content"
      class="composer-input"
      placeholder="What's happening?"
      rows="3"
      maxlength="280"
      :disabled="posting"
    ></textarea>

    <div class="composer-footer">
      <div class="ring-wrap" :title="`${remaining} characters left`">
        <svg viewBox="0 0 36 36" class="ring">
          <circle class="ring-track" cx="18" cy="18" r="15.5" />
          <circle
            class="ring-fill"
            :class="{ 'ring-fill-warn': remaining <= 20, 'ring-fill-over': remaining < 0 }"
            cx="18" cy="18" r="15.5"
            :style="ringStyle"
          />
        </svg>
        <span v-if="remaining <= 20" class="ring-count">{{ remaining }}</span>
      </div>

      <button type="submit" class="btn btn-primary" :disabled="!canSubmit">
        {{ posting ? 'Sending…' : 'Send' }}
      </button>
    </div>

    <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>
  </form>
</template>

<script setup>
import { computed, ref } from 'vue'
import * as tweetService from '../services/tweetService'

const emit = defineEmits(['posted'])

const content = ref('')
const posting = ref(false)
const errorMessage = ref('')

const MAX_LENGTH = 280
const CIRCUMFERENCE = 2 * Math.PI * 15.5

const remaining = computed(() => MAX_LENGTH - content.value.length)

const canSubmit = computed(() => {
  const trimmed = content.value.trim()
  return trimmed.length > 0 && trimmed.length <= MAX_LENGTH && !posting.value
})

const ringStyle = computed(() => {
  const used = Math.min(content.value.length, MAX_LENGTH)
  const fraction = used / MAX_LENGTH
  const offset = CIRCUMFERENCE * (1 - fraction)
  return {
    strokeDasharray: `${CIRCUMFERENCE}`,
    strokeDashoffset: `${offset}`
  }
})

async function handleSubmit() {
  if (!canSubmit.value) return
  errorMessage.value = ''
  posting.value = true
  try {
    const tweet = await tweetService.createTweet(content.value.trim())
    content.value = ''
    emit('posted', tweet)
  } catch (err) {
    errorMessage.value = err.response?.data?.message || 'Could not send. Please try again.'
  } finally {
    posting.value = false
  }
}
</script>

<style scoped>
.composer {
  padding: 20px 0 24px;
  border-bottom: 1px solid var(--hairline);
}

.composer-input {
  resize: none;
  border: none;
  font-size: 17px;
  padding: 4px 0;
  background: transparent;
  font-family: var(--font-body);
}

.composer-input:focus-visible {
  outline: none;
}

.composer-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 8px;
}

.ring-wrap {
  position: relative;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ring {
  width: 28px;
  height: 28px;
  transform: rotate(-90deg);
}

.ring-track {
  fill: none;
  stroke: var(--hairline);
  stroke-width: 3;
}

.ring-fill {
  fill: none;
  stroke: var(--wire);
  stroke-width: 3;
  stroke-linecap: round;
  transition: stroke-dashoffset 0.15s ease, stroke 0.15s ease;
}

.ring-fill-warn {
  stroke: #C77B1E;
}

.ring-fill-over {
  stroke: var(--danger);
}

.ring-count {
  position: absolute;
  font-size: 10px;
  font-weight: 700;
  color: var(--slate);
}
</style>
