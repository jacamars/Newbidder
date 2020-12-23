import React, { useState, useEffect } from "react";
import MDEditor from '@uiw/react-md-editor';

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
import DemoTag from "../simulator/DemoTag.jsx"
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
          id="mime_type"
          spellCheck={false}
          onChange={ (e) => props.callback(e,"mime_type",props.index)}
          type="select">
            {mimeTypes(props.creative.mime_type)}
        </Input>
      </FormGroup>
    </Col>
    <Col className="px-md-1" md="2">
      <FormGroup>
        <label>Linearity</label>
        <Input
          id="vast_video_linearity"
          onChange={ (e) => props.callback(e,"vast_video_linearity",props.index)}
          defaultValue={props.creative.vast_video_linearity}
          type="number"
        />
      </FormGroup>
   </Col>
   <Col className="px-md-1" md="1">
      <FormGroup>
        <label>Duration</label>
        <Input
          id="vast_video_duration"
          onChange={ (e) => props.callback(e,"vast_video_duration",props.index)}
          defaultValue={props.creative.vast_video_duration}
          type="number"
        />
      </FormGroup>
   </Col>
   <Col className="px-md-1" md="2">
      <FormGroup>
        <label>VAST Video Protocol</label>
        <Input
          id="vast_video_protocol"
          type="select"
          onChange={ (e) => props.callback(e,"vast_video_protocol",props.index)}>
              {protocolOptions(props.creative.vast_video_protocol)}
         </Input>
      </FormGroup>
   </Col>
   <Col className="px-md-1" md="1">
      <FormGroup>
        <label>Bit Rate</label>
        <Input
          id="vast_video_bitrate"
          onChange={ (e) => props.callback(e,"vast_video_bitrate",props.index)}
          defaultValue={props.creative.vast_video_bitrate}
          type="number"
        />
      </FormGroup>
   </Col>
   </Row>
   <Row>
    <Col className="px-md-1" md="12">
      <FormGroup>
        <label>Outgoing File</label>
        <MDEditor
            value={props.creative.htmltemplate}
            commands={[]}
            height={300}
            preview="edit"
            onChange={(e)=>props.callback(e,"htmltemplate",props.index)}
          />
          </FormGroup>
        </Col>
      </Row>
      <Row>
      <Col className="px-md-1" md="12">
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