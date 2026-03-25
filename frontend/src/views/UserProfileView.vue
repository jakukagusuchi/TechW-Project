<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import api from '../services/api';

const router = useRouter();
const authStore = useAuthStore();

const profile = ref(null);
const loading = ref(true);
const saving = ref(false);
const error = ref(null);
const success = ref(null);

const editForm = ref({
  firstName: '',
  lastName: '',
  avatarUrl: ''
});

const avatarPreview = ref(null);

const currentTheme = computed(() => authStore.theme || 'dark');

async function fetchProfile() {
  loading.value = true;
  try {
    const { data } = await api.get('/api/users/me');
    profile.value = data;
    editForm.value.firstName = data.firstName || '';
    editForm.value.lastName = data.lastName || '';
    editForm.value.avatarUrl = data.avatarUrl || '';
    avatarPreview.value = data.avatarUrl || null;
  } catch (err) {
    error.value = 'Failed to load profile.';
  } finally {
    loading.value = false;
  }
}

function onAvatarUpload(event) {
  const file = event.target.files[0];
  if (!file) return;
  if (!file.type.startsWith('image/')) return;
  if (file.size > 500 * 1024) {
    error.value = 'Avatar image must be under 500KB.';
    return;
  }
  const reader = new FileReader();
  reader.onload = (e) => {
    editForm.value.avatarUrl = e.target.result;
    avatarPreview.value = e.target.result;
  };
  reader.readAsDataURL(file);
}

function removeAvatar() {
  editForm.value.avatarUrl = '';
  avatarPreview.value = null;
}

async function saveProfile() {
  saving.value = true;
  error.value = null;
  success.value = null;
  try {
    const { data } = await api.put('/api/users/me', {
      firstName: editForm.value.firstName,
      lastName: editForm.value.lastName,
      avatarUrl: editForm.value.avatarUrl
    });
    profile.value = data;
    // Update auth store
    authStore.user.firstName = data.firstName;
    authStore.user.avatarUrl = data.avatarUrl;
    localStorage.setItem('user', JSON.stringify(authStore.user));
    success.value = 'Profile updated successfully!';
    setTimeout(() => success.value = null, 3000);
  } catch (err) {
    error.value = err.response?.data?.message || 'Failed to update profile.';
  } finally {
    saving.value = false;
  }
}

function setTheme(theme) {
  authStore.setTheme(theme);
}

function handleLogout() {
  authStore.logout();
  router.push('/');
}

const userInitials = computed(() => {
  const f = editForm.value.firstName?.[0] || '';
  const l = editForm.value.lastName?.[0] || '';
  return (f + l).toUpperCase() || '?';
});

onMounted(() => fetchProfile());
</script>

<template>
  <div class="container profile-container">
    <h1 class="profile-title">My Profile</h1>

    <div v-if="loading" class="state-box">
      <div class="spinner"></div>
      <p>Loading profile...</p>
    </div>

    <div v-else class="profile-grid">
      <!-- Avatar Section -->
      <div class="profile-card avatar-card">
        <div class="avatar-area">
          <div class="avatar-wrapper" @click="$refs.avatarInput.click()">
            <img v-if="avatarPreview" :src="avatarPreview" alt="Avatar" class="avatar-img" />
            <span v-else class="avatar-initials">{{ userInitials }}</span>
            <div class="avatar-overlay">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z"/><path stroke-linecap="round" stroke-linejoin="round" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z"/></svg>
            </div>
          </div>
          <input ref="avatarInput" type="file" accept="image/*" style="display:none" @change="onAvatarUpload" />
          <button v-if="avatarPreview" @click="removeAvatar" class="remove-avatar-btn">Remove Avatar</button>
        </div>
        <div class="avatar-info">
          <h2 class="user-display-name">{{ editForm.firstName }} {{ editForm.lastName }}</h2>
          <p class="user-email">{{ profile?.email }}</p>
          <span class="role-badge" :class="profile?.role === 'ADMIN' ? 'admin' : 'user'">
            {{ profile?.role }}
          </span>
        </div>
      </div>

      <!-- Account Info -->
      <div class="profile-card info-card">
        <h3 class="card-title">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/></svg>
          Account Information
        </h3>

        <div v-if="error" class="alert alert-error">{{ error }}</div>
        <div v-if="success" class="alert alert-success">{{ success }}</div>

        <form @submit.prevent="saveProfile" class="profile-form">
          <div class="form-row">
            <div class="form-group">
              <label for="firstName">First Name</label>
              <input type="text" id="firstName" class="form-input" v-model="editForm.firstName" placeholder="First name" />
            </div>
            <div class="form-group">
              <label for="lastName">Last Name</label>
              <input type="text" id="lastName" class="form-input" v-model="editForm.lastName" placeholder="Last name" />
            </div>
          </div>
          <div class="form-group">
            <label>Email</label>
            <input type="email" class="form-input disabled" :value="profile?.email" disabled />
          </div>
          <button type="submit" class="btn-primary save-btn" :disabled="saving">
            <span v-if="saving">Saving...</span>
            <span v-else>
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7"/></svg>
              Save Changes
            </span>
          </button>
        </form>
      </div>

      <!-- Theme Preferences -->
      <div class="profile-card theme-card">
        <h3 class="card-title">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M7 21a4 4 0 01-4-4V5a2 2 0 012-2h4a2 2 0 012 2v12a4 4 0 01-4 4zm0 0h12a2 2 0 002-2v-4a2 2 0 00-2-2h-2.343M11 7.343l1.657-1.657a2 2 0 012.828 0l2.829 2.829a2 2 0 010 2.828l-8.486 8.485M7 17h.01"/></svg>
          Appearance
        </h3>
        <div class="theme-options">
          <button :class="['theme-btn', { active: currentTheme === 'dark' }]" @click="setTheme('dark')">
            <div class="theme-preview dark-preview">
              <div class="theme-bar"></div>
              <div class="theme-lines"><div></div><div></div></div>
            </div>
            <span>Dark</span>
          </button>
          <button :class="['theme-btn', { active: currentTheme === 'light' }]" @click="setTheme('light')">
            <div class="theme-preview light-preview">
              <div class="theme-bar"></div>
              <div class="theme-lines"><div></div><div></div></div>
            </div>
            <span>Light</span>
          </button>
          <button :class="['theme-btn', { active: currentTheme === 'semi' }]" @click="setTheme('semi')">
            <div class="theme-preview semi-preview">
              <div class="theme-bar"></div>
              <div class="theme-lines"><div></div><div></div></div>
            </div>
            <span>Semi-Dark</span>
          </button>
        </div>
      </div>

      <!-- Logout -->
      <div class="profile-card logout-card">
        <button @click="handleLogout" class="btn-danger logout-btn">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/></svg>
          Sign Out
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.profile-container { padding-top: 2rem; padding-bottom: 5rem; max-width: 880px; margin: 0 auto; }
.profile-title { font-size: 2.25rem; font-weight: 800; letter-spacing: -0.02em; margin-bottom: 2rem; }

.state-box {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 6rem 0; text-align: center;
  background: var(--surface); border-radius: var(--radius-lg); border: 1px dashed var(--border-strong);
  color: var(--text-muted);
}
.spinner { width: 40px; height: 40px; border: 3px solid var(--border-strong); border-top-color: var(--primary); border-radius: 50%; animation: spin 0.8s linear infinite; margin-bottom: 1rem; }
@keyframes spin { to { transform: rotate(360deg); } }

.profile-grid { display: flex; flex-direction: column; gap: 1.5rem; }

.profile-card {
  background: var(--surface); padding: 2rem; border-radius: var(--radius-lg);
  border: 1px solid var(--border); box-shadow: var(--shadow-sm);
}

/* Avatar Card */
.avatar-card { display: flex; align-items: center; gap: 2rem; }
.avatar-area { display: flex; flex-direction: column; align-items: center; gap: 0.75rem; }
.avatar-wrapper {
  width: 100px; height: 100px; border-radius: 50%; overflow: hidden;
  border: 3px solid var(--primary); cursor: pointer; position: relative;
  display: flex; align-items: center; justify-content: center;
  background: var(--surface-2); transition: all 0.3s;
}
.avatar-wrapper:hover { border-color: var(--primary-hover); transform: scale(1.05); }
.avatar-img { width: 100%; height: 100%; object-fit: cover; }
.avatar-initials { font-size: 2rem; font-weight: 800; color: var(--primary); }
.avatar-overlay {
  position: absolute; inset: 0; background: rgba(0,0,0,0.5);
  display: flex; align-items: center; justify-content: center;
  opacity: 0; transition: opacity 0.2s; color: white;
}
.avatar-wrapper:hover .avatar-overlay { opacity: 1; }
.remove-avatar-btn {
  background: transparent; color: var(--danger); font-size: 0.78rem;
  font-weight: 600; border: none; padding: 0;
}
.remove-avatar-btn:hover { text-decoration: underline; }

.avatar-info { flex: 1; }
.user-display-name { font-size: 1.5rem; font-weight: 800; margin-bottom: 0.25rem; }
.user-email { color: var(--text-muted); font-size: 0.95rem; margin-bottom: 0.5rem; }
.role-badge {
  display: inline-block; padding: 0.2rem 0.7rem; border-radius: 99px;
  font-size: 0.72rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.06em;
}
.role-badge.admin { background: rgba(99,102,241,0.15); color: var(--primary); }
.role-badge.user { background: rgba(16,185,129,0.15); color: var(--success); }

/* Card Title */
.card-title {
  display: flex; align-items: center; gap: 0.6rem;
  font-size: 1.15rem; font-weight: 700; margin-bottom: 1.5rem; color: var(--text-light);
}

/* Alerts */
.alert { padding: 0.75rem 1rem; border-radius: var(--radius-sm); margin-bottom: 1.25rem; font-size: 0.9rem; font-weight: 500; }
.alert-error { background: rgba(239,68,68,0.1); color: #fca5a5; border: 1px solid rgba(239,68,68,0.25); }
.alert-success { background: rgba(16,185,129,0.1); color: #34d399; border: 1px solid rgba(16,185,129,0.25); }

/* Form */
.profile-form { display: flex; flex-direction: column; gap: 1.25rem; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
.form-group { display: flex; flex-direction: column; gap: 0.5rem; }
.form-group label { font-weight: 600; font-size: 0.88rem; color: var(--text-muted); }
.form-input.disabled { opacity: 0.5; cursor: not-allowed; }
.save-btn { align-self: flex-start; }

/* Theme */
.theme-options { display: grid; grid-template-columns: repeat(3, 1fr); gap: 1rem; }
.theme-btn {
  display: flex; flex-direction: column; align-items: center; gap: 0.75rem;
  background: var(--bg-card); border: 2px solid var(--border-strong);
  border-radius: var(--radius-md); padding: 1.25rem 1rem; cursor: pointer; transition: all 0.2s;
  font-weight: 600; font-size: 0.88rem; color: var(--text-muted);
}
.theme-btn:hover { border-color: var(--text-dim); }
.theme-btn.active { border-color: var(--primary); color: var(--text-light); box-shadow: 0 0 0 1px var(--primary); }

.theme-preview {
  width: 100%; height: 56px; border-radius: 8px; overflow: hidden; padding: 8px;
  display: flex; flex-direction: column; gap: 6px;
}
.dark-preview { background: #0a0e1a; }
.light-preview { background: #f1f5f9; }
.semi-preview { background: #1e293b; }

.theme-bar { height: 8px; border-radius: 4px; width: 60%; }
.dark-preview .theme-bar { background: #6366f1; }
.light-preview .theme-bar { background: #6366f1; }
.semi-preview .theme-bar { background: #818cf8; }

.theme-lines { display: flex; flex-direction: column; gap: 4px; }
.theme-lines div { height: 4px; border-radius: 2px; }
.theme-lines div:first-child { width: 100%; }
.theme-lines div:last-child { width: 70%; }
.dark-preview .theme-lines div { background: #1e293b; }
.light-preview .theme-lines div { background: #cbd5e1; }
.semi-preview .theme-lines div { background: #334155; }

/* Logout */
.logout-card { display: flex; justify-content: center; }
.logout-btn { width: 100%; justify-content: center; padding: 1rem; font-size: 1rem; }

@media (max-width: 600px) {
  .avatar-card { flex-direction: column; text-align: center; }
  .form-row { grid-template-columns: 1fr; }
  .theme-options { grid-template-columns: 1fr; }
}
</style>
