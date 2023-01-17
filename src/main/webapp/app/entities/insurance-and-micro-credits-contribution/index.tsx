import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import InsuranceAndMicroCreditsContribution from './insurance-and-micro-credits-contribution';
import InsuranceAndMicroCreditsContributionDetail from './insurance-and-micro-credits-contribution-detail';
import InsuranceAndMicroCreditsContributionUpdate from './insurance-and-micro-credits-contribution-update';
import InsuranceAndMicroCreditsContributionDeleteDialog from './insurance-and-micro-credits-contribution-delete-dialog';

const InsuranceAndMicroCreditsContributionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<InsuranceAndMicroCreditsContribution />} />
    <Route path="new" element={<InsuranceAndMicroCreditsContributionUpdate />} />
    <Route path=":id">
      <Route index element={<InsuranceAndMicroCreditsContributionDetail />} />
      <Route path="edit" element={<InsuranceAndMicroCreditsContributionUpdate />} />
      <Route path="delete" element={<InsuranceAndMicroCreditsContributionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default InsuranceAndMicroCreditsContributionRoutes;
