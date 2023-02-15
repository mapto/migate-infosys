import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IPerson } from 'app/shared/model/person.model';
import { getEntities as getPeople } from 'app/entities/person/person.reducer';
import { IInstitution } from 'app/shared/model/institution.model';
import { getEntities as getInstitutions } from 'app/entities/institution/institution.reducer';
import { IRoleInstitution } from 'app/shared/model/role-institution.model';
import { getEntity, updateEntity, createEntity, reset } from './role-institution.reducer';

export const RoleInstitutionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const people = useAppSelector(state => state.person.entities);
  const institutions = useAppSelector(state => state.institution.entities);
  const roleInstitutionEntity = useAppSelector(state => state.roleInstitution.entity);
  const loading = useAppSelector(state => state.roleInstitution.loading);
  const updating = useAppSelector(state => state.roleInstitution.updating);
  const updateSuccess = useAppSelector(state => state.roleInstitution.updateSuccess);

  const handleClose = () => {
    navigate('/role-institution');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getPeople({}));
    dispatch(getInstitutions({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.start = convertDateTimeToServer(values.start);
    values.end = convertDateTimeToServer(values.end);

    const entity = {
      ...roleInstitutionEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          start: displayDefaultDateTime(),
          end: displayDefaultDateTime(),
        }
      : {
          ...roleInstitutionEntity,
          start: convertDateTimeFromServer(roleInstitutionEntity.start),
          end: convertDateTimeFromServer(roleInstitutionEntity.end),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="migateInfosysApp.roleInstitution.home.createOrEditLabel" data-cy="RoleInstitutionCreateUpdateHeading">
            Create or edit a Role Institution
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField name="id" required readOnly id="role-institution-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField label="Name" id="role-institution-name" name="name" data-cy="name" type="text" />
              <ValidatedField
                label="Start"
                id="role-institution-start"
                name="start"
                data-cy="start"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label="End"
                id="role-institution-end"
                name="end"
                data-cy="end"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/role-institution" replace color="info">
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

export default RoleInstitutionUpdate;
