import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import RoleWork from './role-work';
import RoleWorkDetail from './role-work-detail';
import RoleWorkUpdate from './role-work-update';
import RoleWorkDeleteDialog from './role-work-delete-dialog';

const RoleWorkRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<RoleWork />} />
    <Route path="new" element={<RoleWorkUpdate />} />
    <Route path=":id">
      <Route index element={<RoleWorkDetail />} />
      <Route path="edit" element={<RoleWorkUpdate />} />
      <Route path="delete" element={<RoleWorkDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default RoleWorkRoutes;
