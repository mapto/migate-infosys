import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './work.reducer';

export const WorkDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const workEntity = useAppSelector(state => state.work.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="workDetailsHeading">Work</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{workEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{workEntity.name}</dd>
          <dt>
            <span id="published">Published</span>
          </dt>
          <dd>{workEntity.published ? <TextFormat value={workEntity.published} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>Person</dt>
          <dd>
            {workEntity.people
              ? workEntity.people.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {workEntity.people && i === workEntity.people.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
          <dt>Institution</dt>
          <dd>
            {workEntity.institutions
              ? workEntity.institutions.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {workEntity.institutions && i === workEntity.institutions.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
          <dt>Role Work</dt>
          <dd>
            {workEntity.roleWorks
              ? workEntity.roleWorks.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {workEntity.roleWorks && i === workEntity.roleWorks.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/work" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/work/${workEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default WorkDetail;
