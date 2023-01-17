import { IEvent } from 'app/shared/model/event.model';
import { IPayment } from 'app/shared/model/payment.model';
import { ITicketDelivery } from 'app/shared/model/ticket-delivery.model';
import { TicketStatus } from 'app/shared/model/enumerations/ticket-status.model';

export interface ITicket {
  id?: number;
  code?: string | null;
  dataContentType?: string;
  data?: string;
  pricePerUnit?: number;
  finalAgentCommission?: number | null;
  status?: TicketStatus;
  event?: IEvent | null;
  payment?: IPayment | null;
  ticketDelivery?: ITicketDelivery | null;
}

export const defaultValue: Readonly<ITicket> = {};
