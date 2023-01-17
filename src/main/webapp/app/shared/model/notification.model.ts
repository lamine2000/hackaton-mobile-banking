import dayjs from 'dayjs';

export interface INotification {
  id?: number;
  title?: string;
  message?: string;
  createdAt?: string;
  sentTo?: string;
  sentBy?: string;
}

export const defaultValue: Readonly<INotification> = {};
