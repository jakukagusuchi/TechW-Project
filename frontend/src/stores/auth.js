import { defineStore } from 'pinia';
import api from '../services/api';

export const useAuthStore = defineStore('auth', {
    state: () => ({
        user: JSON.parse(localStorage.getItem('user')) || null,
        token: localStorage.getItem('token') || null,
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
    },
});
