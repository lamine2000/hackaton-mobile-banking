import dayjs from 'dayjs';
import { EventStatus } from 'app/shared/model/enumerations/event-status.model';

export interface IEvent {
  id?: number;
  date?: string | null;
  title?: string;
  description?: string | null;
  createdAt?: string;
  createdBy?: string;
  organizer?: string;
  expireAt?: string;
  status?: EventStatus;
}

export const defaultValue: Readonly<IEvent> = {};
