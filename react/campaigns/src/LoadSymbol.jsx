import React, { useState } from 'react';
import { 
    Input, 
    Button, 
    Card,
    CardBody,
    CardFooter,
    CardHeader,
    Col,
    Form,
    FormGroup,
    Modal, 
    ModalHeader, 
    ModalBody, 
    ModalFooter,
    Row
 } from 'reactstrap';

import {whiteStyle, blackStyle, lookingGlassOptions} from "./Utils"

const LoadSymbolModal = (props) => {
  const [modal, setModal] = useState(true);
  const [symbol, setSymbol] = useState(props.symbol);
  const [count, setCount] = useState(0);

  const toggle = (t) => {
    setModal(!modal);
    props.callback(symbol);
  }

  const change = (e,what) => {
    var s = symbol;
    s[what] = e.target.value;
    setSymbol(s);

    if (s.filename !== "" && s.s3 !== "") {
        alert("Warning S3 and Filename can't both be present");
    }
    if (what === "type")    // force redraw because type affects visibility of size
        setCount(count+1);
  }

  const needSize = () => {
    return (
        symbol.type.toLowerCase().indexOf("bloom") > -1 || symbol.type.toLowerCase().indexOf("cuckoo") > -1
    );
  }


  return (
        <Card key={"counter-"+count}>
            <CardHeader>
                <h5 className="title">Edit Symbols</h5>
            </CardHeader>
            <CardBody>
            <Row>
                    <Col className="pr-md-1" md="4">
                    <FormGroup>
                        <label>Name</label>
                        <Input
                            style={(document.body.classList.contains("white-content")) 
                            ? blackStyle : whiteStyle}
                            onChange={((e)=>change(e,"name"))}
                            defaultValue={symbol.name}
                            type="text"
                        />
                    </FormGroup>
                </Col>
                <Col className="pr-md-1" md="2">
                    <FormGroup>
                        <label>Type</label>
                        <Input
                            id="fixed-width"
                            onChange={ (e) => change(e,"type")}
                            type="select">
                                {lookingGlassOptions(symbol.type)}
                        </Input>
                    </FormGroup>
                </Col>
                <Col className="pr-md-1" md="1">
                    { needSize() &&
                    <FormGroup>
                        <label>Size</label>
                        <Input
                            style={(document.body.classList.contains("white-content")) 
                            ? blackStyle : whiteStyle}
                            onChange={((e)=>change(e,"size"))}
                            defaultValue={symbol.size}
                            type="text"
                        />
                    </FormGroup>}
                </Col>
            </Row>
            <Row>
                    <Col className="pr-md-1" md="4">
                    <FormGroup>
                        <label>S3</label>
                        <Input
                            style={(document.body.classList.contains("white-content")) 
                                ? blackStyle : whiteStyle}
                            defaultValue={symbol.s3}
                            onChange={((e)=>change(e,"s3"))}
                            type="text"
                        />
                    </FormGroup>
                </Col>
                <Col className="pr-md-1" md="4">
                    <FormGroup>
                        <label>File</label>
                        <Input
                            style={(document.body.classList.contains("white-content")) 
                                ? blackStyle : whiteStyle}
                            onChange={((e)=>change(e,"filename"))}
                            defaultValue={symbol.filename}
                            type="text"
                        />
                    </FormGroup>
                </Col>
            </Row>
            <CardFooter>
                <Button className="btn-fill" color="primary" 
                    type="submit" onClick={() => props.callback(true)}>
                    Save
                </Button>
                <Button className="btn-fill" color="danger" type="submit" 
                    onClick={() => props.callback(false)}>
                    Discard
                </Button>
            </CardFooter>
          </CardBody>
    </Card>
       
  );
}

export default LoadSymbolModal;
