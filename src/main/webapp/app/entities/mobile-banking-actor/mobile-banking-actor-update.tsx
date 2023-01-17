import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm, ValidatedBlobField } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IFunctionality } from 'app/shared/model/functionality.model';
import { getEntities as getFunctionalities } from 'app/entities/functionality/functionality.reducer';
import { IMobileBankingActor } from 'app/shared/model/mobile-banking-actor.model';
import { MobileBankingActorStatus } from 'app/shared/model/enumerations/mobile-banking-actor-status.model';
import { getEntity, updateEntity, createEntity, reset } from './mobile-banking-actor.reducer';

export const MobileBankingActorUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const functionalities = useAppSelector(state => state.functionality.entities);
  const mobileBankingActorEntity = useAppSelector(state => state.mobileBankingActor.entity);
  const loading = useAppSelector(state => state.mobileBankingActor.loading);
  const updating = useAppSelector(state => state.mobileBankingActor.updating);
  const updateSuccess = useAppSelector(state => state.mobileBankingActor.updateSuccess);
  const mobileBankingActorStatusValues = Object.keys(MobileBankingActorStatus);

  const handleClose = () => {
    navigate('/mobile-banking-actor' + location.search);
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
      ...mobileBankingActorEntity,
      ...values,
      functionalities: mapIdList(values.functionalities),
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
          status: 'AVAILABLE',
          ...mobileBankingActorEntity,
          functionalities: mobileBankingActorEntity?.functionalities?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="hackaton3App.mobileBankingActor.home.createOrEditLabel" data-cy="MobileBankingActorCreateUpdateHeading">
            <Translate contentKey="hackaton3App.mobileBankingActor.home.createOrEditLabel">Create or edit a MobileBankingActor</Translate>
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
                  id="mobile-banking-actor-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedBlobField
                label={translate('hackaton3App.mobileBankingActor.logo')}
                id="mobile-banking-actor-logo"
                name="logo"
                data-cy="logo"
                isImage
                accept="image/*"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('hackaton3App.mobileBankingActor.name')}
                id="mobile-banking-actor-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('hackaton3App.mobileBankingActor.status')}
                id="mobile-banking-actor-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {mobileBankingActorStatusValues.map(mobileBankingActorStatus => (
                  <option value={mobileBankingActorStatus} key={mobileBankingActorStatus}>
                    {translate('hackaton3App.MobileBankingActorStatus.' + mobileBankingActorStatus)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('hackaton3App.mobileBankingActor.functionality')}
                id="mobile-banking-actor-functionality"
                data-cy="functionality"
                type="select"
                multiple
                name="functionalities"
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/mobile-banking-actor" replace color="info">
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

export default MobileBankingActorUpdate;
