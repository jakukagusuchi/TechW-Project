import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import { useAuthStore } from '../stores/auth';

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: HomeView,
        },
        {
            path: '/login',
            name: 'login',
            component: () => import('../views/LoginView.vue'),
        },
        {
            path: '/register',
            name: 'register',
            component: () => import('../views/RegisterView.vue'),
        },
        {
            path: '/products',
            name: 'products',
            component: () => import('../views/ProductsView.vue'),
        },
        {
            path: '/products/:id',
            name: 'product-detail',
            component: () => import('../views/ProductDetailView.vue'),
        },
        {
            path: '/cart',
            name: 'cart',
            component: () => import('../views/CartView.vue'),
        },
        {
            path: '/checkout',
            name: 'checkout',
            component: () => import('../views/CheckoutView.vue'),
            meta: { requiresAuth: true },
        },
        {
            path: '/orders',
            name: 'orders',
            component: () => import('../views/OrderHistoryView.vue'),
            meta: { requiresAuth: true },
        },
        {
            path: '/admin',
            name: 'admin',
            component: () => import('../views/AdminDashboardView.vue'),
            meta: { requiresAuth: true, requiresAdmin: true },
        },
    ],
});

router.beforeEach((to, from, next) => {
    const auth = useAuthStore();
    if (to.meta.requiresAuth && !auth.isAuthenticated) {
        next('/login');
    } else if (to.meta.requiresAdmin && !auth.isAdmin) {
        next('/');
    } else {
        next();
    }
});

export default router;
