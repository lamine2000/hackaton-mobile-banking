import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IPaymentMethod } from 'app/shared/model/payment-method.model';
import { PaymentMethodType } from 'app/shared/model/enumerations/payment-method-type.model';
import { getEntity, updateEntity, createEntity, reset } from './payment-method.reducer';

export const PaymentMethodUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const paymentMethodEntity = useAppSelector(state => state.paymentMethod.entity);
  const loading = useAppSelector(state => state.paymentMethod.loading);
  const updating = useAppSelector(state => state.paymentMethod.updating);
  const updateSuccess = useAppSelector(state => state.paymentMethod.updateSuccess);
  const paymentMethodTypeValues = Object.keys(PaymentMethodType);

  const handleClose = () => {
    navigate('/payment-method' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...paymentMethodEntity,
      ...values,
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
          type: 'CASH',
          ...paymentMethodEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="hackaton3App.paymentMethod.home.createOrEditLabel" data-cy="PaymentMethodCreateUpdateHeading">
            <Translate contentKey="hackaton3App.paymentMethod.home.createOrEditLabel">Create or edit a PaymentMethod</Translate>
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
                  id="payment-method-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('hackaton3App.paymentMethod.name')}
                id="payment-method-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('hackaton3App.paymentMethod.type')}
                id="payment-method-type"
                name="type"
                data-cy="type"
                type="select"
              >
                {paymentMethodTypeValues.map(paymentMethodType => (
                  <option value={paymentMethodType} key={paymentMethodType}>
                    {translate('hackaton3App.PaymentMethodType.' + paymentMethodType)}
                  </option>
                ))}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/payment-method" replace color="info">
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

export default PaymentMethodUpdate;
