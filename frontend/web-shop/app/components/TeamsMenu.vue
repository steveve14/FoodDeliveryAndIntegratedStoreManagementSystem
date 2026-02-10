<script setup lang="ts">
defineProps<{
  collapsed?: boolean
}>()
</script>

<template>
  <UButton
    :label="collapsed ? undefined : 'FoodDelivery'"
    color="neutral"
    variant="ghost"
    block
    :square="collapsed"
    class="data-[state=open]:bg-elevated"
    :class="[!collapsed && 'py-2']"
  >
    <UAvatar
      src="/Logo.svg"
      alt="admin"
      size="3xl"
    />
    <span
      v-if="!collapsed"
      class="ml-2 font-semibold text-lg"
    >FoodDelivery</span>
  </UButton>
</template>
