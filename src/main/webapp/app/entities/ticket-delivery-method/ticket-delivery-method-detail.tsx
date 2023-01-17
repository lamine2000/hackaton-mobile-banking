import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './ticket-delivery-method.reducer';

export const TicketDeliveryMethodDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const ticketDeliveryMethodEntity = useAppSelector(state => state.ticketDeliveryMethod.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="ticketDeliveryMethodDetailsHeading">
          <Translate contentKey="hackaton3App.ticketDeliveryMethod.detail.title">TicketDeliveryMethod</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{ticketDeliveryMethodEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="hackaton3App.ticketDeliveryMethod.name">Name</Translate>
            </span>
          </dt>
          <dd>{ticketDeliveryMethodEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="hackaton3App.ticketDeliveryMethod.description">Description</Translate>
            </span>
          </dt>
          <dd>{ticketDeliveryMethodEntity.description}</dd>
        </dl>
        <Button tag={Link} to="/ticket-delivery-method" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/ticket-delivery-method/${ticketDeliveryMethodEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TicketDeliveryMethodDetail;
