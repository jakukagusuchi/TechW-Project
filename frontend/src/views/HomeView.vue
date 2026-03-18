<template>
  <div>
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-glow"></div>
      <div class="container hero-content">
        <div class="hero-badge">✨ Premium Tech Store</div>
        <h1 class="hero-title">The Future of Tech<br><span class="gradient-text">In Your Hands</span></h1>
        <p class="hero-subtitle">Discover premium electronics, laptops, and accessories crafted for modern developers and tech enthusiasts.</p>
        <div class="hero-actions">
          <RouterLink to="/products" class="btn-primary hero-btn">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M13 7l5 5m0 0l-5 5m5-5H6"/></svg>
            Shop Now
          </RouterLink>
          <RouterLink to="/products" class="btn-secondary">Browse Catalog</RouterLink>
        </div>
        <!-- <div class="hero-stats">
          <div class="stat"><span class="stat-value">500+</span><span class="stat-label">Products</span></div>
          <div class="stat-divider"></div>
          <div class="stat"><span class="stat-value">50K+</span><span class="stat-label">Customers</span></div>
          <div class="stat-divider"></div>
          <div class="stat"><span class="stat-value">99%</span><span class="stat-label">Satisfaction</span></div>
        </div> -->
      </div>
    </section>

    <!-- Features Section -->
    <section class="features-section">
      <div class="container">
        <div class="features-grid">
          <div class="feature-card" v-for="f in features" :key="f.title">
            <div class="feature-icon">{{ f.icon }}</div>
            <h3>{{ f.title }}</h3>
            <p>{{ f.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="cta-section">
      <div class="container">
        <div class="cta-card">
          <h2>Ready to upgrade your tech?</h2>
          <p>Join thousands of developers who trust TechW for their premium electronics.</p>
          <RouterLink to="/products" class="btn-primary">Start Shopping →</RouterLink>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
const features = [
  { icon: '🚀', title: 'Lightning Fast Shipping', desc: 'Get your orders delivered within 2-3 business days with our premium shipping partners.' },
  { icon: '🛡️', title: '2-Year Warranty', desc: 'All products come with a comprehensive 2-year warranty and dedicated support.' },
  { icon: '💳', title: 'Secure Payments', desc: 'Shop with confidence using our bank-grade encrypted payment processing.' },
  { icon: '🔄', title: '30-Day Returns', desc: 'Easy no-questions-asked returns within 30 days of delivery.' },
];
</script>

<style scoped>
/* ─── Hero ─── */
.hero-section {
  position: relative;
  overflow: hidden;
  padding: 6rem 0 5rem;
  min-height: 85vh;
  display: flex;
  align-items: center;
}
.hero-glow {
  position: absolute;
  width: 800px;
  height: 800px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.15) 0%, transparent 70%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -55%);
  pointer-events: none;
}

.hero-content {
  position: relative;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.5rem;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.4rem 1rem;
  background: var(--primary-light);
  border: 1px solid rgba(99, 102, 241, 0.3);
  border-radius: 99px;
  font-size: 0.8rem;
  font-weight: 700;
  color: #818cf8;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.hero-title {
  font-size: clamp(2.8rem, 7vw, 5.5rem);
  font-weight: 900;
  line-height: 1.05;
  letter-spacing: -0.04em;
  color: var(--text-light);
  max-width: 800px;
}
.gradient-text {
  background: linear-gradient(135deg, #818cf8, #a78bfa, #38bdf8);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 1.15rem;
  color: var(--text-muted);
  max-width: 560px;
  line-height: 1.7;
}

.hero-actions {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
  justify-content: center;
  margin-top: 0.5rem;
}
.hero-btn { padding: 0.85rem 2rem; font-size: 1rem; }

.hero-stats {
  display: flex;
  align-items: center;
  gap: 2rem;
  margin-top: 2rem;
  padding: 1.25rem 2.5rem;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 99px;
}
.stat { display: flex; flex-direction: column; align-items: center; gap: 0.1rem; }
.stat-value { font-size: 1.4rem; font-weight: 800; color: var(--primary); }
.stat-label { font-size: 0.75rem; color: var(--text-muted); font-weight: 500; }
.stat-divider { width: 1px; height: 36px; background: var(--border); }

/* ─── Features ─── */
.features-section {
  padding: 5rem 0;
  border-top: 1px solid var(--border);
}
.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1.5rem;
}
.feature-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  padding: 2rem 1.75rem;
  transition: all 0.3s;
}
.feature-card:hover {
  border-color: var(--primary);
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(99, 102, 241, 0.15);
}
.feature-icon { font-size: 2.5rem; margin-bottom: 1rem; }
.feature-card h3 { font-size: 1.05rem; font-weight: 700; margin-bottom: 0.5rem; }
.feature-card p { font-size: 0.9rem; color: var(--text-muted); line-height: 1.6; }

/* ─── CTA ─── */
.cta-section { padding: 0 0 6rem; }
.cta-card {
  background: linear-gradient(135deg, rgba(99,102,241,0.15) 0%, rgba(167,139,250,0.1) 100%);
  border: 1px solid rgba(99, 102, 241, 0.25);
  border-radius: var(--radius-xl);
  padding: 4rem;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}
.cta-card h2 { font-size: 2.2rem; font-weight: 800; letter-spacing: -0.03em; }
.cta-card p { color: var(--text-muted); font-size: 1rem; max-width: 400px; }

@media (max-width: 640px) {
  .hero-stats { gap: 1rem; padding: 1rem 1.5rem; flex-wrap: wrap; }
  .cta-card { padding: 2.5rem 1.5rem; }
  .cta-card h2 { font-size: 1.75rem; }
}
</style>
