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

import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

var ssp = ["Adx","Nexage","Openx","Stroer"];
var regions = ["US","APAC","EUROPE","Russia"];

var undef;

const CampaignEditor = (props) => {

  const [count, setCount] = useState(0);
  const [campaign, setCampaign] = useState(props.campaign);
  const [startDate, setStartDate] = useState(new Date());
  const [endDate, setEndDate] = useState(new Date());
  const vx = useViewContext();

const getAttachedCreatives = () => {
    return(
      <>
      </>
    );
}

const  addNewCampaign = async () => {
    var x = campaign;
    x.name = document.getElementById("name").value;
    x.target = document.getElementById("target").value;
    x.adomain = document.getElementById("adomain").value;
    var forensiq = document.getElementById("fraudSelect").value;
    x.status = document.getElementById("statusSelect").value;
    x.budget.totalBudget = Number(document.getElementById("totalBudget").value);
    x.budget.dailyBudget = Number(document.getElementById("dailyBudget").value);
    x.budget.hourlyBudget= Number(document.getElementById("hourlyBudget").value);
    x.assignedSpendRate = Number(document.getElementById("spendRate").value);
    x.regions = document.getElementById("regions").value;

  
    x.exchanges = [...document.getElementById("exchanges").options]
                     .filter((x) => x.selected)
                     .map((x)=>x.value);
    
   
    if (x.name === "") { alert("Name cannot be blank"); return; }

    if (forensiq === "true") 
      x.forensiq = true;
    else 
      x.forensiq = false;

    if (startDate != null) {
      x.date = [];
      x.date.push(startDate.getTime());
      x.date.push(endDate.getTime());
    }
    if (!x.adomain) { alert("Ad Domain cannot be blank"); return; }

    props.callback(x);
    return false;
}

const getTrueFalseOptions = (value)  =>{
    if (value === true) {
        return(
            <>
            <option>true</option>
            <option>false</option>
            </>
        );
    }
    return(
        <>
        <option>true</option>
        <option>false</option> 
    </>);
}

const getSelectedExchangeOptions = () => {
    var items = []; 
    items.push();
    for (var i=0;i<ssp.length;i++) {
      var x = ssp[i];
      if (campaign.exchanges.indexOf(x) != -1)
        items.push(<option key={"exchanges-"+x} selected>{x}</option>);
      else
        items.push(<option key={"exchanges-"+x}>{x}</option>);
    }
    return(items);
}

const getStatusOptions = (status) => {
  if (status==='runnable') 
    return(
      <>
      <option>runnable</option>
      <option>offline</option>
      </>
  );
  return(
    <>
    <option>offline</option>
    <option>runnable</option>
    </>);
}

const getSelectedRules = () => {
    return(
        <>
        <option>Rule 1</option>
        <option>Rule 2</option>
        <option>Rule 3</option>
        <option>Rule 4</option>
        <option>Rule 5</option>
        <option>Rule 6</option>
        </>
    );
}

const getSelectedTargets = () => {
    return(
        <>
        <option>Target 1</option>
        <option>Target 2</option>
        <option>Target 3</option>
        <option>Target 4</option>
        <option>Target 5</option>
        </>
    );
}

const getSelectedRegions = () => {
   var items = []; 
    for (var i=0;i<regions.length;i++) {
      var x = regions[i];
      if (campaign.regions === x)
        items.push(<option key={"regions-"+x} selected>{x}</option>);
      else
        items.push(<option key={"exchanges-"+x}>{x}</option>);
    }
    return(items);
}

  const redraw = () => {
      setCount(count+1);
  }

  const update = () => {
      props.callback(true);
  }

  const discard = () => {
      props.callback(false);
  }

  const getLabel = () => {
      if (campaign.sqlid === -1)
        return (<div>Save</div>);
      return(<div>Update</div>);
  }

        return (
            <>
              <div className="content">
                <Row>
                  <Col>
                    <Card>
                      <CardHeader>
                        <h5 className="title">Edit Campaign Details</h5>
                      </CardHeader>
                      <CardBody>
                        <Form>
                          <Row>
                            <Col className="pr-md-1" md="2">
                              <FormGroup>
                                <label>SQL ID (disabled)</label>
                                <Input
                                  defaultValue={campaign.adId}
                                  disabled
                                  type="text"
                                />
                              </FormGroup>
                            </Col>
                            <Col className="px-md-1" md="2">
                              <FormGroup>
                                <label>Name</label>
                                <Input
                                  id="name"
                                  defaultValue={campaign.name}
                                  placeholder="Campaign Name/Ad Id (Required)"
                                  type="text"
                                />
                              </FormGroup>
                            </Col>
                            <Col className="pl-md-1" md="4">
                              <FormGroup>
                                <label htmlFor="addomain">
                                  Ad Domain
                                </label>
                                <Input 
                                  id="adomain"
                                  placeholder="Ad domain (required)"
                                  defaultValue={campaign.adomain}
                                  type="text" />
                              </FormGroup>
                            </Col>
                            <Col className="pl-md-1" md="2">
                              <FormGroup>
                              <Label >Fraud</Label>
                                <Input type="select" id="fraudSelect" defaultValue={campaign.forensiq}>
                                    {getTrueFalseOptions(campaign.forensiq)}
                                </Input>               
                              </FormGroup>
                            </Col>
                            <Col className="pl-md-1" md="2">
                              <FormGroup>
                              <Label>Status</Label>
                                <Input type="select"  id="statusSelect">
                                    {getStatusOptions(campaign.status)}
                                </Input>               
                              </FormGroup>
                            </Col>
                          </Row>
                          <Row>
                            <Col className="pr-md-1" md="12">
                              <FormGroup>
                                <label>Creatives</label>
                                <Input
                                    id="campaign"
                                    type="select" multiple>
                                    {getAttachedCreatives()}
                                </Input>
                              </FormGroup>
                            </Col>
                          </Row>
                          <Row>
                            <Col className="pr-md-1" md="4">
                              <FormGroup>
                                <label>Start</label>
                                <Col >
                                <DatePicker
                                    id="start"
                                    selected={startDate}
                                    onChange={date => setStartDate(date)}
                                    showTimeSelect
                                    timeFormat="HH:mm"
                                    timeIntervals={15}
                                    timeCaption="time"
                                    dateFormat="MMMM d, yyyy h:mm aa"
                                />
                               </Col>
                              </FormGroup>
                            </Col>
                            <Col className="pr-md-1" md="4">
                              <FormGroup>
                                <label>End</label>
                                <Col>
                                <DatePicker
                                    id="end"
                                    selected={endDate}
                                    onChange={date => setEndDate(date)}
                                    showTimeSelect
                                    timeFormat="HH:mm"
                                    timeIntervals={15}
                                    timeCaption="time"
                                    dateFormat="MMMM d, yyyy h:mm aa"
                                />
                                </Col>
                                </FormGroup>
                             </Col>
                             <Col className="px-md-1" md="3">
                              <FormGroup>
                                <label>Spend Rate/Minute</label>
                                <Input id="spendRate"
                                  defaultValue={campaign.assignedSpendRate}
                                  type="number"
                                />
                              </FormGroup>
                            </Col>
                          </Row>
                          <Row>
                            <Col className="pr-md-1" md="4">
                              <FormGroup>
                                <label>Region</label>
                                <Input type="select" id="regions">
                                    {getSelectedRegions()}
                                </Input>
                              </FormGroup>
                            </Col>
                            <Col className="px-md-1" md="4">
                              <FormGroup>
                                <label>Exchanges</label>
                                <Input type="select" name="select" id="exchanges" multiple>
                                    {getSelectedExchangeOptions()}
                                </Input>     
                              </FormGroup>
                            </Col>
                            <Col className="pl-md-1" md="4">
                              <FormGroup>
                                <label>Target</label>
                                <Input type="select" id="target">
                                    {getSelectedTargets()}
                                </Input>
                              </FormGroup>
                            </Col>
                          </Row>
                          <Row>
                            <Col className="pr-md-1" md="4">
                              <FormGroup>
                              <label>Rules</label>
                                <Input type="select" name="select" id="rules" multiple>
                                    {getSelectedRules()}
                                </Input>     
                              </FormGroup>
                            </Col>
                            <Col className="px-md-1" md="2">
                              <FormGroup>
                                <label>Total Budget</label>
                                <Input placeholder="0" type="number" id="totalBudget" defaultValue={campaign.budget.totalBudget}/>
                              </FormGroup>
                            </Col>
                            <Col className="pl-md-1" md="2">
                              <FormGroup>
                                <label>Daily Budget</label>
                                <Input placeholder="0" type="number" id="dailyBudget" defaultValue={campaign.budget.dailyBudget}/>
                              </FormGroup>
                            </Col>
                            <Col className="pl-md-1" md="2">
                              <FormGroup>
                                <label>Hourly Budget</label>
                                <Input placeholder="0" type="number" id="hourlyBudget" defaultValue={campaign.budget.hourlyBudget}/>
                              </FormGroup>
                            </Col>
                          </Row>
                        </Form>
                      </CardBody>
                      <CardFooter>
                        <Button className="btn-fill"
                          color="primary" 
                          onClick={() => addNewCampaign()}>
                          Save
                        </Button>
                        <Button className="btn-fill" color="danger"
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
 export default CampaignEditor;
