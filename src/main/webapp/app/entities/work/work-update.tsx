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
import { IRoleWork } from 'app/shared/model/role-work.model';
import { getEntities as getRoleWorks } from 'app/entities/role-work/role-work.reducer';
import { IWork } from 'app/shared/model/work.model';
import { getEntity, updateEntity, createEntity, reset } from './work.reducer';

export const WorkUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const people = useAppSelector(state => state.person.entities);
  const institutions = useAppSelector(state => state.institution.entities);
  const roleWorks = useAppSelector(state => state.roleWork.entities);
  const workEntity = useAppSelector(state => state.work.entity);
  const loading = useAppSelector(state => state.work.loading);
  const updating = useAppSelector(state => state.work.updating);
  const updateSuccess = useAppSelector(state => state.work.updateSuccess);

  const handleClose = () => {
    navigate('/work');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getPeople({}));
    dispatch(getInstitutions({}));
    dispatch(getRoleWorks({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.published = convertDateTimeToServer(values.published);

    const entity = {
      ...workEntity,
      ...values,
      people: mapIdList(values.people),
      institutions: mapIdList(values.institutions),
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
          published: displayDefaultDateTime(),
        }
      : {
          ...workEntity,
          published: convertDateTimeFromServer(workEntity.published),
          people: workEntity?.people?.map(e => e.id.toString()),
          institutions: workEntity?.institutions?.map(e => e.id.toString()),
          roleWorks: workEntity?.roleWorks?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="migateInfosysApp.work.home.createOrEditLabel" data-cy="WorkCreateUpdateHeading">
            Create or edit a Work
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="work-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField label="Name" id="work-name" name="name" data-cy="name" type="text" />
              <ValidatedField
                label="Published"
                id="work-published"
                name="published"
                data-cy="published"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField label="Person" id="work-person" data-cy="person" type="select" multiple name="people">
                <option value="" key="0" />
                {people
                  ? people.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField label="Institution" id="work-institution" data-cy="institution" type="select" multiple name="institutions">
                <option value="" key="0" />
                {institutions
                  ? institutions.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField label="Role Work" id="work-roleWork" data-cy="roleWork" type="select" multiple name="roleWorks">
                <option value="" key="0" />
                {roleWorks
                  ? roleWorks.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/work" replace color="info">
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

export default WorkUpdate;
