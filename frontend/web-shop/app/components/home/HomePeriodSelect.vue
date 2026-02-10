<script setup lang="ts">
import { eachDayOfInterval } from 'date-fns'
import type { Period, Range } from '~/types'

const model = defineModel<Period>({ required: true })

const props = defineProps<{
  range: Range
}>()

// range가 유효하지 않을 때 에러를 막기 위한 방어 코드 추가
const days = computed(() => {
  try {
    return eachDayOfInterval(props.range)
  } catch (e) {
    return [] // 날짜 계산 실패 시 빈 배열 반환
  }
})

// UI용 라벨과 실제 값을 분리
const periods = computed(() => {
  const options = []
  
  if (days.value.length === 0) return [] // 날짜가 없으면 빈 옵션 반환

  if (days.value.length <= 8) {
    options.push({ label: '일간', value: 'daily' })
  } else if (days.value.length <= 31) {
    options.push({ label: '일간', value: 'daily' })
    options.push({ label: '주간', value: 'weekly' })
  } else {
    options.push({ label: '주간', value: 'weekly' })
    options.push({ label: '월간', value: 'monthly' })
  }
  
  return options as { label: string, value: Period }[]
})

watch(periods, (newPeriods) => {
  if (!newPeriods || newPeriods.length === 0) return

  const isValueValid = newPeriods.some(p => p.value === model.value)

  if (!isValueValid) {
    // [0] 뒤에 !를 붙여서 undefined가 아님을 강제합니다.
    model.value = newPeriods[0]!.value 
  }
}, { immediate: true })

</script>

<template>
  <USelect
    v-model="model"
    :items="periods"
    option-attribute="label"
    value-attribute="value"
    variant="ghost"
    class="data-[state=open]:bg-elevated"
    :ui="{ 
      trailingIcon: 'group-data-[state=open]:rotate-180 transition-transform duration-200' 
    }"
  />
</template>