import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Person from './person';
import PersonDetail from './person-detail';
import PersonUpdate from './person-update';
import PersonDeleteDialog from './person-delete-dialog';

const PersonRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Person />} />
    <Route path="new" element={<PersonUpdate />} />
    <Route path=":id">
      <Route index element={<PersonDetail />} />
      <Route path="edit" element={<PersonUpdate />} />
      <Route path="delete" element={<PersonDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default PersonRoutes;
