import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { IStore } from 'app/shared/model/store.model';
import { getEntities as getStores } from 'app/entities/store/store.reducer';
import { IIntermediateAgent } from 'app/shared/model/intermediate-agent.model';
import { AccountStatus } from 'app/shared/model/enumerations/account-status.model';
import { getEntity, updateEntity, createEntity, reset } from './intermediate-agent.reducer';

export const IntermediateAgentUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const stores = useAppSelector(state => state.store.entities);
  const intermediateAgentEntity = useAppSelector(state => state.intermediateAgent.entity);
  const loading = useAppSelector(state => state.intermediateAgent.loading);
  const updating = useAppSelector(state => state.intermediateAgent.updating);
  const updateSuccess = useAppSelector(state => state.intermediateAgent.updateSuccess);
  const accountStatusValues = Object.keys(AccountStatus);

  const handleClose = () => {
    navigate('/intermediate-agent' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getStores({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...intermediateAgentEntity,
      ...values,
      user: users.find(it => it.id.toString() === values.user.toString()),
      store: stores.find(it => it.id.toString() === values.store.toString()),
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
          ...intermediateAgentEntity,
          user: intermediateAgentEntity?.user?.id,
          store: intermediateAgentEntity?.store?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="hackaton3App.intermediateAgent.home.createOrEditLabel" data-cy="IntermediateAgentCreateUpdateHeading">
            <Translate contentKey="hackaton3App.intermediateAgent.home.createOrEditLabel">Create or edit a IntermediateAgent</Translate>
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
                  id="intermediate-agent-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('hackaton3App.intermediateAgent.firstName')}
                id="intermediate-agent-firstName"
                name="firstName"
                data-cy="firstName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('hackaton3App.intermediateAgent.lastName')}
                id="intermediate-agent-lastName"
                name="lastName"
                data-cy="lastName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('hackaton3App.intermediateAgent.email')}
                id="intermediate-agent-email"
                name="email"
                data-cy="email"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('hackaton3App.intermediateAgent.phone')}
                id="intermediate-agent-phone"
                name="phone"
                data-cy="phone"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('hackaton3App.intermediateAgent.addressLine1')}
                id="intermediate-agent-addressLine1"
                name="addressLine1"
                data-cy="addressLine1"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('hackaton3App.intermediateAgent.addressLine2')}
                id="intermediate-agent-addressLine2"
                name="addressLine2"
                data-cy="addressLine2"
                type="text"
              />
              <ValidatedField
                label={translate('hackaton3App.intermediateAgent.city')}
                id="intermediate-agent-city"
                name="city"
                data-cy="city"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('hackaton3App.intermediateAgent.status')}
                id="intermediate-agent-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {accountStatusValues.map(accountStatus => (
                  <option value={accountStatus} key={accountStatus}>
                    {translate('hackaton3App.AccountStatus.' + accountStatus)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('hackaton3App.intermediateAgent.commissionRate')}
                id="intermediate-agent-commissionRate"
                name="commissionRate"
                data-cy="commissionRate"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                id="intermediate-agent-user"
                name="user"
                data-cy="user"
                label={translate('hackaton3App.intermediateAgent.user')}
                type="select"
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="intermediate-agent-store"
                name="store"
                data-cy="store"
                label={translate('hackaton3App.intermediateAgent.store')}
                type="select"
              >
                <option value="" key="0" />
                {stores
                  ? stores.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/intermediate-agent" replace color="info">
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

export default IntermediateAgentUpdate;
