import React, { useState, useEffect } from 'react';
import { Button, Col, Input, FormGroup, Label, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import {useViewContext } from "./ViewContext";

var undef;

const LoginModal = (props) => {

  useEffect(() => {
    var c = localStorage.getItem('rtb4free_cm:customer');
    var u = localStorage.getItem('rtb4free_cm:username');
    var p = localStorage.getItem('rtb4free_cm:password');
    var s = localStorage.getItem('rtb4free_cm:server');
    if (! (c == null || u == null || p == null || s == null)) {
      setCustomer(c);
      setName(u);
      setPassword(p);
      setServer(s);
      setRemembered(true);
    } else {
      setRemembered(false);
    }
  }, []);

  const vx = useViewContext();

  const [customer, setCustomer] = useState('');
  const [name, setName] = useState('rtb4free');
  const [password, setPassword] = useState('');
  const [server, setServer] = useState(location.hostname+':7379');
  const [remembered, setRemembered] = useState(false);

  const changeCustomer = (event) => {
    setCustomer(event.target.value);
  }

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
    if (remembered) {
      localStorage.setItem('rtb4free_cm:customer',customer);
      localStorage.setItem('rtb4free_cm:username',name);
      localStorage.setItem('rtb4free_cm:password',password);
      localStorage.setItem('rtb4free_cm:server',server);
    } else {
      localStorage.removeItem('rtb4free_cm:customer');
      localStorage.removeItem('rtb4free_cm:username');
      localStorage.removeItem('rtb4free_cm:password');
      localStorage.removeItem('rtb4free_cm:server');
    }

    if (customer === undef || customer === '') {
      alert("Please provide an Org value");
      return;
    }
    if (name === undef || name === '') {
      alert("Please provide a User value");
      return;
    }
    if (password === undef || password === '') {
      alert("Please provide a Password value");
      return;
    }
    if (server === undef || server === '') {
      alert("Please provide a Server value");
      return;
    }

    var jwt = await vx.getToken(customer,name,password,server);
    if (jwt === undef) {
      alert("Could not login...");
      return;
    }

    await vx.getUser();
    await vx.getAccounting();
    await vx.getDbCampaigns();
    await vx.listRules();
    await vx.listTargets();
    await vx.listCreatives();
    await vx.listMacros();
    var mx = await vx.getBidders();
    if (mx === undef)
      return;
    console.log("Bidders = " + mx.length);
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
            <Label for="customer" sm={2}>Org:</Label>
            <Col sm={10}>
                <Input style={estyle} 
                  type="text" id="customer" 
                  defaultValue={customer}
                  placeholder={customer} onChange={changeCustomer}/>
            </Col>
        </FormGroup>
        <FormGroup row>
            <Label for="username" sm={2}>User:</Label>
            <Col sm={10}>
                <Input style={estyle} 
                  type="text" id="username" 
                  defaultValue={name}
                  placeholder={name} onChange={changeName}/>
            </Col>
        </FormGroup>
        <FormGroup row>
            <Label for="password" sm={2}>Password:</Label>
            <Col sm={10}>
                <Input style={estyle} type="password" 
                  id="password"  placeholder="********"  
                  defaultValue={password}
                  onChange={changePassword} />
            </Col>
        </FormGroup>
        <FormGroup row>
            <Label for="server" sm={2}>Server:</Label>
            <Col sm={10}>
             <Input style={estyle} 
              type="text" id="server"  
              placeholder={server}  
              defaultValue={server}
              onChange={changeServer} />
            </Col>
        </FormGroup>
        </ModalBody>
        <ModalFooter>
          <Button color="primary" onClick={login}>Login</Button>{' '}
              <Label check>
              <Input id="remember-me" type="checkbox" onChange={()=> setRemembered(!remembered)} checked={remembered} />{' '}
                Remember Me
              </Label>
        </ModalFooter>
      </Modal>
    </div>
    </>
  );
}

export default LoginModal;