import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './zone.reducer';

export const ZoneDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const zoneEntity = useAppSelector(state => state.zone.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="zoneDetailsHeading">
          <Translate contentKey="hackaton3App.zone.detail.title">Zone</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{zoneEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="hackaton3App.zone.name">Name</Translate>
            </span>
          </dt>
          <dd>{zoneEntity.name}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="hackaton3App.zone.code">Code</Translate>
            </span>
          </dt>
          <dd>{zoneEntity.code}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="hackaton3App.zone.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{zoneEntity.createdAt ? <TextFormat value={zoneEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="createdBy">
              <Translate contentKey="hackaton3App.zone.createdBy">Created By</Translate>
            </span>
          </dt>
          <dd>{zoneEntity.createdBy}</dd>
        </dl>
        <Button tag={Link} to="/zone" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/zone/${zoneEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ZoneDetail;
