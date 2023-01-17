import { IFunctionality } from 'app/shared/model/functionality.model';
import { MobileBankingActorStatus } from 'app/shared/model/enumerations/mobile-banking-actor-status.model';

export interface IMobileBankingActor {
  id?: number;
  logoContentType?: string;
  logo?: string;
  name?: string;
  status?: MobileBankingActorStatus;
  functionalities?: IFunctionality[] | null;
}

export const defaultValue: Readonly<IMobileBankingActor> = {};
