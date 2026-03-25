import { defineStore } from 'pinia';
import api from '../services/api';

const THEME_VARS = {
    dark: {
        '--bg-dark': '#080b14',
        '--bg-card': '#0f1623',
        '--surface': '#141c2e',
        '--surface-2': '#1a2440',
        '--border': 'rgba(255,255,255,0.07)',
        '--border-strong': 'rgba(255,255,255,0.14)',
        '--text-light': '#f0f4ff',
        '--text-muted': '#7a8aa5',
        '--text-dim': '#4a5568',
    },
    light: {
        '--bg-dark': '#f8fafc',
        '--bg-card': '#ffffff',
        '--surface': '#ffffff',
        '--surface-2': '#f1f5f9',
        '--border': 'rgba(0,0,0,0.08)',
        '--border-strong': 'rgba(0,0,0,0.15)',
        '--text-light': '#0f172a',
        '--text-muted': '#64748b',
        '--text-dim': '#94a3b8',
    },
    semi: {
        '--bg-dark': '#111827',
        '--bg-card': '#1f2937',
        '--surface': '#1f2937',
        '--surface-2': '#374151',
        '--border': 'rgba(255,255,255,0.08)',
        '--border-strong': 'rgba(255,255,255,0.16)',
        '--text-light': '#f9fafb',
        '--text-muted': '#9ca3af',
        '--text-dim': '#6b7280',
    },
};

export const useAuthStore = defineStore('auth', {
    state: () => ({
        user: JSON.parse(localStorage.getItem('user')) || null,
        token: localStorage.getItem('token') || null,
        theme: localStorage.getItem('theme') || 'dark',
        loading: false,
        error: null,
    }),
    getters: {
        isAuthenticated: (state) => !!state.token,
        isAdmin: (state) => state.user?.role === 'ADMIN',
    },
    actions: {
        async login(credentials) {
            this.loading = true;
            this.error = null;
            try {
                const { data } = await api.post('/api/auth/login', credentials);
                this.token = data.token;
                this.user = {
                    email: data.email,
                    role: data.role,
                    firstName: data.firstName,
                };
                localStorage.setItem('token', data.token);
                localStorage.setItem('user', JSON.stringify(this.user));
                return true;
            } catch (err) {
                this.error = err.response?.data?.message || 'Login failed';
                return false;
            } finally {
                this.loading = false;
            }
        },
        async register(userData) {
            this.loading = true;
            this.error = null;
            try {
                const { data } = await api.post('/api/auth/register', userData);
                this.token = data.token;
                this.user = {
                    email: data.email,
                    role: data.role,
                    firstName: data.firstName,
                };
                localStorage.setItem('token', data.token);
                localStorage.setItem('user', JSON.stringify(this.user));
                return true;
            } catch (err) {
                this.error = err.response?.data?.message || 'Registration failed';
                return false;
            } finally {
                this.loading = false;
            }
        },
        logout() {
            this.user = null;
            this.token = null;
            localStorage.removeItem('token');
            localStorage.removeItem('user');
        },
        setTheme(theme) {
            this.theme = theme;
            localStorage.setItem('theme', theme);
            this.applyTheme();
        },
        applyTheme() {
            const vars = THEME_VARS[this.theme] || THEME_VARS.dark;
            const root = document.documentElement;
            Object.entries(vars).forEach(([key, value]) => {
                root.style.setProperty(key, value);
            });
            root.setAttribute('data-theme', this.theme);
        },
    },
});
