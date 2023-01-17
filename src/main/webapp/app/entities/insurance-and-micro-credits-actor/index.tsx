import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import InsuranceAndMicroCreditsActor from './insurance-and-micro-credits-actor';
import InsuranceAndMicroCreditsActorDetail from './insurance-and-micro-credits-actor-detail';
import InsuranceAndMicroCreditsActorUpdate from './insurance-and-micro-credits-actor-update';
import InsuranceAndMicroCreditsActorDeleteDialog from './insurance-and-micro-credits-actor-delete-dialog';

const InsuranceAndMicroCreditsActorRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<InsuranceAndMicroCreditsActor />} />
    <Route path="new" element={<InsuranceAndMicroCreditsActorUpdate />} />
    <Route path=":id">
      <Route index element={<InsuranceAndMicroCreditsActorDetail />} />
      <Route path="edit" element={<InsuranceAndMicroCreditsActorUpdate />} />
      <Route path="delete" element={<InsuranceAndMicroCreditsActorDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default InsuranceAndMicroCreditsActorRoutes;
