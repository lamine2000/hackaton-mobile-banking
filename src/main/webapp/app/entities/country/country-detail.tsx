import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './country.reducer';

export const CountryDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const countryEntity = useAppSelector(state => state.country.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="countryDetailsHeading">
          <Translate contentKey="hackaton3App.country.detail.title">Country</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{countryEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="hackaton3App.country.name">Name</Translate>
            </span>
          </dt>
          <dd>{countryEntity.name}</dd>
          <dt>
            <span id="codeAlpha">
              <Translate contentKey="hackaton3App.country.codeAlpha">Code Alpha</Translate>
            </span>
          </dt>
          <dd>{countryEntity.codeAlpha}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="hackaton3App.country.code">Code</Translate>
            </span>
          </dt>
          <dd>{countryEntity.code}</dd>
          <dt>
            <span id="flag">
              <Translate contentKey="hackaton3App.country.flag">Flag</Translate>
            </span>
          </dt>
          <dd>{countryEntity.flag}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="hackaton3App.country.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{countryEntity.createdAt ? <TextFormat value={countryEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="createdBy">
              <Translate contentKey="hackaton3App.country.createdBy">Created By</Translate>
            </span>
          </dt>
          <dd>{countryEntity.createdBy}</dd>
        </dl>
        <Button tag={Link} to="/country" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/country/${countryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CountryDetail;
