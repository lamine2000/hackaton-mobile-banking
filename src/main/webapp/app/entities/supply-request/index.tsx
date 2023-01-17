import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import SupplyRequest from './supply-request';
import SupplyRequestDetail from './supply-request-detail';
import SupplyRequestUpdate from './supply-request-update';
import SupplyRequestDeleteDialog from './supply-request-delete-dialog';

const SupplyRequestRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<SupplyRequest />} />
    <Route path="new" element={<SupplyRequestUpdate />} />
    <Route path=":id">
      <Route index element={<SupplyRequestDetail />} />
      <Route path="edit" element={<SupplyRequestUpdate />} />
      <Route path="delete" element={<SupplyRequestDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SupplyRequestRoutes;
