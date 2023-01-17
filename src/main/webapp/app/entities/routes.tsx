import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Functionality from './functionality';
import FunctionalityCategory from './functionality-category';
import Transac from './transac';
import MobileBankingActor from './mobile-banking-actor';
import SupplyRequest from './supply-request';
import Supply from './supply';
import Payment from './payment';
import PaymentMethod from './payment-method';
import Event from './event';
import Notification from './notification';
import NotificationSettings from './notification-settings';
import Ticket from './ticket';
import TicketDelivery from './ticket-delivery';
import TicketDeliveryMethod from './ticket-delivery-method';
import Store from './store';
import InsuranceAndMicroCreditsActor from './insurance-and-micro-credits-actor';
import InsuranceAndMicroCreditsContribution from './insurance-and-micro-credits-contribution';
import AdminNetwork from './admin-network';
import IntermediateAgent from './intermediate-agent';
import FinalAgent from './final-agent';
import Country from './country';
import Region from './region';
import Department from './department';
import Town from './town';
import Zone from './zone';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="functionality/*" element={<Functionality />} />
        <Route path="functionality-category/*" element={<FunctionalityCategory />} />
        <Route path="transac/*" element={<Transac />} />
        <Route path="mobile-banking-actor/*" element={<MobileBankingActor />} />
        <Route path="supply-request/*" element={<SupplyRequest />} />
        <Route path="supply/*" element={<Supply />} />
        <Route path="payment/*" element={<Payment />} />
        <Route path="payment-method/*" element={<PaymentMethod />} />
        <Route path="event/*" element={<Event />} />
        <Route path="notification/*" element={<Notification />} />
        <Route path="notification-settings/*" element={<NotificationSettings />} />
        <Route path="ticket/*" element={<Ticket />} />
        <Route path="ticket-delivery/*" element={<TicketDelivery />} />
        <Route path="ticket-delivery-method/*" element={<TicketDeliveryMethod />} />
        <Route path="store/*" element={<Store />} />
        <Route path="insurance-and-micro-credits-actor/*" element={<InsuranceAndMicroCreditsActor />} />
        <Route path="insurance-and-micro-credits-contribution/*" element={<InsuranceAndMicroCreditsContribution />} />
        <Route path="admin-network/*" element={<AdminNetwork />} />
        <Route path="intermediate-agent/*" element={<IntermediateAgent />} />
        <Route path="final-agent/*" element={<FinalAgent />} />
        <Route path="country/*" element={<Country />} />
        <Route path="region/*" element={<Region />} />
        <Route path="department/*" element={<Department />} />
        <Route path="town/*" element={<Town />} />
        <Route path="zone/*" element={<Zone />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
