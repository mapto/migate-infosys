import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './person.reducer';

export const PersonDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const personEntity = useAppSelector(state => state.person.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="personDetailsHeading">Person</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{personEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{personEntity.name}</dd>
          <dt>
            <span id="gender">Gender</span>
          </dt>
          <dd>{personEntity.gender}</dd>
          <dt>
            <span id="dob">Dob</span>
          </dt>
          <dd>{personEntity.dob ? <TextFormat value={personEntity.dob} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>Role Institution</dt>
          <dd>
            {personEntity.roleInstitutions
              ? personEntity.roleInstitutions.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {personEntity.roleInstitutions && i === personEntity.roleInstitutions.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
          <dt>Role Work</dt>
          <dd>
            {personEntity.roleWorks
              ? personEntity.roleWorks.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {personEntity.roleWorks && i === personEntity.roleWorks.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/person" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/person/${personEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default PersonDetail;
