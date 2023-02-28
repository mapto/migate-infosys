import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './institution.reducer';

export const InstitutionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const institutionEntity = useAppSelector(state => state.institution.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="institutionDetailsHeading">Institution</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{institutionEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{institutionEntity.name}</dd>
          <dt>
            <span id="country">Country</span>
          </dt>
          <dd>{institutionEntity.country}</dd>
          <dt>Role</dt>
          <dd>
            {institutionEntity.roles
              ? institutionEntity.roles.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.name}</a>
                    {institutionEntity.roles && i === institutionEntity.roles.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
          <dt>Site</dt>
          <dd>{institutionEntity.site ? institutionEntity.site.address : ''}</dd>
        </dl>
        <Button tag={Link} to="/institution" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/institution/${institutionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default InstitutionDetail;
