import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 8998,
    proxy: {
      '/admin/api': {
        target: 'http://localhost:8999',
        changeOrigin: true
      },
      '/api': {
        target: 'http://localhost:8999',
        changeOrigin: true
      }
    }
  },
  build: {
    outDir: 'dist'
  }
})
