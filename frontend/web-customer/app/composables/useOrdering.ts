export interface MenuItem {
  id: string;
  name: string;
  description: string;
  price: number;
  badge?: string;
}

export interface CategoryItem {
  id: string;
  label: string;
  icon: string;
}

export interface StoreItem {
  id: string;
  name: string;
  category: string;
  eta: string;
  rating: number;
  reviewCount: number;
  deliveryFee: string;
  minOrder: string;
  heroIcon: string;
  tags: string[];
  bestseller: string;
  description: string;
  promo: string;
  menus: MenuItem[];
}

export interface ReorderItem {
  id: string;
  storeId: string;
  storeName: string;
  menu: string;
  orderedAt: string;
  totalPrice: string;
}

export interface CartItem {
  id: string;
  storeId: string;
  storeName: string;
  menuId: string;
  menu: string;
  quantity: number;
  price: number;
}

export const formatWon = (price: number) => {
  return new Intl.NumberFormat('ko-KR', {
    style: 'currency',
    currency: 'KRW',
    maximumFractionDigits: 0,
  }).format(price);
};

const quickActions = [
  '30분 내 도착',
  '1인분 가능',
  '배달비 무료',
  '리뷰 이벤트',
];

const categories: CategoryItem[] = [
  { id: 'all', label: '전체', icon: 'i-lucide-layout-grid' },
  { id: 'chicken', label: '치킨', icon: 'i-lucide-drumstick' },
  { id: 'korean', label: '한식', icon: 'i-lucide-soup' },
  { id: 'snack', label: '분식', icon: 'i-lucide-flame' },
  { id: 'japanese', label: '일식', icon: 'i-lucide-fish' },
  { id: 'dessert', label: '디저트', icon: 'i-lucide-ice-cream-cone' },
];

interface BackendStoreItem {
  id: string;
  name: string;
  category: string;
  minOrderAmount: number | null;
  ratingAvg: number | null;
  description: string | null;
  status: string | null;
  eta?: string | null;
  reviewCount?: number | null;
  deliveryFee?: string | null;
  heroIcon?: string | null;
  tags?: string[] | null;
  bestseller?: string | null;
  promo?: string | null;
}

interface BackendMenuItem {
  id: string;
  storeId: string;
  name: string;
  description: string;
  price: number;
  available: boolean;
}

interface BackendOrderItem {
  id: string;
  storeId: string;
  totalAmount: number;
  status: string;
  createdAt: string;
  items: Array<{
    productId: string;
    quantity: number;
    price: number;
  }>;
}

interface CartApiItem {
  id: string;
  userId: string;
  storeId: string;
  storeName: string;
  menuId: string;
  menuName: string;
  quantity: number;
  price: number;
}

export const useOrdering = () => {
  const { $api } = useApi();
  const stores = useState<StoreItem[]>('ordering-stores', () => []);
  const storesLoaded = useState('ordering-stores-loaded', () => false);
  const reorderItems = useState<ReorderItem[]>('ordering-reorder-items', () => []);
  const reorderLoaded = useState('ordering-reorder-loaded', () => false);
  const cartItems = useState<CartItem[]>('ordering-cart-items', () => []);
  const cartLoaded = useState('ordering-cart-loaded', () => false);

  const cartTotal = computed(() => {
    return cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0);
  });

  const normalizeMenu = (menu: BackendMenuItem, index: number): MenuItem => {
    return {
      id: menu.id,
      name: menu.name,
      description: menu.description,
      price: menu.price,
      badge: index === 0 ? 'BEST' : undefined,
    };
  };

  const normalizeStore = (store: BackendStoreItem, menus: BackendMenuItem[]): StoreItem => {
    const mappedMenus = menus.map(normalizeMenu);
    const category = store.category || '기타';

    return {
      id: store.id,
      name: store.name,
      category,
      eta: store.eta || '30~40분',
      rating: Number(store.ratingAvg ?? 0),
      reviewCount: Number(store.reviewCount ?? 0),
      deliveryFee: store.deliveryFee || '2,000원',
      minOrder: formatWon(Number(store.minOrderAmount ?? 0)),
      heroIcon: store.heroIcon || 'i-lucide-store',
      tags: store.tags && store.tags.length > 0 ? store.tags : ['주문 가능'],
      bestseller: store.bestseller || mappedMenus[0]?.name || '대표 메뉴 준비 중',
      description: store.description || '현재 등록된 매장 소개가 없습니다.',
      promo: store.promo || '현재 준비된 프로모션이 없습니다.',
      menus: mappedMenus,
    };
  };

  const upsertStore = (nextStore: StoreItem) => {
    const index = stores.value.findIndex(store => store.id === nextStore.id);
    if (index === -1) {
      stores.value = [...stores.value, nextStore];
      return nextStore;
    }

    const nextStores = [...stores.value];
    nextStores[index] = nextStore;
    stores.value = nextStores;
    return nextStore;
  };

  const loadStores = async (force = false) => {
    if (storesLoaded.value && !force) {
      return stores.value;
    }

    const storeRes = await $api<BackendStoreItem[]>('/api/v1/stores');
    if (!storeRes.success || !storeRes.data) {
      stores.value = [];
      storesLoaded.value = true;
      return stores.value;
    }

    const menuEntries = await Promise.all(
      storeRes.data.map(async (store) => {
        const menuRes = await $api<BackendMenuItem[]>(`/api/v1/stores/${store.id}/menus`);
        return {
          store,
          menus: menuRes.success && menuRes.data ? menuRes.data : [],
        };
      }),
    );

    stores.value = menuEntries.map(({ store, menus }) => normalizeStore(store, menus));
    storesLoaded.value = true;
    return stores.value;
  };

  const loadStoreById = async (storeId: string, force = false) => {
    if (!force) {
      const existing = stores.value.find(store => store.id === storeId);
      if (existing && existing.menus.length > 0) {
        return existing;
      }
    }

    const storeRes = await $api<BackendStoreItem>(`/api/v1/stores/${storeId}`);
    if (!storeRes.success || !storeRes.data) {
      return null;
    }

    const menuRes = await $api<BackendMenuItem[]>(`/api/v1/stores/${storeId}/menus`);
    const normalized = normalizeStore(storeRes.data, menuRes.success && menuRes.data ? menuRes.data : []);
    return upsertStore(normalized);
  };

  const loadReorderItems = async (force = false) => {
    if (reorderLoaded.value && !force) {
      return reorderItems.value;
    }

    await loadStores();

    const orderRes = await $api<BackendOrderItem[]>('/api/v1/orders/my');
    if (!orderRes.success || !orderRes.data) {
      reorderItems.value = [];
      reorderLoaded.value = true;
      return reorderItems.value;
    }

    reorderItems.value = orderRes.data.slice(0, 3).map((order) => {
      const store = stores.value.find(item => item.id === order.storeId);
      return {
        id: order.id,
        storeId: order.storeId,
        storeName: store?.name ?? '매장 정보 없음',
        menu: order.items.length > 1 ? `메뉴 ${order.items.length}건` : '메뉴 1건',
        orderedAt: new Date(order.createdAt).toLocaleString('ko-KR', {
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit',
        }),
        totalPrice: formatWon(order.totalAmount),
      };
    });
    reorderLoaded.value = true;
    return reorderItems.value;
  };

  const loadCart = async (force = false) => {
    if (cartLoaded.value && !force) {
      return;
    }

    const res = await $api<CartApiItem[]>('/api/v1/users/me/cart');
    if (!res.success || !res.data) {
      cartItems.value = [];
      cartLoaded.value = true;
      return;
    }

    cartItems.value = res.data.map(item => ({
      id: item.id,
      storeId: item.storeId,
      storeName: item.storeName,
      menuId: item.menuId,
      menu: item.menuName,
      quantity: item.quantity,
      price: item.price,
    }));
    cartLoaded.value = true;
  };

  const addMenuToCart = async (store: StoreItem, menu: MenuItem) => {
    const existing = cartItems.value.find(
      item => item.storeId === String(store.id) && item.menuId === String(menu.id),
    );

    const nextQuantity = existing ? existing.quantity + 1 : 1;
    const res = await $api<CartApiItem>('/api/v1/users/me/cart', {
      method: 'POST',
      body: {
        storeId: String(store.id),
        storeName: store.name,
        menuId: String(menu.id),
        menuName: menu.name,
        quantity: existing ? 1 : nextQuantity,
        price: menu.price,
      },
    });

    if (res.success && res.data) {
      await loadCart(true);
      return cartItems.value.find(item => item.id === res.data.id) ?? null;
    }

    return null;
  };

  const addBestSellerToCart = async (storeId: string) => {
    const store = stores.value.find(entry => entry.id === storeId);
    if (!store) {
      return null;
    }

    const menu = store.menus[0];
    if (!menu) {
      return null;
    }

    await addMenuToCart(store, menu);
    return { store, menu };
  };

  const removeCartItem = async (cartItemId: string) => {
    await $api(`/api/v1/users/me/cart/${cartItemId}`, {
      method: 'DELETE',
    });
    cartItems.value = cartItems.value.filter(item => item.id !== cartItemId);
  };

  const clearCart = async () => {
    await $api('/api/v1/users/me/cart', {
      method: 'DELETE',
    });
    cartItems.value = [];
  };

  const getStoreById = (storeId: string) => {
    return stores.value.find(store => store.id === storeId);
  };

  return {
    quickActions,
    categories,
    stores,
    loadStores,
    loadStoreById,
    reorderItems,
    loadReorderItems,
    cartItems,
    loadCart,
    cartTotal,
    addMenuToCart,
    addBestSellerToCart,
    removeCartItem,
    clearCart,
    getStoreById,
  };
};
