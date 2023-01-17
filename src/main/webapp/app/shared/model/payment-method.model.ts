import { PaymentMethodType } from 'app/shared/model/enumerations/payment-method-type.model';

export interface IPaymentMethod {
  id?: number;
  name?: string;
  type?: PaymentMethodType;
}

export const defaultValue: Readonly<IPaymentMethod> = {};
