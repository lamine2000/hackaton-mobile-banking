import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import MobileBankingActor from './mobile-banking-actor';
import MobileBankingActorDetail from './mobile-banking-actor-detail';
import MobileBankingActorUpdate from './mobile-banking-actor-update';
import MobileBankingActorDeleteDialog from './mobile-banking-actor-delete-dialog';

const MobileBankingActorRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<MobileBankingActor />} />
    <Route path="new" element={<MobileBankingActorUpdate />} />
    <Route path=":id">
      <Route index element={<MobileBankingActorDetail />} />
      <Route path="edit" element={<MobileBankingActorUpdate />} />
      <Route path="delete" element={<MobileBankingActorDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default MobileBankingActorRoutes;
