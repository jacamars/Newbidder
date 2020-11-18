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
import { useViewContext } from "../../ViewContext";

import "react-datepicker/dist/react-datepicker.css";
import GeoEditor from "./GeoEditor.jsx";
import {deviceTypes, fromCommaList, blackStyle, whiteStyle} from "../../Utils.js"
import LeafMap from "../LeafMap.jsx"

var undef;

const TargetEditor = (props) => {

  const [count, setCount] = useState(0);
  const [target, setTarget] = useState(props.target);
  const vx = useViewContext();

  const nameChangedHandler = (event) => {
      target.name = event.target.value;
      setTarget(target);
  }
  
const domainType = ()  =>{
  if (target.domain_targetting === 'BLACKLIST')
      return(
          <>
          <option selected>BLACKLIST</option>
          <option>WHITELIST</option>
          </>
      );
      return(
          <>
          <option>BLACKLIST</option>
          <option selected>WHITELIST</option>
          </>
      );
}

const getSelectedCountries = () => {
    return(
        <>
        </>
    )
}


const getDeviceTypes = (s) => {
  var checks = [];
  if (s != undef && s.length > 0) {
    checks = s.split(",");
  }
  var items = []; 
  for (var i=1;i<deviceTypes.length;i++) {
    var dt = deviceTypes[i];
    if (checks.indexOf(dt)>-1)
      items.push(<option key={"dt-"+i} selected>{dt}</option>);
    else
      items.push(<option dt={"iab-"+i}>{dt}</option>)
  }
  return(items);
}

const getWBList = (s) => {
  var checks = [];
  if (s.length > 0) {
    checks = s.split(",");
  }
  var items = []; 
  for (var i=1;i<26;i++) {
    var iab = "IAB"+i;
    if (checks.indexOf(iab)>-1)
      items.push(<option key={"iab-"+i} selected>{iab}</option>);
    else
      items.push(<option key={"iab-"+i}>{iab}</option>)
  }
  return(items);
}

  const redraw = () => {
      setCount(count+1);
  }

  const update = () => {
    var dv =  document.getElementById("domain").value;
    var bd = document.getElementById("set").value
    var type =  document.getElementById("domaintype").value
    target.name = document.getElementById("name").value;
    if (dv === "")
      target.list_of_domains = null;
    else
      target.list_of_domains = dv.split("\n").join(",");
    if (bd === "")
      target.listofdomainsSYMBOL = null;
    else
      target.listofdomainsSYMBOL = "$" + bd;
    if (dv != "" || bd != "") {
      target.domain_targetting = type;
    } else
      target.domain_targetting = null;

    var country = document.getElementById("country").value;

    if (country === "" || country === "null")
      target.country = undef;
    else
      target.country = country;

    var car = document.getElementById("carrier").value;
    var os = document.getElementById("os").value;
    var make = document.getElementById("makes").value;
    var model = document.getElementById("models").value;
    target.devicetype = ([...document.getElementById("device-types").options]
      .filter((x) => x.selected)
      .map((x)=>x.value)).join();
  
    if (car === "" || car === "null")
      target.carrier = undef;
    else
      target.carrier = car;
    if (os === "" || os === "null")
      target.os = undef;
    else
      target.os = os;     
    if (make === "" || make === "null")
      target.make = undef;
    else
      target.make = make.split("\n").join(",");
    if (model === "" || make === "null")
      target.model = undef;
    else
      target.model = model.split("\n").join(",");
 
    target.iab_category = ([...document.getElementById("whitelist").options]
        .filter((x) => x.selected)
        .map((x)=>x.value)).join();
    target.iab_category_blklist = ([...document.getElementById("blacklist").options]
        .filter((x) => x.selected)
        .map((x)=>x.value)).join(); 

    props.callback(target);
  }

  const discard = () => {
      props.callback(false);
  }

  const getLabel = () => {
      if (target.id === 0)
        return (<div>Save</div>);
      return(<div>Update</div>);
  }

  const setGeo = (geo) => {
    target.geo = geo;
    setTarget(target);
    redraw();
  }
        return (
            <>
              <div className="content">
                <Row>
                  <Col>
                    <Card>
                      <CardHeader>
                        <h5 className="title">Edit Targeting Details</h5>
                      </CardHeader>
                      <CardBody>
                        <Form>
                          <Row>
                            <Col className="pr-md-1" md="2">
                              <FormGroup>
                                <label>SQL ID (disabled)</label>
                                <Input
                                  style={(document.body.classList.contains("white-content")) 
                                    ?  blackStyle : whiteStyle}
                                  defaultValue={target.id}
                                  disabled
                                  type="text"
                                />
                              </FormGroup>
                            </Col>
                            <Col className="px-md-1" md="3">
                              <FormGroup>
                                <label>Ad Id</label>
                                <Input
                                  id="name"
                                  defaultValue={target.name}
                                  placeholder="Target Name (Required)"
                                  type="text"
                                />
                              </FormGroup>
                            </Col>
                          </Row>
                          <Row>
                            <Col className="px-md-1" md="4">
                              <FormGroup>
                                <label>Domain Values</label>
                                <Input type="textarea" id="domain" defaultValue={fromCommaList(target.list_of_domains)}/>   
                              </FormGroup>
                            </Col>
                            <Col className="pl-md-1" md="4">
                              <FormGroup>
                                <label>Use Big Data Set for Domain Values</label>
                                <Input type="input" id="set" defaultValue={target.listofdomainsSYMBOL}/>
                              </FormGroup>
                            </Col>
                            <Col className="pl-md-1" md="2">
                              <FormGroup>
                                <label>Type</label>
                                <Input type="select" id="domaintype">
                                  {domainType()}
                                  </Input>
                               </FormGroup>
                            </Col>
                          </Row>
                          <Row>   
                          <Col className="pr-md-1" md="2">
                                <FormGroup>
                                    <label>Country</label>
                                    <Input type="textarea" 
                                      id="country" 
                                      spellCheck={false}
                                      placeHolder="ISO-3 Countries."
                                      defaultValue={fromCommaList(target.country)}/>
                                </FormGroup>
                            </Col> 
                            <Col className="pr-md-1" md="8">
                                <FormGroup>
                                  <label>Geographical Boundaries</label>
                                  <Alert color="warning">
                                    <GeoEditor key={"geoeditor-"+count} geo={target.geo} setGeo={setGeo} />
                                  </Alert>
                                </FormGroup>
                            </Col>
                          </Row>
                          <Row>
                          <Col className="pr-md-1" md="2">
                            <FormGroup>
                              <label>Carrier</label>
                                <Input type="input" id="carrier" defaultValue={target.carrier}/>   
                              </FormGroup>
                            </Col>
                           <Col className="pr-md-1" md="2">
                            <FormGroup>
                              <label>Operating System</label>
                                <Input type="input" id="os" defaultValue={target.os}/>   
                              </FormGroup>
                            </Col>
                            <Col className="px-md-1" md="2">
                             <FormGroup>
                              <label>Make</label>
                                <Input type="textarea" id="makes" 
                                  spellCheck={false}
                                  defaultValue={fromCommaList(target.make)}/>
                              </FormGroup>
                            </Col>
                            <Col className="pr-md-1" md="2">
                            <FormGroup>
                                <label>models</label>
                                <Input type="textarea" 
                                  id="models" 
                                  spellCheck={false}
                                  placeHolder="Models, one per line"
                                  defaultValue={fromCommaList(target.model)}/>
                            </FormGroup>
                            </Col>     
                            <Col className="pr-md-1" md="2">
                                <FormGroup>
                                    <label>Device Types</label>
                                    <Input type="select" id="device-types" multiple>
                                      {getDeviceTypes(target.devicetypes_str)}
                                    </Input>
                                </FormGroup>
                            </Col>                           
                          </Row>
                          <Row>
                            <Col>
                                <FormGroup>
                                    <label>IAB Whitelist</label>
                                    <Input type="select" id="whitelist" multiple>
                                        {getWBList(target.iab_category)}
                                    </Input>     
                                </FormGroup>    
                            </Col>
                            <Col>
                                <FormGroup>
                                    <label>IAB Blacklist</label>
                                    <Input type="select" id="blacklist" multiple>
                                        {getWBList(target.iab_category_blklist)}
                                    </Input>     
                                </FormGroup>    
                            </Col>
                          </Row>
                        </Form>
                      </CardBody>
                      <CardFooter>
                        <Button className="btn-fill" color="primary" 
                            type="submit" onClick={() => update()}>
                          Save
                        </Button>
                        <Button className="btn-fill" color="danger" type="submit" 
                            onClick={() => props.callback(null)}>
                          Discard
                        </Button>
                      </CardFooter>
                    </Card>
                  </Col>
                </Row>
              </div>
            </>
          );
 }
 export default TargetEditor;