// https://www.iab.com/wp-content/uploads/2016/03/OpenRTB-Native-Ads-Specification-1-1_2016.pd

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
import {mimeTypes, protocolOptions, contextType, contextSubType, placementType} from "../../Utils";
import ViewAssets from "./ViewAssets";

var undef;

const NativeEditor = (props) => {

  const [rSelected, setRSelected] = useState(props.creative.dealType);
  const [count, setCount] = useState(0);
  const [assets, setAssets] = useState( props.creative.nativead.assets ); 

  const newTitleAsset = () => {
    var n = {
      type: 'title',
      text: ''
    };
    assets.push(n)
    setAssets(assets);
    setCount(count+1);
  }

  const newImageAsset = () => {
    var n = {
      type: 'image',
      url: '',
      w: 0,
      h: 0
    };
    assets.push(n)
    setAssets(assets);
    setCount(count+1);
  }

  const newVideoAsset = () => {
    var n = {
      type: 'video',
      vasttag: '',
    };
    assets.push(n)
    setAssets(assets);
    setCount(count+1);
  }

  const newDataAsset = () => {
    var n = {
      type: 'data',
      label: '',
      value: ''
    };
    assets.push(n)
    setAssets(assets);
    setCount(count+1);
  }

  const newLinkAsset = () => {
    var n = {
      type: 'link',
      url: '',
      clicktrackers: [],
      fallback: ''
    };
    assets.push(n)
    setAssets(assets);
    setCount(count+1);
  }

  const handler =(asset,i) => {
    assets[i] = asset;
    setAssets(assets);
  }

  const setAndRedraw = (assets) => {
    setAssets(assets);
    setCount(count+1);
  }

  //alert("PROPS:  " + JSON.stringify(props,null,2));

  return(
    <>
    <Row>
      <Col className="px-md-1" md="3">
        <FormGroup>
          <label>Context</label>
          <Input
            id="native_context"
            onChange={(e) => props.callback(e,"native_context")}
            type="select">
              {contextType(props.creative.nativead.native_context)}}
          </Input>
      </FormGroup>
      </Col>
      <Col className="px-md-1" md="3">
        <FormGroup>
          <label>Sub-Context</label>
          <Input
            id="native_contextsubtype"
            onChange={(e) => props.callback(e,"native_contextsubtype")}
            type="select">
              {contextSubType(props.creative.nativead.native_contextsubtype)}}
          </Input>
      </FormGroup>
      </Col>
      <Col className="px-md-1" md="3">
        <FormGroup>
          <label>Placement Type</label>
          <Input
            id="native_plcmttype"
            onChange={(e) => props.callback(e,"native_plcmttype")}
            type="select">
              {placementType(props.creative.nativead.native_plcmttype)}}
          </Input>
      </FormGroup>
      </Col>
    </Row>
    <Row>
       <ButtonGroup>
        <Label>Assets</Label>
        &nbsp;
        <Button color="success" size="sm" onClick={()=>newTitleAsset()}>Title</Button>
        &nbsp;
        <Button color="success" size="sm" onClick={()=>newImageAsset()}>Image</Button>
        &nbsp;
        <Button color="success" size="sm" onClick={()=>newVideoAsset()}>Video</Button>
        &nbsp;
        <Button color="success" size="sm" onClick={()=>newDataAsset()}>Data</Button>
        &nbsp;
        <Button color="success" size="sm" onClick={()=>newLinkAsset()}>Link</Button>
       </ButtonGroup>
       <hr/>
    </Row>
    <ViewAssets key={"assets-"+count} 
      assets={assets} 
      nativead={props.creative.nativead} 
      setter={setAndRedraw} 
      callback={handler} />
   </>
  );
};

export default NativeEditor;