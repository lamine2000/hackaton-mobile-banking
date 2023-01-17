import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm, ValidatedBlobField } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IEvent } from 'app/shared/model/event.model';
import { getEntities as getEvents } from 'app/entities/event/event.reducer';
import { IPayment } from 'app/shared/model/payment.model';
import { getEntities as getPayments } from 'app/entities/payment/payment.reducer';
import { ITicketDelivery } from 'app/shared/model/ticket-delivery.model';
import { getEntities as getTicketDeliveries } from 'app/entities/ticket-delivery/ticket-delivery.reducer';
import { ITicket } from 'app/shared/model/ticket.model';
import { TicketStatus } from 'app/shared/model/enumerations/ticket-status.model';
import { getEntity, updateEntity, createEntity, reset } from './ticket.reducer';

export const TicketUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const events = useAppSelector(state => state.event.entities);
  const payments = useAppSelector(state => state.payment.entities);
  const ticketDeliveries = useAppSelector(state => state.ticketDelivery.entities);
  const ticketEntity = useAppSelector(state => state.ticket.entity);
  const loading = useAppSelector(state => state.ticket.loading);
  const updating = useAppSelector(state => state.ticket.updating);
  const updateSuccess = useAppSelector(state => state.ticket.updateSuccess);
  const ticketStatusValues = Object.keys(TicketStatus);

  const handleClose = () => {
    navigate('/ticket' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getEvents({}));
    dispatch(getPayments({}));
    dispatch(getTicketDeliveries({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...ticketEntity,
      ...values,
      event: events.find(it => it.id.toString() === values.event.toString()),
      payment: payments.find(it => it.id.toString() === values.payment.toString()),
      ticketDelivery: ticketDeliveries.find(it => it.id.toString() === values.ticketDelivery.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          status: 'SOLD',
          ...ticketEntity,
          event: ticketEntity?.event?.id,
          payment: ticketEntity?.payment?.id,
          ticketDelivery: ticketEntity?.ticketDelivery?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="hackaton3App.ticket.home.createOrEditLabel" data-cy="TicketCreateUpdateHeading">
            <Translate contentKey="hackaton3App.ticket.home.createOrEditLabel">Create or edit a Ticket</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="ticket-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField label={translate('hackaton3App.ticket.code')} id="ticket-code" name="code" data-cy="code" type="text" />
              <ValidatedBlobField
                label={translate('hackaton3App.ticket.data')}
                id="ticket-data"
                name="data"
                data-cy="data"
                openActionLabel={translate('entity.action.open')}
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('hackaton3App.ticket.pricePerUnit')}
                id="ticket-pricePerUnit"
                name="pricePerUnit"
                data-cy="pricePerUnit"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('hackaton3App.ticket.finalAgentCommission')}
                id="ticket-finalAgentCommission"
                name="finalAgentCommission"
                data-cy="finalAgentCommission"
                type="text"
              />
              <ValidatedField
                label={translate('hackaton3App.ticket.status')}
                id="ticket-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {ticketStatusValues.map(ticketStatus => (
                  <option value={ticketStatus} key={ticketStatus}>
                    {translate('hackaton3App.TicketStatus.' + ticketStatus)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField id="ticket-event" name="event" data-cy="event" label={translate('hackaton3App.ticket.event')} type="select">
                <option value="" key="0" />
                {events
                  ? events.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="ticket-payment"
                name="payment"
                data-cy="payment"
                label={translate('hackaton3App.ticket.payment')}
                type="select"
              >
                <option value="" key="0" />
                {payments
                  ? payments.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="ticket-ticketDelivery"
                name="ticketDelivery"
                data-cy="ticketDelivery"
                label={translate('hackaton3App.ticket.ticketDelivery')}
                type="select"
              >
                <option value="" key="0" />
                {ticketDeliveries
                  ? ticketDeliveries.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/ticket" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default TicketUpdate;
