import { IZone } from 'app/shared/model/zone.model';
import { ITown } from 'app/shared/model/town.model';
import { IDepartment } from 'app/shared/model/department.model';
import { IRegion } from 'app/shared/model/region.model';
import { ICountry } from 'app/shared/model/country.model';
import { CurrencyCode } from 'app/shared/model/enumerations/currency-code.model';
import { StoreStatus } from 'app/shared/model/enumerations/store-status.model';

export interface IStore {
  id?: number;
  code?: string;
  locationContentType?: string;
  location?: string;
  address?: string | null;
  name?: string;
  description?: string | null;
  currency?: CurrencyCode | null;
  phone?: string;
  notificationEmail?: string;
  status?: StoreStatus | null;
  aboutUs?: string | null;
  zone?: IZone | null;
  town?: ITown | null;
  department?: IDepartment | null;
  region?: IRegion | null;
  country?: ICountry | null;
}

export const defaultValue: Readonly<IStore> = {};
