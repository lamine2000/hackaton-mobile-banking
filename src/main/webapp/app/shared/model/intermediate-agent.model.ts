import { IUser } from 'app/shared/model/user.model';
import { IStore } from 'app/shared/model/store.model';
import { AccountStatus } from 'app/shared/model/enumerations/account-status.model';

export interface IIntermediateAgent {
  id?: number;
  firstName?: string;
  lastName?: string;
  email?: string;
  phone?: string;
  addressLine1?: string;
  addressLine2?: string | null;
  city?: string;
  status?: AccountStatus;
  commissionRate?: number;
  user?: IUser | null;
  store?: IStore | null;
}

export const defaultValue: Readonly<IIntermediateAgent> = {};
