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
          <dd>{workEntity.published ? <TextFormat value={workEntity.published} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>Responsible</dt>
          <dd>
            {workEntity.responsibles
              ? workEntity.responsibles.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.name}</a>
                    {workEntity.responsibles && i === workEntity.responsibles.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
          <dt>Sponsor</dt>
          <dd>
            {workEntity.sponsors
              ? workEntity.sponsors.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.name}</a>
                    {workEntity.sponsors && i === workEntity.sponsors.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
          <dt>Role</dt>
          <dd>
            {workEntity.roles
              ? workEntity.roles.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.name}</a>
                    {workEntity.roles && i === workEntity.roles.length - 1 ? '' : ', '}
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
