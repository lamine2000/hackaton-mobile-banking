export interface INotificationSettings {
  id?: number;
  name?: string;
  description?: string | null;
  value?: string | null;
}

export const defaultValue: Readonly<INotificationSettings> = {};
