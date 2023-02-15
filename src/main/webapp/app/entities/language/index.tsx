import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Language from './language';
import LanguageDetail from './language-detail';
import LanguageUpdate from './language-update';
import LanguageDeleteDialog from './language-delete-dialog';

const LanguageRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Language />} />
    <Route path="new" element={<LanguageUpdate />} />
    <Route path=":id">
      <Route index element={<LanguageDetail />} />
      <Route path="edit" element={<LanguageUpdate />} />
      <Route path="delete" element={<LanguageDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default LanguageRoutes;
