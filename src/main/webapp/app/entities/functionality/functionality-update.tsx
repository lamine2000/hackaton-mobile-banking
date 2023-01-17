import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm, ValidatedBlobField } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IFunctionalityCategory } from 'app/shared/model/functionality-category.model';
import { getEntities as getFunctionalityCategories } from 'app/entities/functionality-category/functionality-category.reducer';
import { IMobileBankingActor } from 'app/shared/model/mobile-banking-actor.model';
import { getEntities as getMobileBankingActors } from 'app/entities/mobile-banking-actor/mobile-banking-actor.reducer';
import { IFunctionality } from 'app/shared/model/functionality.model';
import { FunctionalityStatus } from 'app/shared/model/enumerations/functionality-status.model';
import { getEntity, updateEntity, createEntity, reset } from './functionality.reducer';

export const FunctionalityUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const functionalityCategories = useAppSelector(state => state.functionalityCategory.entities);
  const mobileBankingActors = useAppSelector(state => state.mobileBankingActor.entities);
  const functionalityEntity = useAppSelector(state => state.functionality.entity);
  const loading = useAppSelector(state => state.functionality.loading);
  const updating = useAppSelector(state => state.functionality.updating);
  const updateSuccess = useAppSelector(state => state.functionality.updateSuccess);
  const functionalityStatusValues = Object.keys(FunctionalityStatus);

  const handleClose = () => {
    navigate('/functionality' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getFunctionalityCategories({}));
    dispatch(getMobileBankingActors({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...functionalityEntity,
      ...values,
      functionalityCategory: functionalityCategories.find(it => it.id.toString() === values.functionalityCategory.toString()),
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
          status: 'UNAVAILABLE',
          ...functionalityEntity,
          functionalityCategory: functionalityEntity?.functionalityCategory?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="hackaton3App.functionality.home.createOrEditLabel" data-cy="FunctionalityCreateUpdateHeading">
            <Translate contentKey="hackaton3App.functionality.home.createOrEditLabel">Create or edit a Functionality</Translate>
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
                  id="functionality-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedBlobField
                label={translate('hackaton3App.functionality.image')}
                id="functionality-image"
                name="image"
                data-cy="image"
                isImage
                accept="image/*"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('hackaton3App.functionality.status')}
                id="functionality-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {functionalityStatusValues.map(functionalityStatus => (
                  <option value={functionalityStatus} key={functionalityStatus}>
                    {translate('hackaton3App.FunctionalityStatus.' + functionalityStatus)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="functionality-functionalityCategory"
                name="functionalityCategory"
                data-cy="functionalityCategory"
                label={translate('hackaton3App.functionality.functionalityCategory')}
                type="select"
              >
                <option value="" key="0" />
                {functionalityCategories
                  ? functionalityCategories.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/functionality" replace color="info">
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

export default FunctionalityUpdate;
