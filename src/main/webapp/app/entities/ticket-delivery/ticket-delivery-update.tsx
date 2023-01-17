import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ITicketDeliveryMethod } from 'app/shared/model/ticket-delivery-method.model';
import { getEntities as getTicketDeliveryMethods } from 'app/entities/ticket-delivery-method/ticket-delivery-method.reducer';
import { ITicketDelivery } from 'app/shared/model/ticket-delivery.model';
import { getEntity, updateEntity, createEntity, reset } from './ticket-delivery.reducer';

export const TicketDeliveryUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const ticketDeliveryMethods = useAppSelector(state => state.ticketDeliveryMethod.entities);
  const ticketDeliveryEntity = useAppSelector(state => state.ticketDelivery.entity);
  const loading = useAppSelector(state => state.ticketDelivery.loading);
  const updating = useAppSelector(state => state.ticketDelivery.updating);
  const updateSuccess = useAppSelector(state => state.ticketDelivery.updateSuccess);

  const handleClose = () => {
    navigate('/ticket-delivery' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getTicketDeliveryMethods({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.boughtAt = convertDateTimeToServer(values.boughtAt);

    const entity = {
      ...ticketDeliveryEntity,
      ...values,
      ticketDeliveryMethod: ticketDeliveryMethods.find(it => it.id.toString() === values.ticketDeliveryMethod.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          boughtAt: displayDefaultDateTime(),
        }
      : {
          ...ticketDeliveryEntity,
          boughtAt: convertDateTimeFromServer(ticketDeliveryEntity.boughtAt),
          ticketDeliveryMethod: ticketDeliveryEntity?.ticketDeliveryMethod?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="hackaton3App.ticketDelivery.home.createOrEditLabel" data-cy="TicketDeliveryCreateUpdateHeading">
            <Translate contentKey="hackaton3App.ticketDelivery.home.createOrEditLabel">Create or edit a TicketDelivery</Translate>
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
                  id="ticket-delivery-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('hackaton3App.ticketDelivery.boughtAt')}
                id="ticket-delivery-boughtAt"
                name="boughtAt"
                data-cy="boughtAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('hackaton3App.ticketDelivery.boughtBy')}
                id="ticket-delivery-boughtBy"
                name="boughtBy"
                data-cy="boughtBy"
                type="text"
              />
              <ValidatedField
                label={translate('hackaton3App.ticketDelivery.quantity')}
                id="ticket-delivery-quantity"
                name="quantity"
                data-cy="quantity"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                id="ticket-delivery-ticketDeliveryMethod"
                name="ticketDeliveryMethod"
                data-cy="ticketDeliveryMethod"
                label={translate('hackaton3App.ticketDelivery.ticketDeliveryMethod')}
                type="select"
              >
                <option value="" key="0" />
                {ticketDeliveryMethods
                  ? ticketDeliveryMethods.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/ticket-delivery" replace color="info">
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

export default TicketDeliveryUpdate;
