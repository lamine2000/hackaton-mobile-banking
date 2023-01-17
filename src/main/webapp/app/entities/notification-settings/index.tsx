import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import NotificationSettings from './notification-settings';
import NotificationSettingsDetail from './notification-settings-detail';
import NotificationSettingsUpdate from './notification-settings-update';
import NotificationSettingsDeleteDialog from './notification-settings-delete-dialog';

const NotificationSettingsRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<NotificationSettings />} />
    <Route path="new" element={<NotificationSettingsUpdate />} />
    <Route path=":id">
      <Route index element={<NotificationSettingsDetail />} />
      <Route path="edit" element={<NotificationSettingsUpdate />} />
      <Route path="delete" element={<NotificationSettingsDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default NotificationSettingsRoutes;
