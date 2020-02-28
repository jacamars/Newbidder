import React, { useState, useEffect } from "react";

// reactstrap components
import {
  FormGroup,
  Input,
  Row,
  Col
} from "reactstrap";
import DemoTag from "../simulator/DemoTag.jsx"
import {mimeTypes, protocolOptions, apiOptions} from "../../Utils"

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
          id="protocol-width"
          defaultValue={props.creative.mime_type}
          type="select">
            {mimeTypes(props.creative.mime_type)}
          </Input>
      </FormGroup>
    </Col>
    <Col className="px-md-1" md="1">
      <FormGroup>
        <label>Duration</label>
        <Input
          id="duration"
          onChange={ (e) => props.callback(e,"audio_duration")}
          defaultValue={props.creative.audio_duration}
          type="number"
        />
      </FormGroup>
   </Col>
   <Col className="px-md-1" md="1">
      <FormGroup>
        <label>Start Delay</label>
        <Input
          id="delay"
          onChange={ (e) => props.callback(e,"audio_start_delay")}
          defaultValue={props.creative.audio_start_delay}
          type="number"
        />
      </FormGroup>
   </Col>
   <Col className="px-md-1" md="1">
      <FormGroup>
        <label>Bitrate</label>
        <Input
          id="bitrate"
          onChange={ (e) => props.callback(e,"audio_bitrate")}
          defaultValue={props.creative.audio_bitrate}
          type="number"
        />
      </FormGroup>
   </Col>
    <Col className="px-md-1" md="1">
      <FormGroup>
        <label>Supported Protocols</label>
        <Input
          id="height"
          onChange={ (e) => props.callback(e,"audio_protocols")}
          type="select">
                {protocolOptions(props.creative.audio_protocols)}
        </Input>
      </FormGroup>
    </Col>
    <Col className="px-md-1" md="1">
      <FormGroup>
        <label>Supported API</label>
        <Input
          id="height"
          onChange={ (e) => props.callback(e,"audio_api")}
          type="select">
                {apiOptions(props.creative.audio_api)}
        </Input>
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

export default AudioEditor;