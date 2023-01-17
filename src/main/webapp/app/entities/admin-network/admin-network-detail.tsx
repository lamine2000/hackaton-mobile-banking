import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './admin-network.reducer';

export const AdminNetworkDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const adminNetworkEntity = useAppSelector(state => state.adminNetwork.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="adminNetworkDetailsHeading">
          <Translate contentKey="hackaton3App.adminNetwork.detail.title">AdminNetwork</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{adminNetworkEntity.id}</dd>
          <dt>
            <span id="firstName">
              <Translate contentKey="hackaton3App.adminNetwork.firstName">First Name</Translate>
            </span>
          </dt>
          <dd>{adminNetworkEntity.firstName}</dd>
          <dt>
            <span id="lastName">
              <Translate contentKey="hackaton3App.adminNetwork.lastName">Last Name</Translate>
            </span>
          </dt>
          <dd>{adminNetworkEntity.lastName}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="hackaton3App.adminNetwork.email">Email</Translate>
            </span>
          </dt>
          <dd>{adminNetworkEntity.email}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="hackaton3App.adminNetwork.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{adminNetworkEntity.phone}</dd>
          <dt>
            <span id="addressLine1">
              <Translate contentKey="hackaton3App.adminNetwork.addressLine1">Address Line 1</Translate>
            </span>
          </dt>
          <dd>{adminNetworkEntity.addressLine1}</dd>
          <dt>
            <span id="addressLine2">
              <Translate contentKey="hackaton3App.adminNetwork.addressLine2">Address Line 2</Translate>
            </span>
          </dt>
          <dd>{adminNetworkEntity.addressLine2}</dd>
          <dt>
            <span id="city">
              <Translate contentKey="hackaton3App.adminNetwork.city">City</Translate>
            </span>
          </dt>
          <dd>{adminNetworkEntity.city}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="hackaton3App.adminNetwork.status">Status</Translate>
            </span>
          </dt>
          <dd>{adminNetworkEntity.status}</dd>
          <dt>
            <span id="commissionRate">
              <Translate contentKey="hackaton3App.adminNetwork.commissionRate">Commission Rate</Translate>
            </span>
          </dt>
          <dd>{adminNetworkEntity.commissionRate}</dd>
          <dt>
            <Translate contentKey="hackaton3App.adminNetwork.user">User</Translate>
          </dt>
          <dd>{adminNetworkEntity.user ? adminNetworkEntity.user.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/admin-network" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/admin-network/${adminNetworkEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AdminNetworkDetail;
