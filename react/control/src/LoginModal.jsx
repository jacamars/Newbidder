import React, { useState } from 'react';
import { Button, Col, Input, FormGroup, Label, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import {useViewContext } from "./ViewContext";

var undef;

const LoginModal = (props) => {

  const vx = useViewContext();

  const [name, setName] = useState('rtb4free');
  const [password, setPassword] = useState('');
  const [server, setServer] = useState('localhost:8080');

  const changeName = (event) => {
    setName(event.target.value);
  }

  const changePassword = (event) => {
    setPassword(event.target.value);
  }

  const changeServer = (event) => {
      setServer(event.target.value);
  }

  const login = async () => {
    var mx = await vx.getMembers(server);
    console.log("MEMBERS = " + mx.length);
    if (mx === undef)
      return;
    vx.changeLoginState(true);
    props.callback(mx,server);
  }

  // in dark mode, the text is white, but this won't work here.... It will appear invisible in the field
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
                <Input style={estyle} type="text" id="username" placeholder={name} onChange={changeName}/>
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
            <Input style={estyle} type="text" id="server"  placeholder={server}  onChange={changeServer} />
            </Col>
        </FormGroup>
        </ModalBody>
        <ModalFooter>
          <Button color="primary" onClick={login}>Login</Button>{' '}
        </ModalFooter>
      </Modal>
    </div>
    </>
  );
}

export default LoginModal;