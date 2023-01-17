import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm, ValidatedBlobField } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IZone } from 'app/shared/model/zone.model';
import { getEntities as getZones } from 'app/entities/zone/zone.reducer';
import { ITown } from 'app/shared/model/town.model';
import { getEntities as getTowns } from 'app/entities/town/town.reducer';
import { IDepartment } from 'app/shared/model/department.model';
import { getEntities as getDepartments } from 'app/entities/department/department.reducer';
import { IRegion } from 'app/shared/model/region.model';
import { getEntities as getRegions } from 'app/entities/region/region.reducer';
import { ICountry } from 'app/shared/model/country.model';
import { getEntities as getCountries } from 'app/entities/country/country.reducer';
import { IStore } from 'app/shared/model/store.model';
import { CurrencyCode } from 'app/shared/model/enumerations/currency-code.model';
import { StoreStatus } from 'app/shared/model/enumerations/store-status.model';
import { getEntity, updateEntity, createEntity, reset } from './store.reducer';

export const StoreUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const zones = useAppSelector(state => state.zone.entities);
  const towns = useAppSelector(state => state.town.entities);
  const departments = useAppSelector(state => state.department.entities);
  const regions = useAppSelector(state => state.region.entities);
  const countries = useAppSelector(state => state.country.entities);
  const storeEntity = useAppSelector(state => state.store.entity);
  const loading = useAppSelector(state => state.store.loading);
  const updating = useAppSelector(state => state.store.updating);
  const updateSuccess = useAppSelector(state => state.store.updateSuccess);
  const currencyCodeValues = Object.keys(CurrencyCode);
  const storeStatusValues = Object.keys(StoreStatus);

  const handleClose = () => {
    navigate('/store' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getZones({}));
    dispatch(getTowns({}));
    dispatch(getDepartments({}));
    dispatch(getRegions({}));
    dispatch(getCountries({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...storeEntity,
      ...values,
      zone: zones.find(it => it.id.toString() === values.zone.toString()),
      town: towns.find(it => it.id.toString() === values.town.toString()),
      department: departments.find(it => it.id.toString() === values.department.toString()),
      region: regions.find(it => it.id.toString() === values.region.toString()),
      country: countries.find(it => it.id.toString() === values.country.toString()),
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
          currency: 'XOF',
          status: 'OPENED',
          ...storeEntity,
          zone: storeEntity?.zone?.id,
          town: storeEntity?.town?.id,
          department: storeEntity?.department?.id,
          region: storeEntity?.region?.id,
          country: storeEntity?.country?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="hackaton3App.store.home.createOrEditLabel" data-cy="StoreCreateUpdateHeading">
            <Translate contentKey="hackaton3App.store.home.createOrEditLabel">Create or edit a Store</Translate>
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
                  id="store-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('hackaton3App.store.code')}
                id="store-code"
                name="code"
                data-cy="code"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedBlobField
                label={translate('hackaton3App.store.location')}
                id="store-location"
                name="location"
                data-cy="location"
                openActionLabel={translate('entity.action.open')}
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('hackaton3App.store.address')}
                id="store-address"
                name="address"
                data-cy="address"
                type="text"
              />
              <ValidatedField
                label={translate('hackaton3App.store.name')}
                id="store-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('hackaton3App.store.description')}
                id="store-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField
                label={translate('hackaton3App.store.currency')}
                id="store-currency"
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
              <ValidatedField
                label={translate('hackaton3App.store.phone')}
                id="store-phone"
                name="phone"
                data-cy="phone"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('hackaton3App.store.notificationEmail')}
                id="store-notificationEmail"
                name="notificationEmail"
                data-cy="notificationEmail"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField label={translate('hackaton3App.store.status')} id="store-status" name="status" data-cy="status" type="select">
                {storeStatusValues.map(storeStatus => (
                  <option value={storeStatus} key={storeStatus}>
                    {translate('hackaton3App.StoreStatus.' + storeStatus)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('hackaton3App.store.aboutUs')}
                id="store-aboutUs"
                name="aboutUs"
                data-cy="aboutUs"
                type="textarea"
              />
              <ValidatedField id="store-zone" name="zone" data-cy="zone" label={translate('hackaton3App.store.zone')} type="select">
                <option value="" key="0" />
                {zones
                  ? zones.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="store-town" name="town" data-cy="town" label={translate('hackaton3App.store.town')} type="select">
                <option value="" key="0" />
                {towns
                  ? towns.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="store-department"
                name="department"
                data-cy="department"
                label={translate('hackaton3App.store.department')}
                type="select"
              >
                <option value="" key="0" />
                {departments
                  ? departments.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="store-region" name="region" data-cy="region" label={translate('hackaton3App.store.region')} type="select">
                <option value="" key="0" />
                {regions
                  ? regions.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="store-country"
                name="country"
                data-cy="country"
                label={translate('hackaton3App.store.country')}
                type="select"
              >
                <option value="" key="0" />
                {countries
                  ? countries.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/store" replace color="info">
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

export default StoreUpdate;
