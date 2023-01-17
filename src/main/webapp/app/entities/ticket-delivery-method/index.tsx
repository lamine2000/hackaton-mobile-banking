import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TicketDeliveryMethod from './ticket-delivery-method';
import TicketDeliveryMethodDetail from './ticket-delivery-method-detail';
import TicketDeliveryMethodUpdate from './ticket-delivery-method-update';
import TicketDeliveryMethodDeleteDialog from './ticket-delivery-method-delete-dialog';

const TicketDeliveryMethodRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TicketDeliveryMethod />} />
    <Route path="new" element={<TicketDeliveryMethodUpdate />} />
    <Route path=":id">
      <Route index element={<TicketDeliveryMethodDetail />} />
      <Route path="edit" element={<TicketDeliveryMethodUpdate />} />
      <Route path="delete" element={<TicketDeliveryMethodDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TicketDeliveryMethodRoutes;
