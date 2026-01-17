// @ts-check
import withNuxt from './.nuxt/eslint.config.mjs'
import googleConfig from 'eslint-config-google'

export default withNuxt(
  // 1. 구글 설정을 별도의 객체로 전달 (타입 단언 사용)
  /** @type {any} */ (googleConfig), 
  
  // 2. 사용자의 커스텀 설정
  {
    rules: {
      'vue/no-multiple-template-root': 'off',
      'vue/max-attributes-per-line': ['error', { singleline: 3 }],
      'valid-jsdoc': 'off',
      'require-jsdoc': 'off',
      'indent': ['error', 2],
    }
  }
)