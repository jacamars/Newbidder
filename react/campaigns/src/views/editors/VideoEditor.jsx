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
import DemoTag from "./DemoTag.jsx"
import {mimeTypes, protocolOptions} from "../../Utils"

var undef;

const VideoEditor = (props) => {

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
        <label>Mime Type</label>
        <Input
          id="fixed-width"
          spellCheck={false}
          onChange={ (e) => props.callback(e,"mime_type")}
          type="select">
            {mimeTypes(props.creative.mime_type)}
        </Input>
      </FormGroup>
    </Col>
    <Col className="px-md-1" md="2">
      <FormGroup>
        <label>Linearity</label>
        <Input
          id="height"
          onChange={ (e) => props.callback(e,"vast_video_linearity")}
          defaultValue={props.creative.vast_video_linearity}
          type="text"
        />
      </FormGroup>
   </Col>
   <Col className="px-md-1" md="1">
      <FormGroup>
        <label>Duration</label>
        <Input
          id="height"
          onChange={ (e) => props.callback(e,"vast_video_duration")}
          defaultValue={props.creative.vast_video_duration}
          type="text"
        />
      </FormGroup>
   </Col>
   <Col className="px-md-1" md="2">
      <FormGroup>
        <label>VAST Video Protocol</label>
        <Input
          id="protocol"
          type="select"
          onChange={ (e) => props.callback(e,"vast_video_protocol")}>
              {protocolOptions(props.creative.vast_video_protocol)}
         </Input>
      </FormGroup>
   </Col>
   <Col className="px-md-1" md="1">
      <FormGroup>
        <label>Bit Rate</label>
        <Input
          id="height"
          onChange={ (e) => props.callback(e,"vast_video_bitrate")}
          defaultValue={props.creative.vast_video_bitrate}
          type="text"
        />
      </FormGroup>
   </Col>
   </Row>
   <Row>
    <Col className="px-md-1" md="6">
      <FormGroup>
        <label>Outgoing File</label>
        <Input
          id="height"
          onChange={ (e) => props.callback(e,"htmltemplate")}
          defaultValue={props.creative.htmltemplate}
          type="textarea"
        />
      </FormGroup>
      </Col>
      <Col className="px-md-1" md="6">
        <FormGroup>
          <label>Visualization</label>
          <DemoTag isVideo={true} adm={props.creative.htmltemplate} />
        </FormGroup>
      </Col>
   </Row>
   </>
  );
};

export default VideoEditor;