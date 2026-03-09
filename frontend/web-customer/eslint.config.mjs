import withNuxt from './.nuxt/eslint.config.mjs';

const googleStyleRules = {
  'quotes': 'off',
  '@stylistic/quotes': ['error', 'single', { avoidEscape: true }],
  'semi': 'off',
  '@stylistic/semi': ['error', 'always'],
  indent: 'off',
  '@stylistic/indent': ['error', 2, { SwitchCase: 1 }],
  'comma-dangle': 'off',
  '@stylistic/comma-dangle': ['error', 'always-multiline'],
  '@stylistic/member-delimiter-style': ['error', {
    multiline: { delimiter: 'semi', requireLast: true },
    singleline: { delimiter: 'semi', requireLast: false },
    multilineDetection: 'brackets',
  }],
  'object-curly-spacing': 'off',
  '@stylistic/object-curly-spacing': ['error', 'always'],
  'array-bracket-spacing': 'off',
  '@stylistic/array-bracket-spacing': ['error', 'never'],
  'brace-style': 'off',
  '@stylistic/brace-style': ['error', '1tbs', { allowSingleLine: true }],
  'keyword-spacing': 'off',
  '@stylistic/keyword-spacing': ['error', { before: true, after: true }],
  'operator-linebreak': 'off',
  '@stylistic/operator-linebreak': ['error', 'after'],
  'arrow-parens': 'off',
  '@stylistic/arrow-parens': ['error', 'as-needed', { requireForBlockBody: true }],
  'space-before-function-paren': 'off',
  '@stylistic/space-before-function-paren': ['error', 'always'],
  'curly': ['error', 'all'],
  'eqeqeq': ['error', 'smart'],
  'camelcase': ['error', { properties: 'never' }],
  'vue/max-attributes-per-line': 'off',
  'vue/singleline-html-element-content-newline': 'off',
  'vue/html-closing-bracket-newline': 'off',
  'vue/html-indent': 'off',
  'vue/comma-dangle': 'off',
};

export default withNuxt({
  rules: {
    ...googleStyleRules,
  },
});
