import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './supply-request.reducer';

export const SupplyRequestDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const supplyRequestEntity = useAppSelector(state => state.supplyRequest.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="supplyRequestDetailsHeading">
          <Translate contentKey="hackaton3App.supplyRequest.detail.title">SupplyRequest</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{supplyRequestEntity.id}</dd>
          <dt>
            <span id="amount">
              <Translate contentKey="hackaton3App.supplyRequest.amount">Amount</Translate>
            </span>
          </dt>
          <dd>{supplyRequestEntity.amount}</dd>
          <dt>
            <span id="quantity">
              <Translate contentKey="hackaton3App.supplyRequest.quantity">Quantity</Translate>
            </span>
          </dt>
          <dd>{supplyRequestEntity.quantity}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="hackaton3App.supplyRequest.status">Status</Translate>
            </span>
          </dt>
          <dd>{supplyRequestEntity.status}</dd>
          <dt>
            <Translate contentKey="hackaton3App.supplyRequest.functionality">Functionality</Translate>
          </dt>
          <dd>{supplyRequestEntity.functionality ? supplyRequestEntity.functionality.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/supply-request" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/supply-request/${supplyRequestEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default SupplyRequestDetail;
