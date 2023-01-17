import dayjs from 'dayjs';

export interface IDepartment {
  id?: number;
  name?: string;
  code?: string;
  createdAt?: string | null;
  createdBy?: string | null;
}

export const defaultValue: Readonly<IDepartment> = {};
