<script setup lang="ts">
withDefaults(defineProps<{
  count?: number
}>(), {
  count: 0
})

const open = ref(false)

async function onSubmit() {
  await new Promise(resolve => setTimeout(resolve, 1000))
  open.value = false
}
</script>

<template>
  <UModal
    v-model:open="open"
    :title="`고객 ${count} ${count > 1 ? '명' : '명'} 삭제`"
    :description="`이작업은 되돌릴 수 없습니다. 정말로 ${count}명의 고객을 삭제하시겠습니까?`"
  >
    <slot />

    <template #body>
      <div class="flex justify-end gap-2">
        <UButton
          label="취소"
          color="neutral"
          variant="subtle"
          @click="open = false"
        />
        <UButton
          label="삭제"
          color="error"
          variant="solid"
          loading-auto
          @click="onSubmit"
        />
      </div>
    </template>
  </UModal>
</template>
