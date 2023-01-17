import dayjs from 'dayjs';

export interface IRegion {
  id?: number;
  name?: string;
  code?: string;
  createdAt?: string | null;
  createdBy?: string | null;
}

export const defaultValue: Readonly<IRegion> = {};
