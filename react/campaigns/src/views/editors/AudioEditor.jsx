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

var undef;

const AudioEditor = (props) => {

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
          defaultValue={props.creative.mimetype}
          onChange={ (e) => props.callback(e,"mime_type")}
          type="text"
        />
      </FormGroup>
    </Col>
    <Col className="px-md-1" md="1">
      <FormGroup>
        <label>Min Duration</label>
        <Input
          id="height"
          onChange={ (e) => props.callback(e,"audio_min_duration")}
          defaultValue={props.creative.audio_min_duration}
          type="text"
        />
      </FormGroup>
   </Col>
   <Col className="px-md-1" md="1">
      <FormGroup>
        <label>Max Duration</label>
        <Input
          id="height"
          onChange={ (e) => props.callback(e,"audio_max_duration")}
          defaultValue={props.creative.audio_max_duration}
          type="text"
        />
      </FormGroup>
   </Col>
   <Col className="px-md-1" md="1">
      <FormGroup>
        <label>Start Delay</label>
        <Input
          id="height"
          onChange={ (e) => props.callback(e,"audio_start_delay")}
          defaultValue={props.creative.audio_start_delay}
          type="text"
        />
      </FormGroup>
   </Col>
   <Col className="px-md-1" md="1">
      <FormGroup>
        <label>Min Bitrate</label>
        <Input
          id="height"
          onChange={ (e) => props.callback(e,"audio_min_bitrate")}
          defaultValue={props.creative.audio_min_bitrate}
          type="text"
        />
      </FormGroup>
   </Col>
   <Col className="px-md-1" md="1">
      <FormGroup>
        <label>Max Bitrate</label>
        <Input
          id="height"
          onChange={ (e) => props.callback(e,"audio_max_bitrate")}
          defaultValue={props.creative.audio_max_bitrate}
          type="text"
        />
      </FormGroup>
    </Col>
    <Col className="px-md-1" md="2">
      <FormGroup>
        <label>Supported Protocols</label>
        <Input
          id="height"
          onChange={ (e) => props.callback(e,"audio_protocols")}
          defaultValue={props.creative.audio_protocols}
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
          spellCheck={false}
          onChange={ (e) => props.callback(e,"audio_outgoing_file")}
          defaultValue={props.creative.audio_outgoing_file}
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

export default AudioEditor;