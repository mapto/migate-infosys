import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Work from './work';
import WorkDetail from './work-detail';
import WorkUpdate from './work-update';
import WorkDeleteDialog from './work-delete-dialog';

const WorkRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Work />} />
    <Route path="new" element={<WorkUpdate />} />
    <Route path=":id">
      <Route index element={<WorkDetail />} />
      <Route path="edit" element={<WorkUpdate />} />
      <Route path="delete" element={<WorkDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default WorkRoutes;
