import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import FunctionalityCategory from './functionality-category';
import FunctionalityCategoryDetail from './functionality-category-detail';
import FunctionalityCategoryUpdate from './functionality-category-update';
import FunctionalityCategoryDeleteDialog from './functionality-category-delete-dialog';

const FunctionalityCategoryRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<FunctionalityCategory />} />
    <Route path="new" element={<FunctionalityCategoryUpdate />} />
    <Route path=":id">
      <Route index element={<FunctionalityCategoryDetail />} />
      <Route path="edit" element={<FunctionalityCategoryUpdate />} />
      <Route path="delete" element={<FunctionalityCategoryDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default FunctionalityCategoryRoutes;
