import dayjs from 'dayjs';

export interface ICountry {
  id?: number;
  name?: string;
  codeAlpha?: string;
  code?: string;
  flag?: string | null;
  createdAt?: string | null;
  createdBy?: string | null;
}

export const defaultValue: Readonly<ICountry> = {};
