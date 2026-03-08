<script setup>
import { RouterView, RouterLink } from 'vue-router';
import { useAuthStore } from './stores/auth';

const auth = useAuthStore();
</script>

<template>
  <nav class="navbar">
    <div class="container nav-content">
      <RouterLink to="/" class="logo">TechW Store</RouterLink>
      <div class="nav-links">
        <RouterLink to="/products">Products</RouterLink>
        <RouterLink to="/cart">Cart</RouterLink>
        <template v-if="auth.isAuthenticated">
          <RouterLink to="/orders">My Orders</RouterLink>
          <RouterLink v-if="auth.isAdmin" to="/admin">Dashboard</RouterLink>
          <button @click="auth.logout" class="btn-logout">Logout</button>
        </template>
        <template v-else>
          <RouterLink to="/login">Login</RouterLink>
        </template>
      </div>
    </div>
  </nav>

  <main>
    <RouterView />
  </main>

  <footer class="footer">
    <div class="container">
      <p>&copy; 2026 TechW E-Commerce. All rights reserved.</p>
    </div>
  </footer>
</template>

<style scoped>
.navbar {
  height: 70px;
  background-color: var(--surface);
  border-bottom: 1px solid var(--border);
  display: flex;
  align-items: center;
}

.nav-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--primary);
}

.nav-links {
  display: flex;
  gap: 2rem;
  align-items: center;
}

.nav-links a {
  font-weight: 500;
  color: var(--text-muted);
  transition: 0.2s;
}

.nav-links a:hover, .nav-links a.router-link-active {
  color: var(--text-light);
}

.btn-logout {
  background: transparent;
  color: #ef4444;
  font-size: 0.9rem;
}

main {
  min-height: calc(100vh - 140px);
  padding: 2rem 0;
}

.footer {
  height: 70px;
  background-color: var(--surface);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
}
</style>
