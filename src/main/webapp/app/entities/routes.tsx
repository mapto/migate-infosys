import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Person from './person';
import Institution from './institution';
import Work from './work';
import RoleWork from './role-work';
import Location from './location';
import RoleInstitution from './role-institution';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="person/*" element={<Person />} />
        <Route path="institution/*" element={<Institution />} />
        <Route path="work/*" element={<Work />} />
        <Route path="role-work/*" element={<RoleWork />} />
        <Route path="location/*" element={<Location />} />
        <Route path="role-institution/*" element={<RoleInstitution />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
