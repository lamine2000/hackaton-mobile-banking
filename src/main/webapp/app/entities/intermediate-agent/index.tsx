import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import IntermediateAgent from './intermediate-agent';
import IntermediateAgentDetail from './intermediate-agent-detail';
import IntermediateAgentUpdate from './intermediate-agent-update';
import IntermediateAgentDeleteDialog from './intermediate-agent-delete-dialog';

const IntermediateAgentRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<IntermediateAgent />} />
    <Route path="new" element={<IntermediateAgentUpdate />} />
    <Route path=":id">
      <Route index element={<IntermediateAgentDetail />} />
      <Route path="edit" element={<IntermediateAgentUpdate />} />
      <Route path="delete" element={<IntermediateAgentDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default IntermediateAgentRoutes;
