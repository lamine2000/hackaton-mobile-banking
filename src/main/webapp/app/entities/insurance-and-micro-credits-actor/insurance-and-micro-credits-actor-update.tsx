import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm, ValidatedBlobField } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IInsuranceAndMicroCreditsActor } from 'app/shared/model/insurance-and-micro-credits-actor.model';
import { getEntity, updateEntity, createEntity, reset } from './insurance-and-micro-credits-actor.reducer';

export const InsuranceAndMicroCreditsActorUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const insuranceAndMicroCreditsActorEntity = useAppSelector(state => state.insuranceAndMicroCreditsActor.entity);
  const loading = useAppSelector(state => state.insuranceAndMicroCreditsActor.loading);
  const updating = useAppSelector(state => state.insuranceAndMicroCreditsActor.updating);
  const updateSuccess = useAppSelector(state => state.insuranceAndMicroCreditsActor.updateSuccess);

  const handleClose = () => {
    navigate('/insurance-and-micro-credits-actor' + location.search);
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
      ...insuranceAndMicroCreditsActorEntity,
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
          ...insuranceAndMicroCreditsActorEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2
            id="hackaton3App.insuranceAndMicroCreditsActor.home.createOrEditLabel"
            data-cy="InsuranceAndMicroCreditsActorCreateUpdateHeading"
          >
            <Translate contentKey="hackaton3App.insuranceAndMicroCreditsActor.home.createOrEditLabel">
              Create or edit a InsuranceAndMicroCreditsActor
            </Translate>
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
                  id="insurance-and-micro-credits-actor-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedBlobField
                label={translate('hackaton3App.insuranceAndMicroCreditsActor.logo')}
                id="insurance-and-micro-credits-actor-logo"
                name="logo"
                data-cy="logo"
                isImage
                accept="image/*"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('hackaton3App.insuranceAndMicroCreditsActor.name')}
                id="insurance-and-micro-credits-actor-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('hackaton3App.insuranceAndMicroCreditsActor.acronym')}
                id="insurance-and-micro-credits-actor-acronym"
                name="acronym"
                data-cy="acronym"
                type="text"
              />
              <ValidatedField
                label={translate('hackaton3App.insuranceAndMicroCreditsActor.description')}
                id="insurance-and-micro-credits-actor-description"
                name="description"
                data-cy="description"
                type="textarea"
              />
              <Button
                tag={Link}
                id="cancel-save"
                data-cy="entityCreateCancelButton"
                to="/insurance-and-micro-credits-actor"
                replace
                color="info"
              >
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

export default InsuranceAndMicroCreditsActorUpdate;
