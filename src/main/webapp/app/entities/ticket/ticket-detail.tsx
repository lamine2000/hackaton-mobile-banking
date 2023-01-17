import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, openFile, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './ticket.reducer';

export const TicketDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const ticketEntity = useAppSelector(state => state.ticket.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="ticketDetailsHeading">
          <Translate contentKey="hackaton3App.ticket.detail.title">Ticket</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{ticketEntity.id}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="hackaton3App.ticket.code">Code</Translate>
            </span>
          </dt>
          <dd>{ticketEntity.code}</dd>
          <dt>
            <span id="data">
              <Translate contentKey="hackaton3App.ticket.data">Data</Translate>
            </span>
          </dt>
          <dd>
            {ticketEntity.data ? (
              <div>
                {ticketEntity.dataContentType ? (
                  <a onClick={openFile(ticketEntity.dataContentType, ticketEntity.data)}>
                    <Translate contentKey="entity.action.open">Open</Translate>&nbsp;
                  </a>
                ) : null}
                <span>
                  {ticketEntity.dataContentType}, {byteSize(ticketEntity.data)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="pricePerUnit">
              <Translate contentKey="hackaton3App.ticket.pricePerUnit">Price Per Unit</Translate>
            </span>
          </dt>
          <dd>{ticketEntity.pricePerUnit}</dd>
          <dt>
            <span id="finalAgentCommission">
              <Translate contentKey="hackaton3App.ticket.finalAgentCommission">Final Agent Commission</Translate>
            </span>
          </dt>
          <dd>{ticketEntity.finalAgentCommission}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="hackaton3App.ticket.status">Status</Translate>
            </span>
          </dt>
          <dd>{ticketEntity.status}</dd>
          <dt>
            <Translate contentKey="hackaton3App.ticket.event">Event</Translate>
          </dt>
          <dd>{ticketEntity.event ? ticketEntity.event.id : ''}</dd>
          <dt>
            <Translate contentKey="hackaton3App.ticket.payment">Payment</Translate>
          </dt>
          <dd>{ticketEntity.payment ? ticketEntity.payment.id : ''}</dd>
          <dt>
            <Translate contentKey="hackaton3App.ticket.ticketDelivery">Ticket Delivery</Translate>
          </dt>
          <dd>{ticketEntity.ticketDelivery ? ticketEntity.ticketDelivery.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/ticket" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/ticket/${ticketEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TicketDetail;
