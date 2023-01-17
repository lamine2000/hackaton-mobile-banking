import dayjs from 'dayjs';
import { ITicketDeliveryMethod } from 'app/shared/model/ticket-delivery-method.model';

export interface ITicketDelivery {
  id?: number;
  boughtAt?: string;
  boughtBy?: string | null;
  quantity?: number;
  ticketDeliveryMethod?: ITicketDeliveryMethod | null;
}

export const defaultValue: Readonly<ITicketDelivery> = {};
