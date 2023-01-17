import { ISupplyRequest } from 'app/shared/model/supply-request.model';

export interface ISupply {
  id?: number;
  receiver?: string;
  supplyRequest?: ISupplyRequest | null;
}

export const defaultValue: Readonly<ISupply> = {};
