// eslint.config.mjs
export default withNuxt({
  rules: {
    'quotes': ['error', 'single', { avoidEscape: true }],
    '@stylistic/quotes': ['error', 'single', { avoidEscape: true }],
    'semi': ['error', 'always'],
    '@stylistic/semi': ['error', 'always'],
    'indent': ['error', 2],
    '@stylistic/indent': ['error', 2]
  }
});
