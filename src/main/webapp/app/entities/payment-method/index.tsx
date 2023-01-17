import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import PaymentMethod from './payment-method';
import PaymentMethodDetail from './payment-method-detail';
import PaymentMethodUpdate from './payment-method-update';
import PaymentMethodDeleteDialog from './payment-method-delete-dialog';

const PaymentMethodRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<PaymentMethod />} />
    <Route path="new" element={<PaymentMethodUpdate />} />
    <Route path=":id">
      <Route index element={<PaymentMethodDetail />} />
      <Route path="edit" element={<PaymentMethodUpdate />} />
      <Route path="delete" element={<PaymentMethodDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default PaymentMethodRoutes;
