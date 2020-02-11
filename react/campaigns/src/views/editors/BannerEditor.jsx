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

const BannerEditor = (props) => {

  const [rSelected, setRSelected] = useState(props.creative.dealType);

  const setDealSelection = (r) => {
    setRSelected(r);
    props.selector(r);
  }

  return(
    <>
    <Row>
    <Col className="px-md-1" md="2">
      <FormGroup>
        <label>Content Type</label>
        <Input
          id="fixed-width"
          defaultValue={props.creative.contenttype}
          onChange={ (e) => props.callback(e,"contenttype")}
          type="text"
        />
      </FormGroup>
    </Col>
    <Col className="px-md-1" md="2">
      <FormGroup>
        <label>Image Url</label>
        <Input
          id="height"
          onChange={ (e) => props.callback(e,"imageurl")}
          defaultValue={props.creative.imageurl}
          type="text"
        />
      </FormGroup>
   </Col>
   </Row>
   <Row>
    <Col className="px-md-1" md="6">
      <FormGroup>
        <label>HTML Template</label>
        <Input
          id="height"
          onChange={ (e) => props.callback(e,"htmltemplate")}
          defaultValue={props.creative.htmltemplate}
          type="textarea"
        />
      </FormGroup>
      </Col>
   </Row>
   </>
  );
};

export default BannerEditor;