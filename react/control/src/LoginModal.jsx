import React, { useState } from 'react';
import { Button, Col, Input, FormGroup, Label, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import {useViewContext } from "./ViewContext";

const LoginModal = () => {

  const vx = useViewContext();

  const [name, setName] = useState('');
  const [password, setPassword] = useState('');

  const changeName = (event) => {
    setName(event.target.value);
  }

  const changePassword = (event) => {
    setPassword(event.target.value);
  }

  const toggle = () => { vx.changeLoginState(true)}
  const cancel = () => { 

  }

  let estyle = {
    color: 'black'
}

  return (
    <>
    <div>
      <Modal isOpen={!vx.loggedIn}>
        <ModalBody>
        <FormGroup row>
            <Label for="username" sm={2}>User</Label>
            <Col sm={10}>
                <Input style={estyle} type="text" id="username" onChange={changeName}/>
            </Col>
        </FormGroup>
        <FormGroup row>
            <Label for="password" sm={2}>Password:</Label>
            <Col sm={10}>
                <Input style={estyle} type="password" id="password"  placeholder="********"  onChange={changePassword} />
            </Col>
        </FormGroup>
        <FormGroup row>
            <Label for="server" sm={2}>Server:</Label>
            <Col sm={10}>
               <select>
                   <option>1</option>
                   <option>2</option>
               </select>
            </Col>
        </FormGroup>
        </ModalBody>
        <ModalFooter>
          <Button color="primary" onClick={toggle}>Login</Button>{' '}
          <Button color="secondary" onClick={cancel}>Cancel</Button>
        </ModalFooter>
      </Modal>
    </div>
    </>
  );
}

export default LoginModal;