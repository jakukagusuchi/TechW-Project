<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const auth = useAuthStore();
const router = useRouter();

const form = ref({
  email: '',
  password: '',
  firstName: '',
  lastName: '',
});

const error = ref('');

async function handleRegister() {
  const success = await auth.register(form.value);
  if (success) {
    router.push('/');
  } else {
    error.value = auth.error;
  }
}
</script>

<template>
  <div class="auth-container">
    <div class="auth-card">
      <h2>Create Account</h2>
      <p>Join TechW Store and start shopping today.</p>

      <form @submit.prevent="handleRegister" class="auth-form">
        <div class="name-row">
          <div class="form-group">
            <label>First Name</label>
            <input v-model="form.firstName" type="text" placeholder="John" required />
          </div>
          <div class="form-group">
            <label>Last Name</label>
            <input v-model="form.lastName" type="text" placeholder="Doe" required />
          </div>
        </div>

        <div class="form-group">
          <label>Email Address</label>
          <input v-model="form.email" type="email" placeholder="you@example.com" required />
        </div>
        
        <div class="form-group">
          <label>Password</label>
          <input v-model="form.password" type="password" placeholder="••••••••" required />
        </div>

        <p v-if="error" class="error-msg">{{ error }}</p>

        <button type="submit" class="btn-primary w-full" :disabled="auth.loading">
          {{ auth.loading ? 'Creating Account...' : 'Register' }}
        </button>
      </form>

      <p class="switch-auth">
        Already have an account? <RouterLink to="/login">Log in here</RouterLink>
      </p>
    </div>
  </div>
</template>

<style scoped>
/* Reuse styles from LoginView or move to global */
.auth-container { display: flex; justify-content: center; align-items: center; padding: 4rem 0; }
.auth-card { background-color: var(--surface); padding: 3rem; border-radius: 16px; border: 1px solid var(--border); width: 100%; max-width: 500px; }
h2 { font-size: 2rem; margin-bottom: 0.5rem; }
p { color: var(--text-muted); margin-bottom: 2rem; }
.auth-form { display: flex; flex-direction: column; gap: 1.5rem; }
.form-group { display: flex; flex-direction: column; gap: 0.5rem; }
.name-row { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
label { font-size: 0.9rem; font-weight: 600; }
input { padding: 0.75rem; background-color: var(--bg-dark); border: 1px solid var(--border); border-radius: 8px; color: white; outline: none; }
input:focus { border-color: var(--primary); }
.error-msg { color: #ef4444; font-size: 0.9rem; margin-top: -1rem; }
.w-full { width: 100%; }
.switch-auth { margin-top: 2rem; text-align: center; font-size: 0.95rem; }
.switch-auth a { color: var(--primary); font-weight: 600; }
</style>
