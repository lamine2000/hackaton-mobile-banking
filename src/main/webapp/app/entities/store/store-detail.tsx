import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, openFile, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './store.reducer';

export const StoreDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const storeEntity = useAppSelector(state => state.store.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="storeDetailsHeading">
          <Translate contentKey="hackaton3App.store.detail.title">Store</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{storeEntity.id}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="hackaton3App.store.code">Code</Translate>
            </span>
          </dt>
          <dd>{storeEntity.code}</dd>
          <dt>
            <span id="location">
              <Translate contentKey="hackaton3App.store.location">Location</Translate>
            </span>
          </dt>
          <dd>
            {storeEntity.location ? (
              <div>
                {storeEntity.locationContentType ? (
                  <a onClick={openFile(storeEntity.locationContentType, storeEntity.location)}>
                    <Translate contentKey="entity.action.open">Open</Translate>&nbsp;
                  </a>
                ) : null}
                <span>
                  {storeEntity.locationContentType}, {byteSize(storeEntity.location)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="address">
              <Translate contentKey="hackaton3App.store.address">Address</Translate>
            </span>
          </dt>
          <dd>{storeEntity.address}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="hackaton3App.store.name">Name</Translate>
            </span>
          </dt>
          <dd>{storeEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="hackaton3App.store.description">Description</Translate>
            </span>
          </dt>
          <dd>{storeEntity.description}</dd>
          <dt>
            <span id="currency">
              <Translate contentKey="hackaton3App.store.currency">Currency</Translate>
            </span>
          </dt>
          <dd>{storeEntity.currency}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="hackaton3App.store.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{storeEntity.phone}</dd>
          <dt>
            <span id="notificationEmail">
              <Translate contentKey="hackaton3App.store.notificationEmail">Notification Email</Translate>
            </span>
          </dt>
          <dd>{storeEntity.notificationEmail}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="hackaton3App.store.status">Status</Translate>
            </span>
          </dt>
          <dd>{storeEntity.status}</dd>
          <dt>
            <span id="aboutUs">
              <Translate contentKey="hackaton3App.store.aboutUs">About Us</Translate>
            </span>
          </dt>
          <dd>{storeEntity.aboutUs}</dd>
          <dt>
            <Translate contentKey="hackaton3App.store.zone">Zone</Translate>
          </dt>
          <dd>{storeEntity.zone ? storeEntity.zone.name : ''}</dd>
          <dt>
            <Translate contentKey="hackaton3App.store.town">Town</Translate>
          </dt>
          <dd>{storeEntity.town ? storeEntity.town.name : ''}</dd>
          <dt>
            <Translate contentKey="hackaton3App.store.department">Department</Translate>
          </dt>
          <dd>{storeEntity.department ? storeEntity.department.name : ''}</dd>
          <dt>
            <Translate contentKey="hackaton3App.store.region">Region</Translate>
          </dt>
          <dd>{storeEntity.region ? storeEntity.region.name : ''}</dd>
          <dt>
            <Translate contentKey="hackaton3App.store.country">Country</Translate>
          </dt>
          <dd>{storeEntity.country ? storeEntity.country.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/store" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/store/${storeEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default StoreDetail;
