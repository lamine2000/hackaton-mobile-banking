import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IFunctionality } from 'app/shared/model/functionality.model';
import { getEntities as getFunctionalities } from 'app/entities/functionality/functionality.reducer';
import { ISupplyRequest } from 'app/shared/model/supply-request.model';
import { SupplyRequestStatus } from 'app/shared/model/enumerations/supply-request-status.model';
import { getEntity, updateEntity, createEntity, reset } from './supply-request.reducer';

export const SupplyRequestUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const functionalities = useAppSelector(state => state.functionality.entities);
  const supplyRequestEntity = useAppSelector(state => state.supplyRequest.entity);
  const loading = useAppSelector(state => state.supplyRequest.loading);
  const updating = useAppSelector(state => state.supplyRequest.updating);
  const updateSuccess = useAppSelector(state => state.supplyRequest.updateSuccess);
  const supplyRequestStatusValues = Object.keys(SupplyRequestStatus);

  const handleClose = () => {
    navigate('/supply-request' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getFunctionalities({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...supplyRequestEntity,
      ...values,
      functionality: functionalities.find(it => it.id.toString() === values.functionality.toString()),
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
          status: 'PENDING',
          ...supplyRequestEntity,
          functionality: supplyRequestEntity?.functionality?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="hackaton3App.supplyRequest.home.createOrEditLabel" data-cy="SupplyRequestCreateUpdateHeading">
            <Translate contentKey="hackaton3App.supplyRequest.home.createOrEditLabel">Create or edit a SupplyRequest</Translate>
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
                  id="supply-request-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('hackaton3App.supplyRequest.amount')}
                id="supply-request-amount"
                name="amount"
                data-cy="amount"
                type="text"
              />
              <ValidatedField
                label={translate('hackaton3App.supplyRequest.quantity')}
                id="supply-request-quantity"
                name="quantity"
                data-cy="quantity"
                type="text"
              />
              <ValidatedField
                label={translate('hackaton3App.supplyRequest.status')}
                id="supply-request-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {supplyRequestStatusValues.map(supplyRequestStatus => (
                  <option value={supplyRequestStatus} key={supplyRequestStatus}>
                    {translate('hackaton3App.SupplyRequestStatus.' + supplyRequestStatus)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="supply-request-functionality"
                name="functionality"
                data-cy="functionality"
                label={translate('hackaton3App.supplyRequest.functionality')}
                type="select"
              >
                <option value="" key="0" />
                {functionalities
                  ? functionalities.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/supply-request" replace color="info">
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

export default SupplyRequestUpdate;
