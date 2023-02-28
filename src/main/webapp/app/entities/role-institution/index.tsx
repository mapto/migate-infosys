import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import RoleInstitution from './role-institution';
import RoleInstitutionDetail from './role-institution-detail';
import RoleInstitutionUpdate from './role-institution-update';
import RoleInstitutionDeleteDialog from './role-institution-delete-dialog';

const RoleInstitutionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<RoleInstitution />} />
    <Route path="new" element={<RoleInstitutionUpdate />} />
    <Route path=":id">
      <Route index element={<RoleInstitutionDetail />} />
      <Route path="edit" element={<RoleInstitutionUpdate />} />
      <Route path="delete" element={<RoleInstitutionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default RoleInstitutionRoutes;
