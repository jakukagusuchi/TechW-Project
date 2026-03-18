<script setup>
import { RouterView, RouterLink } from 'vue-router';
import { useAuthStore } from './stores/auth';
import { computed, ref, onMounted, onBeforeUnmount } from 'vue';
import api from './services/api';

const auth = useAuthStore();
const cartCount = ref(0);
const scrolled = ref(false);

async function refreshCartCount() {
  if (!auth.isAuthenticated) { cartCount.value = 0; return; }
  try {
    const { data } = await api.get('/api/cart');
    cartCount.value = data?.items?.length || 0;
  } catch {
    cartCount.value = 0;
  }
}

function handleScroll() { scrolled.value = window.scrollY > 10; }

onMounted(() => {
  refreshCartCount();
  window.addEventListener('scroll', handleScroll);
});
onBeforeUnmount(() => window.removeEventListener('scroll', handleScroll));
</script>

<template>
  <nav class="navbar" :class="{ scrolled }">
    <div class="container nav-content">
      <RouterLink to="/" class="logo">
        <span class="logo-icon">⚡</span>
        <span class="logo-text">TechW</span>
      </RouterLink>

      <div class="nav-links">
        <RouterLink to="/products" class="nav-link">Products</RouterLink>

        <RouterLink to="/cart" class="nav-cart" v-if="auth.isAuthenticated">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2 9m13-9l2 9M9 21a1 1 0 100-2 1 1 0 000 2zm10 0a1 1 0 100-2 1 1 0 000 2z"/>
          </svg>
          <span>Cart</span>
          <span v-if="cartCount > 0" class="cart-badge">{{ cartCount }}</span>
        </RouterLink>

        <template v-if="auth.isAuthenticated">
          <RouterLink to="/orders" class="nav-link">My Orders</RouterLink>
          <RouterLink v-if="auth.isAdmin" to="/admin" class="nav-link admin-link">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9.594 3.94c.09-.542.56-.94 1.11-.94h2.593c.55 0 1.02.398 1.11.94l.213 1.281c.063.374.313.686.645.87.074.04.147.083.22.127.324.196.72.257 1.075.124l1.217-.456a1.125 1.125 0 011.37.49l1.296 2.247a1.125 1.125 0 01-.26 1.431l-1.003.827c-.293.24-.438.613-.431.992a6.759 6.759 0 010 .255c-.007.378.138.75.43.99l1.005.828c.424.35.534.954.26 1.43l-1.298 2.247a1.125 1.125 0 01-1.369.491l-1.217-.456c-.355-.133-.75-.072-1.076.124a6.57 6.57 0 01-.22.128c-.331.183-.581.495-.644.869l-.213 1.28c-.09.543-.56.941-1.11.941h-2.594c-.55 0-1.02-.398-1.11-.94l-.213-1.281c-.062-.374-.312-.686-.644-.87a6.52 6.52 0 01-.22-.127c-.325-.196-.72-.257-1.076-.124l-1.217.456a1.125 1.125 0 01-1.369-.49l-1.297-2.247a1.125 1.125 0 01.26-1.431l1.004-.827c.292-.24.437-.613.43-.992a6.932 6.932 0 010-.255c.007-.378-.138-.75-.43-.99l-1.004-.828a1.125 1.125 0 01-.26-1.43l1.297-2.247a1.125 1.125 0 011.37-.491l1.216.456c.356.133.751.072 1.076-.124.072-.044.146-.087.22-.128.332-.183.582-.495.644-.869l.214-1.281z"/><path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/></svg>
            Dashboard
          </RouterLink>
          <button @click="auth.logout(); cartCount = 0" class="btn-logout">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/></svg>
            Logout
          </button>
        </template>
        <template v-else>
          <RouterLink to="/login" class="btn-primary nav-login">
            Login
          </RouterLink>
        </template>
      </div>
    </div>
  </nav>

  <main>
    <RouterView />
  </main>

  <footer class="footer">
    <div class="container footer-content">
      <div class="footer-brand">
        <span class="logo-icon">⚡</span>
        <span>TechW E-Commerce</span>
      </div>
      <p class="footer-copy">© 2026 TechW. All rights reserved.</p>
    </div>
  </footer>
</template>

<style scoped>
.navbar {
  position: sticky;
  top: 0;
  z-index: 100;
  height: 68px;
  background: rgba(8, 11, 20, 0.8);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-bottom: 1px solid var(--border);
  display: flex;
  align-items: center;
  transition: background 0.3s, border-color 0.3s;
}
.navbar.scrolled {
  background: rgba(8, 11, 20, 0.95);
  border-bottom-color: var(--border-strong);
  box-shadow: 0 4px 20px rgba(0,0,0,0.4);
}

.nav-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.35rem;
  font-weight: 800;
  letter-spacing: -0.03em;
}
.logo-icon { font-size: 1.4rem; }
.logo-text {
  background: linear-gradient(135deg, #818cf8, #6366f1);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.nav-link {
  padding: 0.45rem 0.85rem;
  border-radius: var(--radius-sm);
  font-size: 0.9rem;
  font-weight: 500;
  color: var(--text-muted);
  transition: all 0.2s;
}
.nav-link:hover, .nav-link.router-link-active {
  color: var(--text-light);
  background: var(--surface-2, rgba(255,255,255,0.06));
}

.admin-link {
  display: flex;
  align-items: center;
  gap: 0.4rem;
}

.nav-cart {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.45rem 0.9rem;
  border-radius: var(--radius-sm);
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--text-muted);
  position: relative;
  transition: all 0.2s;
}
.nav-cart:hover, .nav-cart.router-link-active {
  color: var(--primary);
  background: var(--primary-light);
}

.cart-badge {
  position: absolute;
  top: 2px;
  right: 2px;
  background: var(--primary);
  color: white;
  font-size: 0.65rem;
  font-weight: 800;
  width: 17px;
  height: 17px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid var(--bg-dark);
}

.btn-logout {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.45rem 0.9rem;
  border-radius: var(--radius-sm);
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--danger);
  background: transparent;
  border: none;
  transition: all 0.2s;
}
.btn-logout:hover {
  background: rgba(239, 68, 68, 0.1);
}

.nav-login {
  margin-left: 0.5rem;
  padding: 0.5rem 1.2rem;
  font-size: 0.85rem;
}

main {
  min-height: calc(100vh - 68px - 72px);
}

.footer {
  height: 72px;
  background: var(--surface);
  border-top: 1px solid var(--border);
  display: flex;
  align-items: center;
}
.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.footer-brand {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 700;
  font-size: 0.95rem;
  color: var(--text-muted);
}
.footer-copy {
  font-size: 0.85rem;
  color: var(--text-dim);
}
</style>
