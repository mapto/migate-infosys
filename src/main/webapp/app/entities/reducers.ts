import country from 'app/entities/country/country.reducer';
import language from 'app/entities/language/language.reducer';
import person from 'app/entities/person/person.reducer';
import institution from 'app/entities/institution/institution.reducer';
import work from 'app/entities/work/work.reducer';
import roleWork from 'app/entities/role-work/role-work.reducer';
import location from 'app/entities/location/location.reducer';
import roleInstitution from 'app/entities/role-institution/role-institution.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  country,
  language,
  person,
  institution,
  work,
  roleWork,
  location,
  roleInstitution,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
