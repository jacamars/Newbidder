import React, { useState, useEffect } from "react";
import MDEditor from '@uiw/react-md-editor';

// reactstrap components
import {
  Alert,
  Button,
  ButtonGroup,
  FormGroup,
  Input,
  Label,
  Row,
  Col
} from "reactstrap";
import DemoTag from "../simulator/DemoTag"
import BannerEditor from "./BannerEditor";
import CreativeSizeEditor from "./CreativeSizeEditor"
import {mimeTypes, protocolOptions, apiOptions} from "../../Utils"

var undef;

const AudioEditor = (props) => {

  const [rSelected, setRSelected] = useState(props.creative.dealType);
  const [showCompanion, setShowCompanion] = useState(false);
  const [companion, setCompanion] = useState({
    htmltemplate: '',
    sizeType: '0'
  });
  const [count, setCount] = useState(1);

  const setDealSelection = (r) => {
    setRSelected(r);
    props.selector(r);
  }

const newCompanion = () => {
  setShowCompanion(true);
}

const deleteCompanion = () => {
  setShowCompanion(false);
}

const setHtml = (e,type) => {
  companion[type]=e.target.value;
  setCompanion(companion);
  setCount(count+1);
}

// Callback for w/h in CreativeSizeEditor
const setSize = (e, key) => {
  if (e == null) {
    companion[key] = "0";
    setCompanion(companion)
    return;
  }

  companion[key] = e.target.value;
  setCompanion(companion);
}

// Set the dimension type
const setSizeType = (t) => {
  companion.sizeType = t;
  setCompanion(companion);
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
    <Col className="px-md-1" md="2">
      <FormGroup>
        <label>Supported Protocols</label>
        <Input
          id="height"
          onChange={ (e) => props.callback(e,"audio_protocol")}
          type="select">
                {protocolOptions(props.creative.audio_protocol)}
        </Input>
      </FormGroup>
    </Col>
    <Col className="px-md-1" md="2">
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
    <Col className="px-md-1" md="12">
      <FormGroup>
        <label>DAAST FILE</label>
        <MDEditor
            id="outgoingfile"
            value={props.creative.htmltemplate}
            commands={[]}
            height={400}
            preview="edit"
            onChange={(e)=>props.callback(e,"htmltemplate")}
          />
      </FormGroup>
    </Col>
    </Row>
    <Row>
    <Col className="px-md-1" md="6">
        <FormGroup>
          <label>Visualization</label>
          <DemoTag isVideo={true} adm={props.creative.htmltemplate} />
        </FormGroup>
    </Col>
   </Row>
   <Row>
      <ButtonGroup>
        <Label>Companion</Label>
          &nbsp;
        <Button color="success" size="sm" onClick={()=>newCompanion()}>Companion</Button>
      </ButtonGroup>
    </Row>
      {showCompanion &&  
        <Alert color="success">
        <Row>
            <Label>Banner Ad</Label>
              <Button color="warning" size="sm" onClick={()=>deleteCompanion()}>Remove</Button>
        </Row>
        <CreativeSizeEditor 
                  creative={companion} 
                  callback={setSize} 
                  selector={setSizeType}/>
        <BannerEditor key={"audio-companion-"-count} 
                  creative={companion} 
                  callback={setHtml}/>
        </Alert>
        }
   </>
  );
};

export default AudioEditor;