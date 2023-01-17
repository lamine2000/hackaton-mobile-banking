import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AdminNetwork from './admin-network';
import AdminNetworkDetail from './admin-network-detail';
import AdminNetworkUpdate from './admin-network-update';
import AdminNetworkDeleteDialog from './admin-network-delete-dialog';

const AdminNetworkRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<AdminNetwork />} />
    <Route path="new" element={<AdminNetworkUpdate />} />
    <Route path=":id">
      <Route index element={<AdminNetworkDetail />} />
      <Route path="edit" element={<AdminNetworkUpdate />} />
      <Route path="delete" element={<AdminNetworkDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AdminNetworkRoutes;
