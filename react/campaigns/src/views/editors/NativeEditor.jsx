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
import {mimeTypes, protocolOptions, contextType, contextSubType} from "../../Utils";
import ViewAssets from "./ViewAssets";

var undef;

const NativeEditor = (props) => {

  const [rSelected, setRSelected] = useState(props.creative.dealType);
  const [count, setCount] = useState(0);
  const [assets, setAssets] = useState( props.creative.native_assets ); 

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

  return(
    <>
    <Row>
    <Col className="px-md-1" md="4">
      <FormGroup>
        <label>Ad Link</label>
        <Input
          id="link"
          defaultValue={props.creative.native_link}
          onChange={ (e) => props.callback(e,"native_link")}
          type="text">
          </Input>
      </FormGroup>
    </Col>
    <Col className="px-md-1" md="4">
      <FormGroup>
        <label>Imp Tracker URLS</label>
        <Input
          id="imptrackers"
          defaultValue={props.creative.native_trk_urls}
          type="select">
            {mimeTypes(props.creative.native_trk_urls)}
          </Input>
      </FormGroup>
    </Col>
    <Col className="px-md-1" md="4">
    <FormGroup>
        <label>JS Tracker</label>
        <Input
          id="jstracker"
          onChange={ (e) => props.callback(e,"native_js_tracker")}
          defaultValue={props.creative.native_js_tracker}
          type="textarea"
        />
      </FormGroup>
    </Col>
    </Row>
    <Row>
      <Col className="px-md-1" md="4">
        <FormGroup>
          <label>Context</label>
          <Input
            id="native_context"
            onChange={(e) => props.multiHandler(e,"native_context")}
            type="select" multiple>
              {contextType(props.creative.native_context)}}
          </Input>
      </FormGroup>
      </Col>
      <Col className="px-md-1" md="4">
        <FormGroup>
          <label>Sub-Context</label>
          <Input
            id="native_contextsubtype"
            onChange={(e) => props.multiHandler(e,"native_context")}
            type="select" multiple>
              {contextSubType(props.creative.native_contextsubtype)}}
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
      creative={props.creative} 
      setter={setAndRedraw} 
      callback={handler} />
   </>
  );
};

export default NativeEditor;