import React, { useState } from 'react';
import { 
    Input, 
    Button, 
    Col,
    Form,
    FormGroup,
    Modal, 
    ModalHeader, 
    ModalBody, 
    ModalFooter,
    Row
 } from 'reactstrap';

import {whiteStyle, blackStyle} from "./Utils"

const LoadSymbolModal = (props) => {
  const [modal, setModal] = useState(true);
  const [symbol, setSymbol] = useState(props.symbol);

  const toggle = (t) => {
    setModal(!modal);
    props.callback(symbol);
  }

  const change = (e,what) => {
    var s = symbol;
    s[what] = e.target.value;
    setSymbol(s);
  }

    // in dark mode, the text is white, but this won't work here.... It will appear invisible in the field
    let estyle = {
        color: 'black'
      }

  return (
      <div>
      <Modal isOpen={modal} toggle={toggle}>
        <ModalHeader toggle={toggle}>Add/Update Symbol</ModalHeader>
        <ModalBody>
            <Form>
                <Row>
                    <Col className="pr-md-1" md="6">
                    <FormGroup>
                        <label>S3</label>
                        <Input
                            style={estyle}
                            defaultValue={symbol.s3}
                            onChange={((e)=>change(e,"s3"))}
                            type="text"
                        />
                    </FormGroup>
                </Col>
                <Col className="pr-md-1" md="6">
                    <FormGroup>
                        <label>File</label>
                        <Input
                            style={estyle}
                            onChange={((e)=>change(e,"file"))}
                            defaultValue={symbol.file}
                            type="text"
                        />
                    </FormGroup>
                </Col>
            </Row>
            <Row>
                    <Col className="pr-md-1" md="6">
                    <FormGroup>
                        <label>Name</label>
                        <Input
                            style={estyle}
                            onChange={((e)=>change(e,"s3name"))}
                            defaultValue={symbol.name}
                            type="text"
                        />
                    </FormGroup>
                </Col>
                <Col className="pr-md-1" md="4">
                    <FormGroup>
                        <label>Type</label>
                        <Input
                            style={estyle}
                            onChange={((e)=>change(e,"type"))}
                            defaultValue={symbol.type}
                            type="text"
                        />
                    </FormGroup>
                </Col>
                <Col className="pr-md-1" md="4">
                    <FormGroup>
                        <label>Size</label>
                        <Input
                            style={estyle}
                            onChange={((e)=>change(e,"size"))}
                            defaultValue={symbol.size}
                            type="text"
                        />
                    </FormGroup>
                </Col>
            </Row>
          </Form>
        </ModalBody>
        <ModalFooter>
          <Button color="primary" onClick={(e) => toggle(true)} >Save</Button>{' '}
          <Button color="secondary" onClick={(e) => toggle(false)}>Cancel</Button>
        </ModalFooter>
      </Modal>
    </div>
  );
}

export default LoadSymbolModal;
