import React, { useState, useEffect } from "react";

// reactstrap components
import {
  Badge,
  Button,
  ButtonGroup,
  ButtonToolbar,
  Card,
  CardHeader,
  CardBody,
  CardFooter,
  CardText,

  CardTitle,
  Form,
  FormGroup,
  Input,
  InputGroup,
  InputGroupAddon,
  InputGroupText,
  Table,
  Label,
  Row,
  Col
} from "reactstrap";


var undef;

const DealEditor = (props) => {

  const [rSelected, setRSelected] = useState(props.creative.dealType);

  const setDealSelection = (r) => {
    setRSelected(r);
    props.selector(r);
  }

  return(
    <Row>
    <Col className="px-md-1" md="6">
  <ButtonGroup>
    <label>Deals:&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; </label>
    <Button color="primary" onClick={() => setDealSelection(1)} active={rSelected === 1}>No Deal</Button>
    <Button color="primary" onClick={() => setDealSelection(2)} active={rSelected === 2}>Private Only</Button>
    <Button color="primary" onClick={() => setDealSelection(3)} active={rSelected === 3}>Private Preferred</Button>
  </ButtonGroup>
  </Col>
  { rSelected === 2 && <>
    <Col className="px-md-1" md="2">
      <FormGroup>
        <label>Width</label>
        <Input
          id="fixed-width"
          defaultValue={props.creative.width}
          onChange={ (e) => props.callback(e,"width")}
          type="text"
        />
      </FormGroup>
    </Col>
  <Col className="px-md-1" md="2">
      <FormGroup>
        <label>Height</label>
        <Input
          id="height"
          onChange={ (e) => props.callback(e,"height")}
          defaultValue={props.creative.height}
          type="text"
        />
      </FormGroup>
  </Col>
 </>}
 { rSelected === 3 && <>
  <Col className="px-md-1" md="2">
      <FormGroup>
        <label>Width Range</label>
        <Input
          id="height"
          onChange={ (e) => props.callback(e,"width_range")}
          defaultValue={props.creative.width_range}
          placeHolder="n-n"
          type="text"
        />
      </FormGroup>
  </Col>
  <Col className="px-md-1" md="2">
      <FormGroup>
        <label>Height Range</label>
        <Input
          id="fixed-width"
          defaultValue={props.creative.height_range}
          onChange={ (e) => props.callback(e,"height_range")}
          placeHolder="n-n"
          type="text"
        />
      </FormGroup>
    </Col>
 </>}
</Row>
  );
};

export default DealEditor;