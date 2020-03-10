// https://www.iab.com/wp-content/uploads/2016/03/OpenRTB-Native-Ads-Specification-1-1_2016.pd

import React, { useState, useEffect } from "react";

// reactstrap components
import {
  Alert,
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
import {mimeTypes, protocolOptions, assetDataType} from "../../Utils";
import VideoEditor from "./VideoEditor";

var undef;

const ViewAssets = (props) => {

const [video, setVideo] = useState({});

  const handler = (e, i, key ) => {
    props.nativead.assets[i][key] = e.target.value;
  }

  const deleteAsset = (i) => {
      console.log("Delete asset: " + i);
      props.nativead.assets.splice(i,1);
      props.setter(props.assets);
  }

  const setVideoAsset = () => {

  }

  return(
    props.nativead.assets.map((row,i) => (
        <>
            {row.type === 'title' && <>
            <Alert color="primary">
               <Label>TITLE ASSET</Label>
               <Button color="warning" size="sm" onClick={()=>deleteAsset(i)}>Remove</Button>
               <Row>
                <Col className="px-md-1" md="4">
                    <FormGroup>
                        <label>Title Text</label>
                        <Input
                            id="title"
                            onChange={ (e) => handler(e,i,"text")}
                            defaultValue={row.title}
                            type="text">
                        </Input>
                    </FormGroup>
                </Col>
              </Row>
              </Alert>
            </>}
            {row.type === 'image' && <>
            <Alert color="success">
               <Label>IMAGE ASSET</Label>
               <Button color="warning" size="sm" onClick={()=>deleteAsset(i)}>Remove</Button>
              <Row>
              <Col className="px-md-1" md="4">
                  <FormGroup>
                      <label>URL </label>
                      <Input
                          id="title"
                          onChange={ (e) => handler(e,i,"url")}
                          defaultValue={row.url}
                          type="text">
                      </Input>
                  </FormGroup>
              </Col>
              <Col className="px-md-1" md="1">
                  <FormGroup>
                      <label>Width</label>
                      <Input
                          id="w"
                          onChange={ (e) => handler(e,i,"w")}
                          defaultValue={row.w}
                          type="number">
                      </Input>
                  </FormGroup>
              </Col>
              <Col className="px-md-1" md="1">
                  <FormGroup>
                      <label>Height</label>
                      <Input
                          id="h"
                          onChange={ (e) => handler(e,i,"h")}
                          defaultValue={row.w}
                          type="number">
                      </Input>
                  </FormGroup>
              </Col>
            </Row>
            </Alert>
            </> }
            {row.type === 'video' && <>
            <Alert color="warning">
               <Label>VIDEO ASSET</Label>
               <Button color="warning" size="sm" onClick={()=>deleteAsset(i)}>Remove</Button>
               <Row>
                <Col className="px-md-1" md="4">
                    <FormGroup>
                        <label>Landing URL</label>
                        <Input
                            id="title"
                            onChange={ (e) => handler(e,i,"url")}
                            defaultValue={row.url}
                            type="text">
                        </Input>
                    </FormGroup>
                </Col>
                <Col className="px-md-1" md="4">
                    <FormGroup>
                        <label>Clicktracker</label>
                        <Input
                            id="title"
                            onChange={ (e) => handler(e,i,"url")}
                            defaultValue={row.clicktracker}
                            type="text">
                        </Input>
                    </FormGroup>
                </Col>
              </Row>
              <VideoEditor key={'video-asset'} 
                              creative={video} 
                              callback={setVideoAsset}/>
              </Alert>
            </>}
            {row.type === 'data' && <>
            <Alert color="info">
               <Label>DATA ASSET</Label>
               <Button color="dark" size="sm" onClick={()=>deleteAsset(i)}>Remove</Button>
              <Row>
              <Col className="px-md-1" md="4">
                  <FormGroup>
                      <label>Type</label>
                      <Input
                          id="datatype"
                          onChange={ (e) => handler(e,i,"datatype")}
                          type="select">
                            {assetDataType(row.datatype)}
                      </Input>
                  </FormGroup>
              </Col>
              <Col className="px-md-1" md="4">
                  <FormGroup>
                      <label>Label</label>
                      <Input
                          id="label"
                          onChange={ (e) => handler(e,i,"label")}
                          defaultValue={row.label}
                          type="text">
                      </Input>
                  </FormGroup>
              </Col>
              <Col className="px-md-1" md="4">
                  <FormGroup>
                      <label>Value</label>
                      <Input
                          id="w"
                          onChange={ (e) => handler(e,i,"value")}
                          defaultValue={row.w}
                          type="text">
                      </Input>
                  </FormGroup>
              </Col>
            </Row>
            </Alert>
            </>}
            {row.type === 'link' && <>
            <Alert color="warning">
               <Label>LINK ASSET</Label>
               <Button color="danger" size="sm" onClick={()=>deleteAsset(i)}>Remove</Button>
              <Row>
              <Col className="px-md-1" md="4">
                  <FormGroup>
                      <label>URL</label>
                      <Input
                          id="title"
                          onChange={ (e) => handler(e,i,"url")}
                          defaultValue={row.url}
                          type="text">
                      </Input>
                  </FormGroup>
              </Col>
              <Col className="px-md-1" md="4">
                  <FormGroup>
                      <label>Clicktrackers</label>
                      <Input
                          id="title"
                          onChange={ (e) => handler(e,i,"clicktrackers")}
                          defaultValue={row.urclicktrackers}
                          type="text">
                      </Input>
                  </FormGroup>
              </Col>
              <Col className="px-md-1" md="4">
                  <FormGroup>
                      <label>Fallback</label>
                      <Input
                          id="w"
                          onChange={ (e) => handler(e,i,"fallback")}
                          defaultValue={row.fallback}
                          type="text">
                      </Input>
                  </FormGroup>
              </Col>
            </Row>
            </Alert>
            </>}
        </>
    ))
  );
};

export default ViewAssets;