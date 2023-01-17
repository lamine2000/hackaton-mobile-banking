import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntity, deleteEntity } from './insurance-and-micro-credits-contribution.reducer';

export const InsuranceAndMicroCreditsContributionDeleteDialog = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id));
    setLoadModal(true);
  }, []);

  const insuranceAndMicroCreditsContributionEntity = useAppSelector(state => state.insuranceAndMicroCreditsContribution.entity);
  const updateSuccess = useAppSelector(state => state.insuranceAndMicroCreditsContribution.updateSuccess);

  const handleClose = () => {
    navigate('/insurance-and-micro-credits-contribution' + location.search);
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmDelete = () => {
    dispatch(deleteEntity(insuranceAndMicroCreditsContributionEntity.id));
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose} data-cy="insuranceAndMicroCreditsContributionDeleteDialogHeading">
        <Translate contentKey="entity.delete.title">Confirm delete operation</Translate>
      </ModalHeader>
      <ModalBody id="hackaton3App.insuranceAndMicroCreditsContribution.delete.question">
        <Translate
          contentKey="hackaton3App.insuranceAndMicroCreditsContribution.delete.question"
          interpolate={{ id: insuranceAndMicroCreditsContributionEntity.id }}
        >
          Are you sure you want to delete this InsuranceAndMicroCreditsContribution?
        </Translate>
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button
          id="jhi-confirm-delete-insuranceAndMicroCreditsContribution"
          data-cy="entityConfirmDeleteButton"
          color="danger"
          onClick={confirmDelete}
        >
          <FontAwesomeIcon icon="trash" />
          &nbsp;
          <Translate contentKey="entity.action.delete">Delete</Translate>
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default InsuranceAndMicroCreditsContributionDeleteDialog;
