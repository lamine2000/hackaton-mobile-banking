import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ISupplyRequest } from 'app/shared/model/supply-request.model';
import { getEntities as getSupplyRequests } from 'app/entities/supply-request/supply-request.reducer';
import { ISupply } from 'app/shared/model/supply.model';
import { getEntity, updateEntity, createEntity, reset } from './supply.reducer';

export const SupplyUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const supplyRequests = useAppSelector(state => state.supplyRequest.entities);
  const supplyEntity = useAppSelector(state => state.supply.entity);
  const loading = useAppSelector(state => state.supply.loading);
  const updating = useAppSelector(state => state.supply.updating);
  const updateSuccess = useAppSelector(state => state.supply.updateSuccess);

  const handleClose = () => {
    navigate('/supply' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getSupplyRequests({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...supplyEntity,
      ...values,
      supplyRequest: supplyRequests.find(it => it.id.toString() === values.supplyRequest.toString()),
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
          ...supplyEntity,
          supplyRequest: supplyEntity?.supplyRequest?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="hackaton3App.supply.home.createOrEditLabel" data-cy="SupplyCreateUpdateHeading">
            <Translate contentKey="hackaton3App.supply.home.createOrEditLabel">Create or edit a Supply</Translate>
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
                  id="supply-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('hackaton3App.supply.receiver')}
                id="supply-receiver"
                name="receiver"
                data-cy="receiver"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="supply-supplyRequest"
                name="supplyRequest"
                data-cy="supplyRequest"
                label={translate('hackaton3App.supply.supplyRequest')}
                type="select"
              >
                <option value="" key="0" />
                {supplyRequests
                  ? supplyRequests.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/supply" replace color="info">
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

export default SupplyUpdate;
