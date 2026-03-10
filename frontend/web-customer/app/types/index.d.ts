export type Period = 'daily' | 'weekly' | 'monthly';

export interface Avatar {
  src: string;
  alt?: string;
}

export type CustomerStatus = 'subscribed' | 'unsubscribed' | 'bounced';

export interface User {
  id: string;
  name: string;
  email: string;
  avatar: Avatar;
  status: CustomerStatus;
  location: string;
}

export type MemberRole = 'member' | 'owner';

export interface Member {
  name: string;
  username: string;
  role: MemberRole;
  avatar: Avatar;
}

export interface Range {
  start: Date;
  end: Date;
}

export interface Stat {
  title: string;
  icon: string;
  value: number | string;
  variation: number;
  link?: string;
  formatter?: (value: number) => string;
}

export interface Sale {
  id: string;
  date: string;
  status: SaleStatus;
  email: string;
  amount: number;
}

export interface Notification {
  id: number;
  unread?: boolean;
  sender: User;
  body: string;
  date: string;
}
