import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IRoleInstitution } from 'app/shared/model/role-institution.model';
import { getEntities as getRoleInstitutions } from 'app/entities/role-institution/role-institution.reducer';
import { ILocation } from 'app/shared/model/location.model';
import { getEntities as getLocations } from 'app/entities/location/location.reducer';
import { IWork } from 'app/shared/model/work.model';
import { getEntities as getWorks } from 'app/entities/work/work.reducer';
import { IInstitution } from 'app/shared/model/institution.model';
import { Country } from 'app/shared/model/enumerations/country.model';
import { getEntity, updateEntity, createEntity, reset } from './institution.reducer';

export const InstitutionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const roleInstitutions = useAppSelector(state => state.roleInstitution.entities);
  const locations = useAppSelector(state => state.location.entities);
  const works = useAppSelector(state => state.work.entities);
  const institutionEntity = useAppSelector(state => state.institution.entity);
  const loading = useAppSelector(state => state.institution.loading);
  const updating = useAppSelector(state => state.institution.updating);
  const updateSuccess = useAppSelector(state => state.institution.updateSuccess);
  const countryValues = Object.keys(Country);

  const handleClose = () => {
    navigate('/institution');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getRoleInstitutions({}));
    dispatch(getLocations({}));
    dispatch(getWorks({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...institutionEntity,
      ...values,
      roles: mapIdList(values.roles),
      site: locations.find(it => it.id.toString() === values.site.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          country: 'UNITED_STATES',
          ...institutionEntity,
          roles: institutionEntity?.roles?.map(e => e.id.toString()),
          site: institutionEntity?.site?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="migateInfosysApp.institution.home.createOrEditLabel" data-cy="InstitutionCreateUpdateHeading">
            Create or edit a Institution
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="institution-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Name"
                id="institution-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField label="Country" id="institution-country" name="country" data-cy="country" type="select">
                {countryValues.map(country => (
                  <option value={country} key={country}>
                    {country}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField label="Role" id="institution-role" data-cy="role" type="select" multiple name="roles">
                <option value="" key="0" />
                {roleInstitutions
                  ? roleInstitutions.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="institution-site" name="site" data-cy="site" label="Site" type="select">
                <option value="" key="0" />
                {locations
                  ? locations.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.address}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/institution" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default InstitutionUpdate;
