<template>
  <div class="auth-page">
    <div class="auth-card">
      <h1 class="auth-wordmark">KoWire</h1>
      <p class="auth-tagline">Pick a handle. Start sending.</p>

      <form class="auth-form" @submit.prevent="handleSubmit">
        <label class="field-label" for="displayName">Display name</label>
        <input id="displayName" v-model="form.displayName" type="text" placeholder="Jane Doe" required />

        <label class="field-label" for="username">Username</label>
        <input
          id="username"
          v-model="form.username"
          type="text"
          placeholder="jane_doe"
          pattern="^[a-zA-Z0-9_]+$"
          title="Letters, numbers, and underscores only"
          minlength="3"
          maxlength="20"
          autocomplete="username"
          required
        />

        <label class="field-label" for="email">Email</label>
        <input id="email" v-model="form.email" type="email" placeholder="jane@example.com" autocomplete="email" required />

        <label class="field-label" for="password">Password</label>
        <input
          id="password"
          v-model="form.password"
          type="password"
          placeholder="At least 6 characters"
          minlength="6"
          autocomplete="new-password"
          required
        />

        <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>

        <button type="submit" class="btn btn-primary auth-submit" :disabled="loading">
          {{ loading ? 'Creating account…' : 'Create account' }}
        </button>
      </form>

      <p class="auth-switch">
        Already have an account? <RouterLink to="/login">Sign in</RouterLink>
      </p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'

const form = reactive({
  displayName: '',
  username: '',
  email: '',
  password: ''
})

const loading = ref(false)
const errorMessage = ref('')

const authStore = useAuthStore()
const router = useRouter()

async function handleSubmit() {
  errorMessage.value = ''
  loading.value = true
  try {
    await authStore.register({ ...form })
    router.push('/')
  } catch (err) {
    const data = err.response?.data
    if (data?.errors) {
      errorMessage.value = Object.values(data.errors)[0]
    } else {
      errorMessage.value = data?.message || 'Could not create your account. Please try again.'
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.auth-card {
  width: 100%;
  max-width: 380px;
}

.auth-wordmark {
  font-family: var(--font-display);
  font-size: 40px;
  font-weight: 600;
  margin: 0 0 6px;
  letter-spacing: -0.01em;
}

.auth-tagline {
  color: var(--slate);
  font-size: 15px;
  margin: 0 0 32px;
  line-height: 1.5;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.field-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--ink);
  margin-top: 12px;
}

.auth-submit {
  margin-top: 20px;
  width: 100%;
  padding: 13px;
}

.auth-switch {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: var(--slate);
}

.auth-switch a {
  font-weight: 600;
}
</style>
