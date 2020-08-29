import React, { useState } from 'react';
import { Input, Button, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import {uuidv4} from "./Utils"

const DecisionModal = (props) => {
  const [modal, setModal] = useState(true);
  const [inputValue, setInputValue] = useState(uuidv4);

  const toggle = (t) => {
    setModal(!modal);
    props.callback(t,inputValue);
  }

  const addToInput = (e) => {
    var s = e.target.value;
    setInputValue(s)
  }

    // in dark mode, the text is white, but this won't work here.... It will appear invisible in the field
    let estyle = {
      color: 'black'
    }

  return (
      <div>
      <Modal isOpen={modal} toggle={toggle}>
        <ModalHeader toggle={toggle}>{props.title}</ModalHeader>
        <ModalBody>
           {props.message}
           {(props.input) && 
              <Input
                style={estyle}
                type="text"
                id={"modal-input"}
                defaultValue={inputValue}
                onChange={(e) => addToInput(e)}/>
            }
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
