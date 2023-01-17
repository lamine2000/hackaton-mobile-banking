import { IFunctionality } from 'app/shared/model/functionality.model';
import { SupplyRequestStatus } from 'app/shared/model/enumerations/supply-request-status.model';

export interface ISupplyRequest {
  id?: number;
  amount?: number | null;
  quantity?: number | null;
  status?: SupplyRequestStatus;
  functionality?: IFunctionality | null;
}

export const defaultValue: Readonly<ISupplyRequest> = {};
