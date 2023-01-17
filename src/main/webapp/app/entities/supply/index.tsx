import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Supply from './supply';
import SupplyDetail from './supply-detail';
import SupplyUpdate from './supply-update';
import SupplyDeleteDialog from './supply-delete-dialog';

const SupplyRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Supply />} />
    <Route path="new" element={<SupplyUpdate />} />
    <Route path=":id">
      <Route index element={<SupplyDetail />} />
      <Route path="edit" element={<SupplyUpdate />} />
      <Route path="delete" element={<SupplyDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SupplyRoutes;
