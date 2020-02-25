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
import DemoTag from "../simulator/DemoTag.jsx";
import {mimeTypes} from "../../Utils";
import { useViewContext } from "../../ViewContext";

var undef;

const BannerEditor = (props) => {

  const vx = useViewContext();
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
          type="select">
            {mimeTypes(props.creative.contenttype)}
        </Input>
      </FormGroup>
    </Col>
    <Col className="px-md-1" md="6">
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
    <Col className="px-md-1" sm={6}>
      <FormGroup>
        <label>HTML Template</label>
        <Input
          id="height"
          onChange={ (e) => props.callback(e,"htmltemplate")}
          defaultValue={props.creative.htmltemplate}
          spellCheck={false}
          rows={8}
          type="textarea"
        />
      </FormGroup>
      </Col>
      <Col className="px-md-1" md="6">
        <FormGroup>
          <label>Visualization</label>
          <DemoTag isVideo={false} adm={props.creative.htmltemplate} />
        </FormGroup>
      </Col>
   </Row>
   </>
  );
};

export default BannerEditor;