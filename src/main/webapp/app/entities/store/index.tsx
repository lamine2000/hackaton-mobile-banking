import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Store from './store';
import StoreDetail from './store-detail';
import StoreUpdate from './store-update';
import StoreDeleteDialog from './store-delete-dialog';

const StoreRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Store />} />
    <Route path="new" element={<StoreUpdate />} />
    <Route path=":id">
      <Route index element={<StoreDetail />} />
      <Route path="edit" element={<StoreUpdate />} />
      <Route path="delete" element={<StoreDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default StoreRoutes;
