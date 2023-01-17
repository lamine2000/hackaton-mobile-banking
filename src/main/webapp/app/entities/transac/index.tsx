import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Transac from './transac';
import TransacDetail from './transac-detail';
import TransacUpdate from './transac-update';
import TransacDeleteDialog from './transac-delete-dialog';

const TransacRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Transac />} />
    <Route path="new" element={<TransacUpdate />} />
    <Route path=":id">
      <Route index element={<TransacDetail />} />
      <Route path="edit" element={<TransacUpdate />} />
      <Route path="delete" element={<TransacDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TransacRoutes;
