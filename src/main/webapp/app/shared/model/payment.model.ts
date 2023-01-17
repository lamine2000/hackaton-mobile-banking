import { ITransac } from 'app/shared/model/transac.model';
import { IPaymentMethod } from 'app/shared/model/payment-method.model';

export interface IPayment {
  id?: number;
  transac?: ITransac | null;
  paymentMethod?: IPaymentMethod | null;
}

export const defaultValue: Readonly<IPayment> = {};
