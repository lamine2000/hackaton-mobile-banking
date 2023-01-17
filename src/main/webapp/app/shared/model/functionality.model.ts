import { IFunctionalityCategory } from 'app/shared/model/functionality-category.model';
import { IMobileBankingActor } from 'app/shared/model/mobile-banking-actor.model';
import { FunctionalityStatus } from 'app/shared/model/enumerations/functionality-status.model';

export interface IFunctionality {
  id?: number;
  imageContentType?: string;
  image?: string;
  status?: FunctionalityStatus;
  functionalityCategory?: IFunctionalityCategory | null;
  mobileBankingActors?: IMobileBankingActor[] | null;
}

export const defaultValue: Readonly<IFunctionality> = {};
