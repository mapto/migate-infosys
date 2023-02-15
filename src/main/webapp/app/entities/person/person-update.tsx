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
import { IRoleWork } from 'app/shared/model/role-work.model';
import { getEntities as getRoleWorks } from 'app/entities/role-work/role-work.reducer';
import { IWork } from 'app/shared/model/work.model';
import { getEntities as getWorks } from 'app/entities/work/work.reducer';
import { IPerson } from 'app/shared/model/person.model';
import { getEntity, updateEntity, createEntity, reset } from './person.reducer';

export const PersonUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const roleInstitutions = useAppSelector(state => state.roleInstitution.entities);
  const roleWorks = useAppSelector(state => state.roleWork.entities);
  const works = useAppSelector(state => state.work.entities);
  const personEntity = useAppSelector(state => state.person.entity);
  const loading = useAppSelector(state => state.person.loading);
  const updating = useAppSelector(state => state.person.updating);
  const updateSuccess = useAppSelector(state => state.person.updateSuccess);

  const handleClose = () => {
    navigate('/person');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getRoleInstitutions({}));
    dispatch(getRoleWorks({}));
    dispatch(getWorks({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.dob = convertDateTimeToServer(values.dob);

    const entity = {
      ...personEntity,
      ...values,
      roleInstitutions: mapIdList(values.roleInstitutions),
      roleWorks: mapIdList(values.roleWorks),
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
          dob: displayDefaultDateTime(),
        }
      : {
          ...personEntity,
          dob: convertDateTimeFromServer(personEntity.dob),
          roleInstitutions: personEntity?.roleInstitutions?.map(e => e.id.toString()),
          roleWorks: personEntity?.roleWorks?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="migateInfosysApp.person.home.createOrEditLabel" data-cy="PersonCreateUpdateHeading">
            Create or edit a Person
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="person-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Name"
                id="person-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField label="Gender" id="person-gender" name="gender" data-cy="gender" type="text" />
              <ValidatedField label="Dob" id="person-dob" name="dob" data-cy="dob" type="datetime-local" placeholder="YYYY-MM-DD HH:mm" />
              <ValidatedField
                label="Role Institution"
                id="person-roleInstitution"
                data-cy="roleInstitution"
                type="select"
                multiple
                name="roleInstitutions"
              >
                <option value="" key="0" />
                {roleInstitutions
                  ? roleInstitutions.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField label="Role Work" id="person-roleWork" data-cy="roleWork" type="select" multiple name="roleWorks">
                <option value="" key="0" />
                {roleWorks
                  ? roleWorks.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/person" replace color="info">
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

export default PersonUpdate;
