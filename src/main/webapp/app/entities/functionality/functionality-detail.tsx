import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, openFile, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './functionality.reducer';

export const FunctionalityDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const functionalityEntity = useAppSelector(state => state.functionality.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="functionalityDetailsHeading">
          <Translate contentKey="hackaton3App.functionality.detail.title">Functionality</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{functionalityEntity.id}</dd>
          <dt>
            <span id="image">
              <Translate contentKey="hackaton3App.functionality.image">Image</Translate>
            </span>
          </dt>
          <dd>
            {functionalityEntity.image ? (
              <div>
                {functionalityEntity.imageContentType ? (
                  <a onClick={openFile(functionalityEntity.imageContentType, functionalityEntity.image)}>
                    <img
                      src={`data:${functionalityEntity.imageContentType};base64,${functionalityEntity.image}`}
                      style={{ maxHeight: '30px' }}
                    />
                  </a>
                ) : null}
                <span>
                  {functionalityEntity.imageContentType}, {byteSize(functionalityEntity.image)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="status">
              <Translate contentKey="hackaton3App.functionality.status">Status</Translate>
            </span>
          </dt>
          <dd>{functionalityEntity.status}</dd>
          <dt>
            <Translate contentKey="hackaton3App.functionality.functionalityCategory">Functionality Category</Translate>
          </dt>
          <dd>{functionalityEntity.functionalityCategory ? functionalityEntity.functionalityCategory.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/functionality" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/functionality/${functionalityEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default FunctionalityDetail;
