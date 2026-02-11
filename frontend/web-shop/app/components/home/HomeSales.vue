<script setup lang="ts">
import { h, resolveComponent } from "vue";
import type { TableColumn } from "@nuxt/ui";
import type { Period, Range } from "~/types";

const props = defineProps<{
  period: Period;
  range: Range;
}>();

const UBadge = resolveComponent("UBadge");

// 더미 메뉴 데이터
const sampleMenus = [
  "황금 올리브 치킨",
  "양념 반 후라이드 반",
  "블랙 페퍼 윙봉",
  "치즈볼 세트 A",
  "콜라 1.25L 포함 세트",
];

// 주문 상태 타입 정의
type OrderStatus = "접수" | "조리중" | "배달중" | "완료" | "취소";

type Order = {
  id: string;
  date: string;
  status: OrderStatus;
  menu: string;
  amount: number;
};

const { data } = await useAsyncData(
  "home-sales",
  async () => {
    const sales: Order[] = [];
    const currentDate = new Date();

    for (let i = 0; i < 5; i++) {
      const minutesAgo = Math.floor(Math.random() * 120); // 최근 2시간 내
      const date = new Date(currentDate.getTime() - minutesAgo * 60000);

      // 상태 랜덤 배정
      const statuses = ["접수", "조리중", "배달중", "완료"];

      sales.push({
        id: (240215001 + i).toString(), // 주문번호 형식
        date: date.toISOString(),
        status: statuses[
          Math.floor(Math.random() * statuses.length)
        ] as OrderStatus,
        menu: sampleMenus[
          Math.floor(Math.random() * sampleMenus.length)
        ] as string,
        amount:
          Math.floor(Math.random() * 3 + 1) * 10000 +
          (Math.random() > 0.5 ? 5000 : 0), // 1.5만 ~ 3.5만
      });
    }

    return sales.sort(
      (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime(),
    );
  },
  {
    watch: [() => props.period, () => props.range],
    default: () => [],
  },
);

const columns: TableColumn<any>[] = [
  {
    accessorKey: "id",
    header: "주문번호",
    cell: ({ row }) => `#${row.getValue("id")}`,
  },
  {
    accessorKey: "date",
    header: "접수 시간",
    cell: ({ row }) => {
      return new Date(row.getValue("date")).toLocaleTimeString("ko-KR", {
        hour: "2-digit",
        minute: "2-digit",
        hour12: false,
      });
    },
  },
  {
    accessorKey: "menu",
    header: "주문 메뉴",
    // 텍스트 색상을 지정하지 않아 기본(foreground) 색상을 따름
    cell: ({ row }) =>
      h("span", { class: "font-medium" }, row.getValue("menu")),
  },
  {
    accessorKey: "status",
    header: "상태",
    cell: ({ row }) => {
      const status = row.getValue("status") as OrderStatus;

      // 상태에 따른 Semantic Color 매핑 (전역 테마에 정의된 키값만 사용)
      const color = {
        접수: "error",
        조리중: "warning",
        배달중: "primary",
        완료: "success",
        취소: "neutral",
      }[status] as any;

      return h(
        UBadge,
        { class: "font-bold", variant: "subtle", color },
        () => status,
      );
    },
  },
  {
    accessorKey: "amount",
    header: () => h("div", { class: "text-right" }, "결제 금액"),
    cell: ({ row }) => {
      const amount = Number(row.getValue("amount"));
      const formatted = new Intl.NumberFormat("ko-KR", {
        style: "currency",
        currency: "KRW",
      }).format(amount);
      return h("div", { class: "text-right font-medium" }, formatted);
    },
  },
];
</script>

<template>
  <UTable
    :data="data"
    :columns="columns"
    class="shrink-0"
    label="최근 주문 내역"
    :ui="{
      base: 'table-fixed border-separate border-spacing-0',
      thead: '[&>tr]:bg-elevated/50 [&>tr]:after:content-none',
      tbody: '[&>tr]:last:[&>td]:border-b-0',
      th: 'first:rounded-l-lg last:rounded-r-lg border-y border-default first:border-l last:border-r',
      td: 'border-b border-default',
    }"
  />
</template>
