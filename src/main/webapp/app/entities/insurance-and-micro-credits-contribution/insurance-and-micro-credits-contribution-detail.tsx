import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './insurance-and-micro-credits-contribution.reducer';

export const InsuranceAndMicroCreditsContributionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const insuranceAndMicroCreditsContributionEntity = useAppSelector(state => state.insuranceAndMicroCreditsContribution.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="insuranceAndMicroCreditsContributionDetailsHeading">
          <Translate contentKey="hackaton3App.insuranceAndMicroCreditsContribution.detail.title">
            InsuranceAndMicroCreditsContribution
          </Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{insuranceAndMicroCreditsContributionEntity.id}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="hackaton3App.insuranceAndMicroCreditsContribution.code">Code</Translate>
            </span>
          </dt>
          <dd>{insuranceAndMicroCreditsContributionEntity.code}</dd>
          <dt>
            <Translate contentKey="hackaton3App.insuranceAndMicroCreditsContribution.insuranceAndMicroCreditsActor">
              Insurance And Micro Credits Actor
            </Translate>
          </dt>
          <dd>
            {insuranceAndMicroCreditsContributionEntity.insuranceAndMicroCreditsActor
              ? insuranceAndMicroCreditsContributionEntity.insuranceAndMicroCreditsActor.id
              : ''}
          </dd>
          <dt>
            <Translate contentKey="hackaton3App.insuranceAndMicroCreditsContribution.payment">Payment</Translate>
          </dt>
          <dd>{insuranceAndMicroCreditsContributionEntity.payment ? insuranceAndMicroCreditsContributionEntity.payment.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/insurance-and-micro-credits-contribution" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button
          tag={Link}
          to={`/insurance-and-micro-credits-contribution/${insuranceAndMicroCreditsContributionEntity.id}/edit`}
          replace
          color="primary"
        >
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default InsuranceAndMicroCreditsContributionDetail;
