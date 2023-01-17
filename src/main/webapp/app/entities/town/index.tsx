import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Town from './town';
import TownDetail from './town-detail';
import TownUpdate from './town-update';
import TownDeleteDialog from './town-delete-dialog';

const TownRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Town />} />
    <Route path="new" element={<TownUpdate />} />
    <Route path=":id">
      <Route index element={<TownDetail />} />
      <Route path="edit" element={<TownUpdate />} />
      <Route path="delete" element={<TownDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TownRoutes;
