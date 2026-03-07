import { sub } from 'date-fns'

const notifications = [{
  id: 1,
  unread: true,
  sender: {
    name: '이서연',
    email: 'seoyeon.lee@example.com',
    avatar: {
      src: 'https://i.pravatar.cc/128?u=2'
    }
  },
  body: '메시지를 보냈습니다',
  date: sub(new Date(), { minutes: 7 }).toISOString()
}, {
  id: 2,
  sender: {
    name: '한소희'
  },
  body: '이메일 수신을 구독했습니다',
  date: sub(new Date(), { hours: 1 }).toISOString()
}, {
  id: 3,
  unread: true,
  sender: {
    name: '박지훈',
    email: 'jihoon.park@example.com',
    avatar: {
      src: 'https://i.pravatar.cc/128?u=3'
    }
  },
  body: '메시지를 보냈습니다',
  date: sub(new Date(), { hours: 3 }).toISOString()
}, {
  id: 4,
  sender: {
    name: '최유진',
    avatar: {
      src: 'https://i.pravatar.cc/128?u=4'
    }
  },
  body: '프로젝트에 추가했습니다',
  date: sub(new Date(), { hours: 3 }).toISOString()
}, {
  id: 5,
  sender: {
    name: '정하늘',
    avatar: {
      src: 'https://i.pravatar.cc/128?u=5'
    }
  },
  body: '장바구니를 이탈했습니다',
  date: sub(new Date(), { hours: 7 }).toISOString()
}, {
  id: 6,
  sender: {
    name: '배지현',
    avatar: {
      src: 'https://i.pravatar.cc/128?u=6'
    }
  },
  body: '상품을 구매했습니다',
  date: sub(new Date(), { days: 1, hours: 3 }).toISOString()
}, {
  id: 7,
  unread: true,
  sender: {
    name: '한소희',
    email: 'sohee.han@example.com',
    avatar: {
      src: 'https://i.pravatar.cc/128?u=8'
    }
  },
  body: '메시지를 보냈습니다',
  date: sub(new Date(), { days: 2 }).toISOString()
}, {
  id: 8,
  sender: {
    name: '강수빈',
    email: 'subin.kang@example.com',
    avatar: {
      src: 'https://i.pravatar.cc/128?u=9'
    }
  },
  body: '환불을 요청했습니다',
  date: sub(new Date(), { days: 5, hours: 4 }).toISOString()
}, {
  id: 9,
  unread: true,
  sender: {
    name: '오승우',
    email: 'seungwoo.oh@example.com'
  },
  body: '메시지를 보냈습니다',
  date: sub(new Date(), { days: 6 }).toISOString()
}, {
  id: 10,
  sender: {
    name: '송태윤'
  },
  body: '이메일 수신을 구독했습니다',
  date: sub(new Date(), { days: 6 }).toISOString()
}, {
  id: 11,
  sender: {
    name: '윤도현'
  },
  body: '장바구니를 이탈했습니다',
  date: sub(new Date(), { days: 7 }).toISOString()
}, {
  id: 12,
  sender: {
    name: '임채원'
  },
  body: '이메일 수신을 구독했습니다',
  date: sub(new Date(), { days: 9 }).toISOString()
}, {
  id: 13,
  sender: {
    name: '한소희',
    email: 'sohee.han@example.com',
    avatar: {
      src: 'https://i.pravatar.cc/128?u=8'
    }
  },
  body: '이메일 수신을 구독했습니다',
  date: sub(new Date(), { days: 10 }).toISOString()
}, {
  id: 14,
  sender: {
    name: '강수빈',
    email: 'subin.kang@example.com',
    avatar: {
      src: 'https://i.pravatar.cc/128?u=9'
    }
  },
  body: '이메일 수신을 구독했습니다',
  date: sub(new Date(), { days: 11 }).toISOString()
}, {
  id: 15,
  sender: {
    name: '오승우'
  },
  body: '상품을 구매했습니다',
  date: sub(new Date(), { days: 12 }).toISOString()
}, {
  id: 16,
  sender: {
    name: '송태윤',
    avatar: {
      src: 'https://i.pravatar.cc/128?u=16'
    }
  },
  body: '이메일 수신을 구독했습니다',
  date: sub(new Date(), { days: 13 }).toISOString()
}, {
  id: 17,
  sender: {
    name: '윤도현'
  },
  body: '이메일 수신을 구독했습니다',
  date: sub(new Date(), { days: 14 }).toISOString()
}, {
  id: 18,
  sender: {
    name: '임채원'
  },
  body: '이메일 수신을 구독했습니다',
  date: sub(new Date(), { days: 15 }).toISOString()
}, {
  id: 19,
  sender: {
    name: '한소희',
    email: 'sohee.han@example.com',
    avatar: {
      src: 'https://i.pravatar.cc/128?u=8'
    }
  },
  body: '이메일 수신을 구독했습니다',
  date: sub(new Date(), { days: 16 }).toISOString()
}, {
  id: 20,
  sender: {
    name: '강수빈',
    email: 'subin.kang@example.com',
    avatar: {
      src: 'https://i.pravatar.cc/128?u=9'
    }
  },
  body: '상품을 구매했습니다',
  date: sub(new Date(), { days: 17 }).toISOString()
}, {
  id: 21,
  sender: {
    name: '오승우'
  },
  body: '장바구니를 이탈했습니다',
  date: sub(new Date(), { days: 17 }).toISOString()
}, {
  id: 22,
  sender: {
    name: '송태윤'
  },
  body: '이메일 수신을 구독했습니다',
  date: sub(new Date(), { days: 18 }).toISOString()
}, {
  id: 23,
  sender: {
    name: '윤도현'
  },
  body: '이메일 수신을 구독했습니다',
  date: sub(new Date(), { days: 19 }).toISOString()
}, {
  id: 24,
  sender: {
    name: '임채원',
    avatar: {
      src: 'https://i.pravatar.cc/128?u=24'
    }
  },
  body: '이메일 수신을 구독했습니다',
  date: sub(new Date(), { days: 20 }).toISOString()
}, {
  id: 25,
  sender: {
    name: '한소희',
    email: 'sohee.han@example.com',
    avatar: {
      src: 'https://i.pravatar.cc/128?u=8'
    }
  },
  body: '이메일 수신을 구독했습니다',
  date: sub(new Date(), { days: 20 }).toISOString()
}, {
  id: 26,
  sender: {
    name: '강수빈',
    email: 'subin.kang@example.com',
    avatar: {
      src: 'https://i.pravatar.cc/128?u=9'
    }
  },
  body: '장바구니를 이탈했습니다',
  date: sub(new Date(), { days: 21 }).toISOString()
}, {
  id: 27,
  sender: {
    name: '오승우'
  },
  body: '이메일 수신을 구독했습니다',
  date: sub(new Date(), { days: 22 }).toISOString()
}]

export default eventHandler(async () => {
  return notifications
})
