import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IInsuranceAndMicroCreditsActor } from 'app/shared/model/insurance-and-micro-credits-actor.model';
import { getEntities as getInsuranceAndMicroCreditsActors } from 'app/entities/insurance-and-micro-credits-actor/insurance-and-micro-credits-actor.reducer';
import { IPayment } from 'app/shared/model/payment.model';
import { getEntities as getPayments } from 'app/entities/payment/payment.reducer';
import { IInsuranceAndMicroCreditsContribution } from 'app/shared/model/insurance-and-micro-credits-contribution.model';
import { getEntity, updateEntity, createEntity, reset } from './insurance-and-micro-credits-contribution.reducer';

export const InsuranceAndMicroCreditsContributionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const insuranceAndMicroCreditsActors = useAppSelector(state => state.insuranceAndMicroCreditsActor.entities);
  const payments = useAppSelector(state => state.payment.entities);
  const insuranceAndMicroCreditsContributionEntity = useAppSelector(state => state.insuranceAndMicroCreditsContribution.entity);
  const loading = useAppSelector(state => state.insuranceAndMicroCreditsContribution.loading);
  const updating = useAppSelector(state => state.insuranceAndMicroCreditsContribution.updating);
  const updateSuccess = useAppSelector(state => state.insuranceAndMicroCreditsContribution.updateSuccess);

  const handleClose = () => {
    navigate('/insurance-and-micro-credits-contribution' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getInsuranceAndMicroCreditsActors({}));
    dispatch(getPayments({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...insuranceAndMicroCreditsContributionEntity,
      ...values,
      insuranceAndMicroCreditsActor: insuranceAndMicroCreditsActors.find(
        it => it.id.toString() === values.insuranceAndMicroCreditsActor.toString()
      ),
      payment: payments.find(it => it.id.toString() === values.payment.toString()),
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
          ...insuranceAndMicroCreditsContributionEntity,
          insuranceAndMicroCreditsActor: insuranceAndMicroCreditsContributionEntity?.insuranceAndMicroCreditsActor?.id,
          payment: insuranceAndMicroCreditsContributionEntity?.payment?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2
            id="hackaton3App.insuranceAndMicroCreditsContribution.home.createOrEditLabel"
            data-cy="InsuranceAndMicroCreditsContributionCreateUpdateHeading"
          >
            <Translate contentKey="hackaton3App.insuranceAndMicroCreditsContribution.home.createOrEditLabel">
              Create or edit a InsuranceAndMicroCreditsContribution
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
                  id="insurance-and-micro-credits-contribution-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('hackaton3App.insuranceAndMicroCreditsContribution.code')}
                id="insurance-and-micro-credits-contribution-code"
                name="code"
                data-cy="code"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="insurance-and-micro-credits-contribution-insuranceAndMicroCreditsActor"
                name="insuranceAndMicroCreditsActor"
                data-cy="insuranceAndMicroCreditsActor"
                label={translate('hackaton3App.insuranceAndMicroCreditsContribution.insuranceAndMicroCreditsActor')}
                type="select"
              >
                <option value="" key="0" />
                {insuranceAndMicroCreditsActors
                  ? insuranceAndMicroCreditsActors.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="insurance-and-micro-credits-contribution-payment"
                name="payment"
                data-cy="payment"
                label={translate('hackaton3App.insuranceAndMicroCreditsContribution.payment')}
                type="select"
              >
                <option value="" key="0" />
                {payments
                  ? payments.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button
                tag={Link}
                id="cancel-save"
                data-cy="entityCreateCancelButton"
                to="/insurance-and-micro-credits-contribution"
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

export default InsuranceAndMicroCreditsContributionUpdate;
