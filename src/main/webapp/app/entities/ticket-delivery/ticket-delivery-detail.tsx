import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './ticket-delivery.reducer';

export const TicketDeliveryDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const ticketDeliveryEntity = useAppSelector(state => state.ticketDelivery.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="ticketDeliveryDetailsHeading">
          <Translate contentKey="hackaton3App.ticketDelivery.detail.title">TicketDelivery</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{ticketDeliveryEntity.id}</dd>
          <dt>
            <span id="boughtAt">
              <Translate contentKey="hackaton3App.ticketDelivery.boughtAt">Bought At</Translate>
            </span>
          </dt>
          <dd>
            {ticketDeliveryEntity.boughtAt ? (
              <TextFormat value={ticketDeliveryEntity.boughtAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="boughtBy">
              <Translate contentKey="hackaton3App.ticketDelivery.boughtBy">Bought By</Translate>
            </span>
          </dt>
          <dd>{ticketDeliveryEntity.boughtBy}</dd>
          <dt>
            <span id="quantity">
              <Translate contentKey="hackaton3App.ticketDelivery.quantity">Quantity</Translate>
            </span>
          </dt>
          <dd>{ticketDeliveryEntity.quantity}</dd>
          <dt>
            <Translate contentKey="hackaton3App.ticketDelivery.ticketDeliveryMethod">Ticket Delivery Method</Translate>
          </dt>
          <dd>{ticketDeliveryEntity.ticketDeliveryMethod ? ticketDeliveryEntity.ticketDeliveryMethod.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/ticket-delivery" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/ticket-delivery/${ticketDeliveryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TicketDeliveryDetail;
