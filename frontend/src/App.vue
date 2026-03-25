<script setup>
import { RouterView, RouterLink, useRouter } from 'vue-router';
import { useAuthStore } from './stores/auth';
import { computed, ref, onMounted, onBeforeUnmount } from 'vue';
import api from './services/api';

const router = useRouter();
const auth = useAuthStore();
const cartCount = ref(0);
const scrolled = ref(false);
const showProfileMenu = ref(false);

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

function handleClickOutside(e) {
  if (showProfileMenu.value && !e.target.closest('.profile-dropdown-area')) {
    showProfileMenu.value = false;
  }
}

function toggleProfileMenu() {
  showProfileMenu.value = !showProfileMenu.value;
}

function navigateProfile(path) {
  showProfileMenu.value = false;
  router.push(path);
}

function handleLogout() {
  showProfileMenu.value = false;
  auth.logout();
  router.push('/');
}

function cycleTheme() {
  const themes = ['dark', 'light', 'semi'];
  const currentIndex = themes.indexOf(auth.theme);
  const next = themes[(currentIndex + 1) % themes.length];
  auth.setTheme(next);
}

const themeIcon = computed(() => {
  switch (auth.theme) {
    case 'light': return '☀️';
    case 'semi': return '🌓';
    default: return '🌙';
  }
});

const themeLabel = computed(() => {
  switch (auth.theme) {
    case 'light': return 'Light';
    case 'semi': return 'Semi';
    default: return 'Dark';
  }
});

const userInitial = computed(() => auth.user?.firstName?.[0]?.toUpperCase() || '?');
const userAvatar = computed(() => auth.user?.avatarUrl || null);

onMounted(() => {
  refreshCartCount();
  auth.applyTheme();
  window.addEventListener('scroll', handleScroll);
  window.addEventListener('click', handleClickOutside);
});
onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll);
  window.removeEventListener('click', handleClickOutside);
});
</script>

<template>
  <nav class="navbar" :class="{ scrolled }">
    <div class="container nav-content">
      <!-- LEFT: Logo -->
      <RouterLink to="/" class="logo">
        <span class="logo-icon">⚡</span>
        <span class="logo-text">TechW</span>
      </RouterLink>

      <!-- CENTER: Nav Links -->
      <div class="nav-center">
        <RouterLink to="/products" class="nav-link">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/></svg>
          Products
        </RouterLink>

        <RouterLink v-if="auth.isAuthenticated" to="/orders" class="nav-link">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01"/></svg>
          My Orders
        </RouterLink>

        <RouterLink v-if="auth.isAdmin" to="/admin" class="nav-link admin-link">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9.594 3.94c.09-.542.56-.94 1.11-.94h2.593c.55 0 1.02.398 1.11.94l.213 1.281c.063.374.313.686.645.87.074.04.147.083.22.127.324.196.72.257 1.075.124l1.217-.456a1.125 1.125 0 011.37.49l1.296 2.247a1.125 1.125 0 01-.26 1.431l-1.003.827c-.293.24-.438.613-.431.992a6.759 6.759 0 010 .255c-.007.378.138.75.43.99l1.005.828c.424.35.534.954.26 1.43l-1.298 2.247a1.125 1.125 0 01-1.369.491l-1.217-.456c-.355-.133-.75-.072-1.076.124a6.57 6.57 0 01-.22.128c-.331.183-.581.495-.644.869l-.213 1.28c-.09.543-.56.941-1.11.941h-2.594c-.55 0-1.02-.398-1.11-.94l-.213-1.281c-.062-.374-.312-.686-.644-.87a6.52 6.52 0 01-.22-.127c-.325-.196-.72-.257-1.076-.124l-1.217.456a1.125 1.125 0 01-1.369-.49l-1.297-2.247a1.125 1.125 0 01.26-1.431l1.004-.827c.292-.24.437-.613.43-.992a6.932 6.932 0 010-.255c.007-.378-.138-.75-.43-.99l-1.004-.828a1.125 1.125 0 01-.26-1.43l1.297-2.247a1.125 1.125 0 011.37-.491l1.216.456c.356.133.751.072 1.076-.124.072-.044.146-.087.22-.128.332-.183.582-.495.644-.869l.214-1.281z"/><path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/></svg>
          Dashboard
        </RouterLink>
      </div>

      <!-- RIGHT: Theme + Cart + Profile/Login -->
      <div class="nav-right">
        <!-- Theme Toggle (always visible) -->
        <button class="theme-toggle-btn" @click="cycleTheme" :title="`Theme: ${themeLabel}`">
          <span class="theme-emoji">{{ themeIcon }}</span>
        </button>

        <RouterLink to="/cart" class="nav-cart" v-if="auth.isAuthenticated">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2 9m13-9l2 9M9 21a1 1 0 100-2 1 1 0 000 2zm10 0a1 1 0 100-2 1 1 0 000 2z"/>
          </svg>
          <span v-if="cartCount > 0" class="cart-badge">{{ cartCount }}</span>
        </RouterLink>

        <!-- Profile Avatar with Dropdown -->
        <template v-if="auth.isAuthenticated">
          <div class="profile-dropdown-area">
            <button class="nav-avatar-btn" @click.stop="toggleProfileMenu" title="My Account">
              <img v-if="userAvatar" :src="userAvatar" alt="Avatar" class="avatar-img-nav" />
              <span v-else class="avatar-circle">{{ userInitial }}</span>
            </button>

            <Transition name="dropdown">
              <div v-if="showProfileMenu" class="profile-dropdown">
                <div class="dropdown-header">
                  <img v-if="userAvatar" :src="userAvatar" alt="Avatar" class="dropdown-avatar-img" />
                  <span v-else class="dropdown-avatar-circle">{{ userInitial }}</span>
                  <div class="dropdown-user-info">
                    <span class="dropdown-user-name">{{ auth.user?.firstName || 'User' }}</span>
                    <span class="dropdown-user-email">{{ auth.user?.email }}</span>
                  </div>
                </div>
                <div class="dropdown-divider"></div>
                <button class="dropdown-item" @click="navigateProfile('/profile')">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/></svg>
                  My Profile
                </button>
                <button class="dropdown-item" @click="navigateProfile('/orders')">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/></svg>
                  My Orders
                </button>
                <button v-if="auth.isAdmin" class="dropdown-item" @click="navigateProfile('/admin')">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.066 2.573c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.573 1.066c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.066-2.573c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"/><path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/></svg>
                  Settings
                </button>
                <div class="dropdown-divider"></div>
                <button class="dropdown-item logout-item" @click="handleLogout">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/></svg>
                  Logout
                </button>
              </div>
            </Transition>
          </div>
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
  position: sticky; top: 0; z-index: 100; height: 68px;
  background: rgba(8, 11, 20, 0.8);
  backdrop-filter: blur(16px); -webkit-backdrop-filter: blur(16px);
  border-bottom: 1px solid var(--border);
  display: flex; align-items: center;
  transition: background 0.3s, border-color 0.3s;
}
.navbar.scrolled {
  background: rgba(8, 11, 20, 0.95);
  border-bottom-color: var(--border-strong);
  box-shadow: 0 4px 20px rgba(0,0,0,0.4);
}

.nav-content { display: flex; justify-content: space-between; align-items: center; }

/* Logo (left) */
.logo { display: flex; align-items: center; gap: 0.5rem; font-size: 1.35rem; font-weight: 800; letter-spacing: -0.03em; flex-shrink: 0; }
.logo-icon { font-size: 1.4rem; }
.logo-text { background: linear-gradient(135deg, #818cf8, #6366f1); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }

/* Center links */
.nav-center { display: flex; align-items: center; gap: 0.25rem; }
.nav-link {
  display: flex; align-items: center; gap: 0.4rem;
  padding: 0.45rem 0.85rem; border-radius: var(--radius-sm);
  font-size: 0.9rem; font-weight: 500; color: var(--text-muted); transition: all 0.2s;
}
.nav-link:hover, .nav-link.router-link-active { color: var(--text-light); background: var(--surface-2, rgba(255,255,255,0.06)); }

/* Right section */
.nav-right { display: flex; align-items: center; gap: 0.5rem; flex-shrink: 0; }

/* Theme Toggle */
.theme-toggle-btn {
  width: 36px; height: 36px; border-radius: 50%;
  background: var(--surface-2); border: 1px solid var(--border-strong);
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: all 0.2s; font-size: 1rem;
}
.theme-toggle-btn:hover { border-color: var(--primary); transform: scale(1.1); }

/* Cart */
.nav-cart {
  display: flex; align-items: center; justify-content: center;
  width: 36px; height: 36px; border-radius: 50%;
  color: var(--text-muted); position: relative; transition: all 0.2s;
  background: transparent; border: 1px solid transparent;
}
.nav-cart:hover, .nav-cart.router-link-active { color: var(--primary); background: var(--primary-light); }
.cart-badge {
  position: absolute; top: -2px; right: -4px;
  background: var(--primary); color: white; font-size: 0.6rem; font-weight: 800;
  width: 16px; height: 16px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  border: 2px solid var(--bg-dark);
}

/* Profile Dropdown */
.profile-dropdown-area { position: relative; }
.nav-avatar-btn {
  width: 36px; height: 36px; border-radius: 50%; overflow: hidden;
  background: transparent; border: 2px solid transparent; cursor: pointer;
  display: flex; align-items: center; justify-content: center; padding: 0;
  transition: all 0.2s;
}
.nav-avatar-btn:hover { border-color: var(--primary); transform: scale(1.08); }
.avatar-img-nav { width: 100%; height: 100%; object-fit: cover; border-radius: 50%; }
.avatar-circle {
  width: 100%; height: 100%; border-radius: 50%;
  background: linear-gradient(135deg, var(--primary), var(--primary-hover));
  color: white; font-weight: 800; font-size: 0.85rem;
  display: flex; align-items: center; justify-content: center;
}

/* Dropdown Panel */
.profile-dropdown {
  position: absolute; top: calc(100% + 10px); right: 0;
  width: 260px; background: var(--surface); border: 1px solid var(--border-strong);
  border-radius: var(--radius-md); box-shadow: 0 12px 40px rgba(0,0,0,0.5);
  z-index: 200; overflow: hidden;
}
.dropdown-header {
  display: flex; align-items: center; gap: 0.75rem;
  padding: 1rem 1.15rem; background: rgba(99,102,241,0.05);
}
.dropdown-avatar-img { width: 38px; height: 38px; border-radius: 50%; object-fit: cover; border: 2px solid var(--primary); flex-shrink: 0; }
.dropdown-avatar-circle {
  width: 38px; height: 38px; border-radius: 50%; flex-shrink: 0;
  background: linear-gradient(135deg, var(--primary), var(--primary-hover));
  color: white; font-weight: 800; font-size: 0.9rem;
  display: flex; align-items: center; justify-content: center;
}
.dropdown-user-info { display: flex; flex-direction: column; min-width: 0; }
.dropdown-user-name { font-weight: 700; font-size: 0.95rem; color: var(--text-light); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.dropdown-user-email { font-size: 0.78rem; color: var(--text-muted); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.dropdown-divider { height: 1px; background: var(--border); }

.dropdown-item {
  display: flex; align-items: center; gap: 0.65rem; width: 100%;
  padding: 0.7rem 1.15rem; background: transparent; border: none;
  color: var(--text-muted); font-size: 0.88rem; font-weight: 500;
  cursor: pointer; transition: all 0.15s; text-align: left; font-family: inherit;
}
.dropdown-item:hover { background: var(--surface-2); color: var(--text-light); }
.dropdown-item.logout-item { color: #f87171; }
.dropdown-item.logout-item:hover { background: rgba(239, 68, 68, 0.1); }

/* Dropdown animation */
.dropdown-enter-active { animation: dropdownIn 0.2s ease-out; }
.dropdown-leave-active { animation: dropdownIn 0.15s ease-in reverse; }
@keyframes dropdownIn {
  from { opacity: 0; transform: translateY(-8px) scale(0.96); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.nav-login { margin-left: 0.25rem; padding: 0.5rem 1.2rem; font-size: 0.85rem; }

main { min-height: calc(100vh - 68px - 72px); }

.footer { height: 72px; background: var(--surface); border-top: 1px solid var(--border); display: flex; align-items: center; }
.footer-content { display: flex; justify-content: space-between; align-items: center; }
.footer-brand { display: flex; align-items: center; gap: 0.5rem; font-weight: 700; font-size: 0.95rem; color: var(--text-muted); }
.footer-copy { font-size: 0.85rem; color: var(--text-dim); }

@media (max-width: 768px) {
  .nav-center { display: none; }
}
</style>
