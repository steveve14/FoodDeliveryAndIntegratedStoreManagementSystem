import type { User } from '~/types'

const customers: User[] = [{
  id: 1,
  name: '김민수',
  email: 'minsu.kim@example.com',
  avatar: {
    src: 'https://i.pravatar.cc/128?u=1'
  },
  status: 'subscribed',
  location: '서울특별시 강남구'
}, {
  id: 2,
  name: '이서연',
  email: 'seoyeon.lee@example.com',
  avatar: {
    src: 'https://i.pravatar.cc/128?u=2'
  },
  status: 'unsubscribed',
  location: '부산광역시 해운대구'
}, {
  id: 3,
  name: '박지훈',
  email: 'jihoon.park@example.com',
  avatar: {
    src: 'https://i.pravatar.cc/128?u=3'
  },
  status: 'bounced',
  location: '대구광역시 수성구'
}, {
  id: 4,
  name: '최유진',
  email: 'yujin.choi@example.com',
  avatar: {
    src: 'https://i.pravatar.cc/128?u=4'
  },
  status: 'subscribed',
  location: '인천광역시 연수구'
}, {
  id: 5,
  name: '정하늘',
  email: 'haneul.jung@example.com',
  avatar: {
    src: 'https://i.pravatar.cc/128?u=5'
  },
  status: 'subscribed',
  location: '대전광역시 유성구'
}, {
  id: 6,
  name: '강수빈',
  email: 'subin.kang@example.com',
  avatar: {
    src: 'https://i.pravatar.cc/128?u=6'
  },
  status: 'subscribed',
  location: '광주광역시 서구'
}, {
  id: 7,
  name: '윤도현',
  email: 'dohyun.yoon@example.com',
  avatar: {
    src: 'https://i.pravatar.cc/128?u=7'
  },
  status: 'subscribed',
  location: '서울특별시 마포구'
}, {
  id: 8,
  name: '한소희',
  email: 'sohee.han@example.com',
  avatar: {
    src: 'https://i.pravatar.cc/128?u=8'
  },
  status: 'subscribed',
  location: '경기도 성남시'
}, {
  id: 9,
  name: '송태윤',
  email: 'taeyun.song@example.com',
  avatar: {
    src: 'https://i.pravatar.cc/128?u=9'
  },
  status: 'bounced',
  location: '울산광역시 남구'
}, {
  id: 10,
  name: '임채원',
  email: 'chaewon.lim@example.com',
  avatar: {
    src: 'https://i.pravatar.cc/128?u=10'
  },
  status: 'subscribed',
  location: '세종특별자치시'
}, {
  id: 11,
  name: '오승우',
  email: 'seungwoo.oh@example.com',
  avatar: {
    src: 'https://i.pravatar.cc/128?u=11'
  },
  status: 'subscribed',
  location: '경기도 수원시'
}, {
  id: 12,
  name: '배지현',
  email: 'jihyun.bae@example.com',
  avatar: {
    src: 'https://i.pravatar.cc/128?u=12'
  },
  status: 'unsubscribed',
  location: '제주특별자치도 제주시'
}, {
  id: 13,
  name: '권혁준',
  email: 'hyukjun.kwon@example.com',
  avatar: {
    src: 'https://i.pravatar.cc/128?u=13'
  },
  status: 'unsubscribed',
  location: '서울특별시 송파구'
}, {
  id: 14,
  name: '신예린',
  email: 'yerin.shin@example.com',
  avatar: {
    src: 'https://i.pravatar.cc/128?u=14'
  },
  status: 'unsubscribed',
  location: '경기도 고양시'
}, {
  id: 15,
  name: '조현우',
  email: 'hyunwoo.jo@example.com',
  avatar: {
    src: 'https://i.pravatar.cc/128?u=15'
  },
  status: 'subscribed',
  location: '충청북도 청주시'
}, {
  id: 16,
  name: '유서윤',
  email: 'seoyun.yu@example.com',
  avatar: {
    src: 'https://i.pravatar.cc/128?u=16'
  },
  status: 'subscribed',
  location: '전라북도 전주시'
}, {
  id: 17,
  name: '문준호',
  email: 'junho.moon@example.com',
  avatar: {
    src: 'https://i.pravatar.cc/128?u=17'
  },
  status: 'unsubscribed',
  location: '강원도 춘천시'
}, {
  id: 18,
  name: '남지아',
  email: 'jia.nam@example.com',
  avatar: {
    src: 'https://i.pravatar.cc/128?u=18'
  },
  status: 'subscribed',
  location: '경상남도 창원시'
}, {
  id: 19,
  name: '서은호',
  email: 'eunho.seo@example.com',
  avatar: {
    src: 'https://i.pravatar.cc/128?u=19'
  },
  status: 'bounced',
  location: '서울특별시 종로구'
}, {
  id: 20,
  name: '황다은',
  email: 'daeun.hwang@example.com',
  avatar: {
    src: 'https://i.pravatar.cc/128?u=20'
  },
  status: 'subscribed',
  location: '경기도 용인시'
}]

export default eventHandler(async () => {
  return customers
})
