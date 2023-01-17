import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './payment-method.reducer';

export const PaymentMethodDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const paymentMethodEntity = useAppSelector(state => state.paymentMethod.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="paymentMethodDetailsHeading">
          <Translate contentKey="hackaton3App.paymentMethod.detail.title">PaymentMethod</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{paymentMethodEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="hackaton3App.paymentMethod.name">Name</Translate>
            </span>
          </dt>
          <dd>{paymentMethodEntity.name}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="hackaton3App.paymentMethod.type">Type</Translate>
            </span>
          </dt>
          <dd>{paymentMethodEntity.type}</dd>
        </dl>
        <Button tag={Link} to="/payment-method" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/payment-method/${paymentMethodEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PaymentMethodDetail;
