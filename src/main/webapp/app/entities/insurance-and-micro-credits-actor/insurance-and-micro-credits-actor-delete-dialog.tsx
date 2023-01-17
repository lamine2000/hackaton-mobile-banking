import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntity, deleteEntity } from './insurance-and-micro-credits-actor.reducer';

export const InsuranceAndMicroCreditsActorDeleteDialog = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id));
    setLoadModal(true);
  }, []);

  const insuranceAndMicroCreditsActorEntity = useAppSelector(state => state.insuranceAndMicroCreditsActor.entity);
  const updateSuccess = useAppSelector(state => state.insuranceAndMicroCreditsActor.updateSuccess);

  const handleClose = () => {
    navigate('/insurance-and-micro-credits-actor' + location.search);
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmDelete = () => {
    dispatch(deleteEntity(insuranceAndMicroCreditsActorEntity.id));
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose} data-cy="insuranceAndMicroCreditsActorDeleteDialogHeading">
        <Translate contentKey="entity.delete.title">Confirm delete operation</Translate>
      </ModalHeader>
      <ModalBody id="hackaton3App.insuranceAndMicroCreditsActor.delete.question">
        <Translate
          contentKey="hackaton3App.insuranceAndMicroCreditsActor.delete.question"
          interpolate={{ id: insuranceAndMicroCreditsActorEntity.id }}
        >
          Are you sure you want to delete this InsuranceAndMicroCreditsActor?
        </Translate>
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button
          id="jhi-confirm-delete-insuranceAndMicroCreditsActor"
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

export default InsuranceAndMicroCreditsActorDeleteDialog;
