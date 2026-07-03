<template>
  <div class="app-shell">
    <header v-if="authStore.isAuthenticated" class="topbar">
      <RouterLink to="/" class="wordmark">KoWire</RouterLink>

      <nav class="topnav">
        <RouterLink to="/" class="topnav-link" exact-active-class="topnav-link-active">Home</RouterLink>
        <RouterLink to="/explore" class="topnav-link" exact-active-class="topnav-link-active">Explore</RouterLink>
        <RouterLink
          v-if="authStore.user"
          :to="`/profile/${authStore.user.username}`"
          class="topnav-link"
          exact-active-class="topnav-link-active"
        >
          Profile
        </RouterLink>
      </nav>

      <button class="btn btn-ghost logout-btn" @click="handleLogout">Sign out</button>
    </header>

    <main class="app-main">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { useAuthStore } from './store/auth'

const authStore = useAuthStore()
const router = useRouter()

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.app-shell {
  min-height: 100vh;
}

.topbar {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  gap: 28px;
  padding: 14px 24px;
  background: var(--paper);
  border-bottom: 1px solid var(--hairline);
}

.wordmark {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 600;
  color: var(--ink);
  letter-spacing: -0.01em;
}

.topnav {
  display: flex;
  gap: 20px;
  flex: 1;
}

.topnav-link {
  font-size: 14px;
  font-weight: 600;
  color: var(--slate);
  padding: 6px 2px;
  border-bottom: 2px solid transparent;
}

.topnav-link:hover {
  color: var(--ink);
}

.topnav-link-active {
  color: var(--ink);
  border-bottom-color: var(--wire);
}

.logout-btn {
  padding: 8px 16px;
  font-size: 13px;
}

.app-main {
  max-width: 640px;
  margin: 0 auto;
  padding: 0 16px 48px;
}

@media (max-width: 600px) {
  .topbar {
    gap: 16px;
    padding: 12px 16px;
  }
  .topnav {
    gap: 14px;
  }
}
</style>
