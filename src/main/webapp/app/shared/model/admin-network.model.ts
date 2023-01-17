import { IUser } from 'app/shared/model/user.model';
import { AccountStatus } from 'app/shared/model/enumerations/account-status.model';

export interface IAdminNetwork {
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
}

export const defaultValue: Readonly<IAdminNetwork> = {};
