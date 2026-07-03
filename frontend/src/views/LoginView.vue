<template>
  <div class="auth-page">
    <div class="auth-card">
      <h1 class="auth-wordmark">KoWire</h1>
      <p class="auth-tagline">Kovačevići svih zemalja ujedinite se.</p>

      <form class="auth-form" @submit.prevent="handleSubmit">
        <label class="field-label" for="usernameOrEmail">Username or email</label>
        <input
          id="usernameOrEmail"
          v-model="form.usernameOrEmail"
          type="text"
          autocomplete="username"
          placeholder="jane_doe or jane@example.com"
          required
        />

        <label class="field-label" for="password">Password</label>
        <input
          id="password"
          v-model="form.password"
          type="password"
          autocomplete="current-password"
          placeholder="••••••••"
          required
        />

        <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>

        <button type="submit" class="btn btn-primary auth-submit" :disabled="loading">
          {{ loading ? 'Signing in…' : 'Sign in' }}
        </button>
      </form>

      <p class="auth-switch">
        New here? <RouterLink to="/register">Create an account</RouterLink>
      </p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'

const form = reactive({
  usernameOrEmail: '',
  password: ''
})

const loading = ref(false)
const errorMessage = ref('')

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()

async function handleSubmit() {
  errorMessage.value = ''
  loading.value = true
  try {
    await authStore.login({ ...form })
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (err) {
    errorMessage.value = err.response?.data?.message || 'Could not sign in. Check your details and try again.'
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
