import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Functionality from './functionality';
import FunctionalityDetail from './functionality-detail';
import FunctionalityUpdate from './functionality-update';
import FunctionalityDeleteDialog from './functionality-delete-dialog';

const FunctionalityRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Functionality />} />
    <Route path="new" element={<FunctionalityUpdate />} />
    <Route path=":id">
      <Route index element={<FunctionalityDetail />} />
      <Route path="edit" element={<FunctionalityUpdate />} />
      <Route path="delete" element={<FunctionalityDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default FunctionalityRoutes;
