import { FunctionalityCategoryStatus } from 'app/shared/model/enumerations/functionality-category-status.model';

export interface IFunctionalityCategory {
  id?: number;
  logoContentType?: string;
  logo?: string;
  status?: FunctionalityCategoryStatus;
}

export const defaultValue: Readonly<IFunctionalityCategory> = {};
