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
import { useViewContext } from "../../ViewContext";

import "react-datepicker/dist/react-datepicker.css";
import IAB from '../../IAB';
import CampaignEditor from "./CampaignEditor";


var undef;

const TargetEditor = (props) => {

  const [count, setCount] = useState(0);
  const [target, setTarget] = useState(props.target);
  const [startDate, setStartDate] = useState(new Date());
  const vx = useViewContext();

  const nameChangedHandler = (event) => {
      target.name = event.target.value;
      setTarget(target);
  }
  

const getTrueFalseOptions = (value)  =>{
    if (value)
        return(
            <>
            <option selected>true</option>
            <option>false</option>
            </>
        );
        return(
            <>
            <option>true</option>
            <option selected>false</option>
            </>
        );
}

const domainType = ()  =>{
  if (target.domaintargetting === 'BLACKLIST')
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

const getWBList = (s) => {
  var items = []; 
  for (var i=1;i<26;i++) {
    var iab = "IAB"+i;
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

    var lat = document.getElementById("lat").value;
    var lon = document.getElementById("lon").value;
    var range = document.getElementById("range").value;
    if (lat === "" || lat === "0")
      target.geo_latitude = null;
    else
      target.geo_latitude = Number(lat);
    if (lon === "" || lon === "0")
      target.geo_longitude = null;
    else
      target.geo_longitude = Number(lon);
    if (range === "" || range === "0")
      target.geo_range = null;
    else
      target.geo_range = Number(range);
     
    var car = document.getElementById("carrier").value;
    var os = document.getElementById("os").value;

    var make = document.getElementById("makes").value;
    var model = document.getElementById("models").value;
    var devicetypes = document.getElementById("device-types").value;
    if (make === "")
      target.make = null;
    else
      target.make = make.split("\n").join(",");
    if (model === "")
      target.model = null;
    else
      target.model = model.split("\n").join(",");
    if (devicetypes === "")
      target.devicetypes = null;
    else
      target.devicetypes = devicetypes.split("\n").join(",");
 
    target.IAB_category = ([...document.getElementById("whitelist").options]
        .filter((x) => x.selected)
        .map((x)=>x.value)).join();
    target.IAB_category_blklst = ([...document.getElementById("blacklist").options]
        .filter((x) => x.selected)
        .map((x)=>x.value)).join(); 
    
    

    alert(JSON.stringify(target,null,2));

    props.callback(true);
  }

  const discard = () => {
      props.callback(false);
  }

  const getLabel = () => {
      if (target.id === 0)
        return (<div>Save</div>);
      return(<div>Update</div>);
  }

  const asTextAreaList = (list) => {
    var str = "";
    if (!list)
      return str;
    return list.join();
  }

  const fromCommaList = (str) => {
    if (!str)
      return "";
    return str.split(",").join("\n");
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
                                <Input type="textarea" id="domain" defaultValue={asTextAreaList(target.listofdomains)}/>
   
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
                            <Col className="pr-md-1" md="4">
                              <FormGroup>
                              <label>Geo Latitude</label>
                                <Input type="input" id="lat" defaultValue={target.lat}/>   
                              </FormGroup>
                            </Col>
                            <Col className="px-md-1" md="2">
                              <FormGroup>
                              <label>Geo Longitude</label>
                                <Input type="input"  id="lon" defaultValue={target.lon}/>   
                              </FormGroup>
                              </Col>
                              <Col className="px-md-1" md="2">
                              <FormGroup>
                              <label>Geo Range</label>
                                <Input type="input" id="range" defaultValue={target.range}/>   
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
                                <Input type="textarea" id="makes" defaultValue={fromCommaList(target.make)}/>
                              </FormGroup>
                            </Col>
                            <Col className="pr-md-1" md="2">
                            <FormGroup>
                                <label>models</label>
                                <Input type="textarea" id="models" defaultValue={fromCommaList(target.model)}/>
                            </FormGroup>
                            </Col>     
                            <Col className="pr-md-1" md="2">
                                <FormGroup>
                                    <label>Device Types</label>
                                    <Input type="textarea" id="device-types" defaultValue={fromCommaList(target.devicetypes)}/>   
                                </FormGroup>
                            </Col>                           
                          </Row>
                          <Row>
                            <Col>
                                <FormGroup>
                                    <label>IAB Whitelist</label>
                                    <Input type="select" id="whitelist" multiple>
                                        {getWBList(target.IAB_category)}
                                    </Input>     
                                </FormGroup>    
                            </Col>
                            <Col>
                                <FormGroup>
                                    <label>IAB Blacklist</label>
                                    <Input type="select" id="blacklist" multiple>
                                        {getWBList(target.IAB_category_blklst)}
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