export interface IInsuranceAndMicroCreditsActor {
  id?: number;
  logoContentType?: string;
  logo?: string;
  name?: string;
  acronym?: string | null;
  description?: string | null;
}

export const defaultValue: Readonly<IInsuranceAndMicroCreditsActor> = {};
