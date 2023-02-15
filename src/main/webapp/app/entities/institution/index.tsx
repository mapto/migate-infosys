import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Institution from './institution';
import InstitutionDetail from './institution-detail';
import InstitutionUpdate from './institution-update';
import InstitutionDeleteDialog from './institution-delete-dialog';

const InstitutionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Institution />} />
    <Route path="new" element={<InstitutionUpdate />} />
    <Route path=":id">
      <Route index element={<InstitutionDetail />} />
      <Route path="edit" element={<InstitutionUpdate />} />
      <Route path="delete" element={<InstitutionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default InstitutionRoutes;
