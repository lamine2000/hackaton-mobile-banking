import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TicketDelivery from './ticket-delivery';
import TicketDeliveryDetail from './ticket-delivery-detail';
import TicketDeliveryUpdate from './ticket-delivery-update';
import TicketDeliveryDeleteDialog from './ticket-delivery-delete-dialog';

const TicketDeliveryRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TicketDelivery />} />
    <Route path="new" element={<TicketDeliveryUpdate />} />
    <Route path=":id">
      <Route index element={<TicketDeliveryDetail />} />
      <Route path="edit" element={<TicketDeliveryUpdate />} />
      <Route path="delete" element={<TicketDeliveryDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TicketDeliveryRoutes;
