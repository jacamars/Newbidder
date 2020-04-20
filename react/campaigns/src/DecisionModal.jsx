import React, { useState } from 'react';
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';

const DecisionModal = (props) => {
  const [modal, setModal] = useState(true);

  const toggle = (t) => {
    setModal(!modal);
    props.callback(t);
  }

  return (
      <div>
      <Modal isOpen={modal} toggle={toggle}>
        <ModalHeader toggle={toggle}>{props.title}</ModalHeader>
        <ModalBody>
           {props.message}
        </ModalBody>
        <ModalFooter>
          <Button color="primary" onClick={(e) => toggle(true)}>{props.name}</Button>{' '}
          <Button color="secondary" onClick={(e) => toggle(false)}>Cancel</Button>
        </ModalFooter>
      </Modal>
    </div>
  );
}

export default DecisionModal;
