import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './town.reducer';

export const TownDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const townEntity = useAppSelector(state => state.town.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="townDetailsHeading">
          <Translate contentKey="hackaton3App.town.detail.title">Town</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{townEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="hackaton3App.town.name">Name</Translate>
            </span>
          </dt>
          <dd>{townEntity.name}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="hackaton3App.town.code">Code</Translate>
            </span>
          </dt>
          <dd>{townEntity.code}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="hackaton3App.town.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{townEntity.createdAt ? <TextFormat value={townEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="createdBy">
              <Translate contentKey="hackaton3App.town.createdBy">Created By</Translate>
            </span>
          </dt>
          <dd>{townEntity.createdBy}</dd>
        </dl>
        <Button tag={Link} to="/town" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/town/${townEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TownDetail;
