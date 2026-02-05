<script setup lang="ts">
const state = reactive<{ [key: string]: boolean }>({
  email: true,
  desktop: false,
  product_updates: true,
  weekly_digest: false,
  important_updates: true
})

// UI 텍스트 한글화
const sections = [{
  title: '알림 수신 채널',
  description: '알림을 받을 경로를 설정합니다.',
  fields: [{
    name: 'email',
    label: '이메일',
    description: '일일 요약 리포트를 이메일로 수신합니다.'
  }, {
    name: 'desktop',
    label: '데스크톱 알림',
    description: '브라우저 데스크톱 푸시 알림을 수신합니다.'
  }]
}]

async function onChange() {
  // 변경 사항 처리 로직 (API 호출 등)
  console.log(state)
}
</script>

<template>
  <div v-for="(section, index) in sections" :key="index">
    <UPageCard
      :title="section.title"
      :description="section.description"
      variant="naked"
      class="mb-4"
    />

    <UPageCard variant="subtle" :ui="{ container: 'divide-y divide-default' }">
      <UFormField
        v-for="field in section.fields"
        :key="field.name"
        :name="field.name"
        :label="field.label"
        :description="field.description"
        class="flex items-center justify-between not-last:pb-4 gap-2"
      >
        <USwitch
          v-model="state[field.name]"
          @update:model-value="onChange"
        />
      </UFormField>
    </UPageCard>
  </div>
</template>