import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ITransac } from 'app/shared/model/transac.model';
import { CurrencyCode } from 'app/shared/model/enumerations/currency-code.model';
import { TransacType } from 'app/shared/model/enumerations/transac-type.model';
import { getEntity, updateEntity, createEntity, reset } from './transac.reducer';

export const TransacUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const transacEntity = useAppSelector(state => state.transac.entity);
  const loading = useAppSelector(state => state.transac.loading);
  const updating = useAppSelector(state => state.transac.updating);
  const updateSuccess = useAppSelector(state => state.transac.updateSuccess);
  const currencyCodeValues = Object.keys(CurrencyCode);
  const transacTypeValues = Object.keys(TransacType);

  const handleClose = () => {
    navigate('/transac' + location.search);
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
    values.createdAt = convertDateTimeToServer(values.createdAt);

    const entity = {
      ...transacEntity,
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
      ? {
          createdAt: displayDefaultDateTime(),
        }
      : {
          currency: 'XOF',
          type: 'DEPOSIT',
          ...transacEntity,
          createdAt: convertDateTimeFromServer(transacEntity.createdAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="hackaton3App.transac.home.createOrEditLabel" data-cy="TransacCreateUpdateHeading">
            <Translate contentKey="hackaton3App.transac.home.createOrEditLabel">Create or edit a Transac</Translate>
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
                  id="transac-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('hackaton3App.transac.code')}
                id="transac-code"
                name="code"
                data-cy="code"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('hackaton3App.transac.createdBy')}
                id="transac-createdBy"
                name="createdBy"
                data-cy="createdBy"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('hackaton3App.transac.createdAt')}
                id="transac-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('hackaton3App.transac.receiver')}
                id="transac-receiver"
                name="receiver"
                data-cy="receiver"
                type="text"
              />
              <ValidatedField
                label={translate('hackaton3App.transac.sender')}
                id="transac-sender"
                name="sender"
                data-cy="sender"
                type="text"
              />
              <ValidatedField
                label={translate('hackaton3App.transac.amount')}
                id="transac-amount"
                name="amount"
                data-cy="amount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('hackaton3App.transac.currency')}
                id="transac-currency"
                name="currency"
                data-cy="currency"
                type="select"
              >
                {currencyCodeValues.map(currencyCode => (
                  <option value={currencyCode} key={currencyCode}>
                    {translate('hackaton3App.CurrencyCode.' + currencyCode)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField label={translate('hackaton3App.transac.type')} id="transac-type" name="type" data-cy="type" type="select">
                {transacTypeValues.map(transacType => (
                  <option value={transacType} key={transacType}>
                    {translate('hackaton3App.TransacType.' + transacType)}
                  </option>
                ))}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/transac" replace color="info">
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

export default TransacUpdate;
