import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm, ValidatedBlobField } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IFunctionalityCategory } from 'app/shared/model/functionality-category.model';
import { FunctionalityCategoryStatus } from 'app/shared/model/enumerations/functionality-category-status.model';
import { getEntity, updateEntity, createEntity, reset } from './functionality-category.reducer';

export const FunctionalityCategoryUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const functionalityCategoryEntity = useAppSelector(state => state.functionalityCategory.entity);
  const loading = useAppSelector(state => state.functionalityCategory.loading);
  const updating = useAppSelector(state => state.functionalityCategory.updating);
  const updateSuccess = useAppSelector(state => state.functionalityCategory.updateSuccess);
  const functionalityCategoryStatusValues = Object.keys(FunctionalityCategoryStatus);

  const handleClose = () => {
    navigate('/functionality-category' + location.search);
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
      ...functionalityCategoryEntity,
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
          status: 'UNAVAILABLE',
          ...functionalityCategoryEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="hackaton3App.functionalityCategory.home.createOrEditLabel" data-cy="FunctionalityCategoryCreateUpdateHeading">
            <Translate contentKey="hackaton3App.functionalityCategory.home.createOrEditLabel">
              Create or edit a FunctionalityCategory
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
                  id="functionality-category-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedBlobField
                label={translate('hackaton3App.functionalityCategory.logo')}
                id="functionality-category-logo"
                name="logo"
                data-cy="logo"
                isImage
                accept="image/*"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('hackaton3App.functionalityCategory.status')}
                id="functionality-category-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {functionalityCategoryStatusValues.map(functionalityCategoryStatus => (
                  <option value={functionalityCategoryStatus} key={functionalityCategoryStatus}>
                    {translate('hackaton3App.FunctionalityCategoryStatus.' + functionalityCategoryStatus)}
                  </option>
                ))}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/functionality-category" replace color="info">
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

export default FunctionalityCategoryUpdate;
