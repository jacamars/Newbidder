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

const CreativeSizeEditor = (props) => {

  const [rSelected, setRSelected] = useState(props.creative.sizeType);

  const setSelection = (r) => {
    if (r == 1) {
      props.callback(null,"width");
      props.callback(null,"height");
    }
    setRSelected(r);
    props.selector(r);
  }

  return(
    <Row>
    <Col className="px-md-1" md="7">
  <ButtonGroup>
    <label>Match Size:</label>
    <Button color="primary" onClick={() => setSelection(1)} active={rSelected === 1}>Any</Button>
    <Button color="primary" onClick={() => setSelection(2)} active={rSelected === 2}>Specified</Button>
    <Button color="primary" onClick={() => setSelection(3)} active={rSelected === 3}>W/H Ranges</Button>
    <Button color="primary" onClick={() => setSelection(4)} active={rSelected === 3}>W/H List</Button>
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
          type="number"
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
          type="number"
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
 { rSelected === 4 && <>
  <Col className="px-md-1" md="4">
      <FormGroup>
        <label>List of W/H</label>
        <Input type="textarea" 
        id="domain" 
        placeholder="List of WxH..."
        onChange={ (e) => props.callback(e,"width_height_list")}
        defaultValue={props.creative.width_height_list}/>   
      </FormGroup>
  </Col>
 </>}
</Row>
  );
};

export default CreativeSizeEditor;