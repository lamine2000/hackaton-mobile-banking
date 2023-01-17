import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Zone from './zone';
import ZoneDetail from './zone-detail';
import ZoneUpdate from './zone-update';
import ZoneDeleteDialog from './zone-delete-dialog';

const ZoneRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Zone />} />
    <Route path="new" element={<ZoneUpdate />} />
    <Route path=":id">
      <Route index element={<ZoneDetail />} />
      <Route path="edit" element={<ZoneUpdate />} />
      <Route path="delete" element={<ZoneDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ZoneRoutes;
