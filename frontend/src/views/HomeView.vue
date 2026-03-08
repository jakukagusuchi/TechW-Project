<template>
  <div class="container">
    <div class="hero">
      <h1>Premium Electronics for the Modern Developer</h1>
      <p>Discover the latest laptops, smartphones, and accessories.</p>
      <RouterLink to="/products" class="btn-primary">Shop Now</RouterLink>
    </div>
  </div>
</template>

<style scoped>
.hero {
  text-align: center;
  padding: 6rem 0;
  background: radial-gradient(circle at center, #1e293b 0%, #0f172a 100%);
  border-radius: 24px;
  border: 1px solid var(--border);
}

h1 {
  font-size: 3.5rem;
  margin-bottom: 1.5rem;
  background: linear-gradient(to right, #60a5fa, #2563eb);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

p {
  font-size: 1.25rem;
  color: var(--text-muted);
  margin-bottom: 3rem;
}
</style>
