import dayjs from 'dayjs';
import { CurrencyCode } from 'app/shared/model/enumerations/currency-code.model';
import { TransacType } from 'app/shared/model/enumerations/transac-type.model';

export interface ITransac {
  id?: number;
  code?: string;
  createdBy?: string;
  createdAt?: string;
  receiver?: string | null;
  sender?: string | null;
  amount?: number;
  currency?: CurrencyCode;
  type?: TransacType;
}

export const defaultValue: Readonly<ITransac> = {};
