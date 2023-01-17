import { IInsuranceAndMicroCreditsActor } from 'app/shared/model/insurance-and-micro-credits-actor.model';
import { IPayment } from 'app/shared/model/payment.model';

export interface IInsuranceAndMicroCreditsContribution {
  id?: number;
  code?: string;
  insuranceAndMicroCreditsActor?: IInsuranceAndMicroCreditsActor | null;
  payment?: IPayment | null;
}

export const defaultValue: Readonly<IInsuranceAndMicroCreditsContribution> = {};
