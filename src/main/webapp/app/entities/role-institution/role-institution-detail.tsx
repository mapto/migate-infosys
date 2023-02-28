import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './role-institution.reducer';

export const RoleInstitutionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const roleInstitutionEntity = useAppSelector(state => state.roleInstitution.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="roleInstitutionDetailsHeading">Role Institution</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{roleInstitutionEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{roleInstitutionEntity.name}</dd>
          <dt>
            <span id="start">Start</span>
          </dt>
          <dd>
            {roleInstitutionEntity.start ? (
              <TextFormat value={roleInstitutionEntity.start} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="end">End</span>
          </dt>
          <dd>
            {roleInstitutionEntity.end ? <TextFormat value={roleInstitutionEntity.end} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
        </dl>
        <Button tag={Link} to="/role-institution" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/role-institution/${roleInstitutionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default RoleInstitutionDetail;
