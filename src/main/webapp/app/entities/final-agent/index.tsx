import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import FinalAgent from './final-agent';
import FinalAgentDetail from './final-agent-detail';
import FinalAgentUpdate from './final-agent-update';
import FinalAgentDeleteDialog from './final-agent-delete-dialog';

const FinalAgentRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<FinalAgent />} />
    <Route path="new" element={<FinalAgentUpdate />} />
    <Route path=":id">
      <Route index element={<FinalAgentDetail />} />
      <Route path="edit" element={<FinalAgentUpdate />} />
      <Route path="delete" element={<FinalAgentDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default FinalAgentRoutes;
