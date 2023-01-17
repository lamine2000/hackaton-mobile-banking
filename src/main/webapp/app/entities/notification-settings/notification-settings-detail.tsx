import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './notification-settings.reducer';

export const NotificationSettingsDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const notificationSettingsEntity = useAppSelector(state => state.notificationSettings.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="notificationSettingsDetailsHeading">
          <Translate contentKey="hackaton3App.notificationSettings.detail.title">NotificationSettings</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{notificationSettingsEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="hackaton3App.notificationSettings.name">Name</Translate>
            </span>
          </dt>
          <dd>{notificationSettingsEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="hackaton3App.notificationSettings.description">Description</Translate>
            </span>
          </dt>
          <dd>{notificationSettingsEntity.description}</dd>
          <dt>
            <span id="value">
              <Translate contentKey="hackaton3App.notificationSettings.value">Value</Translate>
            </span>
          </dt>
          <dd>{notificationSettingsEntity.value}</dd>
        </dl>
        <Button tag={Link} to="/notification-settings" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/notification-settings/${notificationSettingsEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default NotificationSettingsDetail;
