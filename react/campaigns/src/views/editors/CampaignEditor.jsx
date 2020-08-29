import React, { useState, useEffect } from "react";

// reactstrap components
import {
  Button,
  Card,
  CardHeader,
  CardBody,
  CardFooter,
  Form,
  FormGroup,
  Input,
  Label,
  Row,
  Col
} from "reactstrap";
import { useViewContext } from "../../ViewContext";

import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import DayPartEditor from "./DayPartEditor";
import {getTrueFalseOptions, ssp, blackStyle, whiteStyle} from "../../Utils.js"

var regions = ["US","APAC","EUROPE","Russia"];

var undef;

const CampaignEditor = (props) => {

  const [count, setCount] = useState(0);
  const [campaign, setCampaign] = useState(props.campaign);
  const [startDate, setStartDate] = useState(new Date(props.campaign.activate_time));
  const [endDate, setEndDate] = useState(new Date(props.campaign.expire_time));
  const [daypartSchedule, setDaypartSchedule] = useState(props.campaign.daypartSchedule);
  const vx = useViewContext();

const getAttachedCreatives = () => {
  var items = []; 
  items.push();
  var mt = {
    banner:campaign.banners,
    video: campaign.videos,
    audio: campaign.audios,
    native:campaign.natives
  };
  for (var i=0;i<vx.creatives.length;i++) {
    var x = vx.creatives[i];
    var list = mt[x.type];
    if (list != undef && list.indexOf(x.id) != -1)
      items.push(<option key={"creatives-"+i} selected>{x.name}</option>);
    else
      items.push(<option key={"creatives-"+i}>{x.name}</option>);
  }
  return(items);
}

const getIdOf = (name) => {
  for (var i=0;i<vx.targets.length;i++) {
    var x = vx.targets[i];
    if (x.name === name)
      return x.id;
  }
  return 0;
}

const  addNewCampaign = async () => {
    var x = campaign;
    x.name = document.getElementById("name").value;
    x.ad_domain = document.getElementById("adomain").value;
    var forensiq = document.getElementById("fraudSelect").value;
    x.status = document.getElementById("statusSelect").value;
    x.budget.totalBudget = Number(document.getElementById("totalBudget").value);
    x.budget.dailyBudget = Number(document.getElementById("dailyBudget").value);
    x.budget.hourlyBudget= Number(document.getElementById("hourlyBudget").value);
    x.spendrate = Number(document.getElementById("spendRate").value);
    x.regions = document.getElementById("regions").value;

    var capspec = document.getElementById("capspec").value;
    var capexpire = document.getElementById("capexpire").value;
    var capcount = document.getElementById("capcount").value;
    x.capunit = document.getElementById("capunit").value;
    x.capspec = capspec === "" ? undef : capspec;
    x.capexpire = capexpire === "" ? undef : Number(capexpire);
    x.capcount = capcount === "" ? undef : Number(capcount);
    

    var tid = document.getElementById("target").value;
    tid = getIdOf(tid);
    x.target_id = tid === "" ? 0 : tid;
    if (x.target_id == 0) {
      alert("No viable target, assigned, offline");
      x.status = "offline";
    }

    x.exchanges = [...document.getElementById("exchanges").options]
                     .filter((x) => x.selected)
                     .map((x)=>x.value);

    x.rules = [...document.getElementById("rules").options]
                     .filter((x) => x.selected)
                     .map((x)=>Number(x.value));
    
    var cnames = [...document.getElementById("creatives").options]
                     .filter((x) => x.selected)
                     .map((x)=>x.value);
    x.banners = [];
    x.videos = [];
    x.audios = [];
    x.natives = [];
    for(var i=0;i<cnames.length;i++) {
      var name = cnames[i];
      var cr = vx.findCreativeByName(name);
      if (cr === undef) {
        alert("DB problem, creative: " + name + " is missing");
        return;
      }
      switch(cr.type) {
        case "banner":
          x.banners.push(cr.id);
          break;
        case "video":
          x.videos.push(cr.id);
          break;
        case "audio":
          x.audios.push(cr.id);
          break;
        case "native":
          x.natives.push(cr.id);
          break;
        default:
          alert("Type " + cr.type + " unknown for creative " + cr.name);
          return;
      }
    }
   
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

    x.activate_time = startDate.getTime();
    x.expire_time = endDate.getTime();
    if (!x.ad_domain) { alert("Ad Domain cannot be blank"); return; }

    if (daypartSchedule === undef || daypartSchedule === null ||  daypartSchedule.length === 0) {
      x.day_parting_utc = undef;
    } else {
      x.day_parting_utc = JSON.stringify(daypartSchedule);
      if (x.day_parting_utc.indexOf("1") < 0) {
          alert("Empty daypart, undef'ing it");
          x.day_parting_utc = undef;
      }
    }

    //alert(JSON.stringify(x,null,2));

    props.callback(x);

    setCampaign(null);
    setDaypartSchedule(null);

    return false;
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
  var items = []; 
  for (var i=0;i<vx.rules.length;i++) {
    var x = vx.rules[i];
    //alert("x = " + JSON.stringify(x,null,2) + ", rules = " + JSON.stringify(campaign.rules,null,2));
    if (campaign.rules.indexOf(x.id) != -1)
      items.push(<option key={"rules-"+x.id} selected value={x.id}>{x.name}</option>);
    else
      items.push(<option key={"rules-"+x.id} value={x.id}>{x.name}</option>);
  }
  return(items);
}

const getSelectedTargets = () => {
  var items = []; 
  items.push(<option key={"target-none"}></option>);
  for (var i=0;i<vx.targets.length;i++) {
    var x = vx.targets[i];
    console.log("Check: " + x.id + " vs " + campaign.target_id);
    if (campaign.target_id === x.id)
      items.push(<option key={"target-"+i} value={x.name} selected>{x.name}</option>);
    else
      items.push(<option key={"target-"+i} value={x.name}>{x.name}</option>);
  }
  return(items);
}

const getSelectedRegions = () => {
   var items = []; 
    for (var i=0;i<regions.length;i++) {
      var x = regions[i];
      if (campaign.regions === x)
        items.push(<option key={"regions-"+x} selected>{x}</option>);
      else
        items.push(<option key={"regions-"+x}>{x}</option>);
    }
    return(items);
}

  const redraw = () => {
      setCount(count+1);
  }

  const discard = () => {
    setCampaign(null);
    setDaypartSchedule(null);
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
                                  style={(document.body.classList.contains("white-content")) 
                                    ? blackStyle : whiteStyle}
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
                                  defaultValue={campaign.ad_domain}
                                  type="text" />
                              </FormGroup>
                            </Col>
                            <Col className="pl-md-1" md="2">
                              <FormGroup>
                              <Label >Fraud Suppression</Label>
                                <Input type="select" id="fraudSelect" defaultValue={campaign.forensiq}>
                                    {getTrueFalseOptions(campaign.forensiq)}
                                </Input>               
                              </FormGroup>
                            </Col>
                            <Col className="pl-md-1" md="2">
                              <FormGroup>
                              <Label>Bidder Status</Label>
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
                                    id="creatives"
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
                                <label>Bidding Region</label>
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
                                <Input type="select" id="rules" multiple>
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

                        <Row>
                        <Col className="pr-md-1" md="4">
                              <FormGroup>
                              <label>Frequency Specification</label>
                                <Input type="text" name="capspec" id="capspec" defaultValue={campaign.capSpec}/>
                              </FormGroup>
                            </Col>
                            <Col className="pl-md-1" md="1">
                              <FormGroup>
                                <label>Count</label>
                                <Input placeholder="0" type="number" id="capcount" defaultValue={campaign.capCount}/>
                              </FormGroup>
                            </Col>
                            <Col className="px-md-1" md="1">
                              <FormGroup>
                                <label>Expiration</label>
                                <Input placeholder="0" type="number" id="capexpire" defaultValue={campaign.capExpire}/>
                              </FormGroup>
                            </Col>
                            <Col className="px-md-1" md="2">
                              <FormGroup>
                                <label>Timebase</label>
                                <Input type="select" name="select" id="capunit">
                                  <option selected={campaign.capUnit === undef || campaign.capUnit === "seconds"}>seconds</option>
                                  <option selected={campaign.capUnit === "minutes"}>minutes</option>
                                  <option selected={campaign.capUnit === "hours"}>hours</option>
                                  <option selected={campaign.capUnit === "days"}>days</option>
                                </Input>
                              </FormGroup>
                            </Col>
                        </Row>

                        <DayPartEditor 
                            key={"day-part-"+count}
                            daypart={daypartSchedule}
                            redraw={redraw}
                            callback={setDaypartSchedule}/>

                        </Form>
                      </CardBody>
                      <CardFooter>
                        <Button className="btn-fill"
                          color="primary" 
                          onClick={() => addNewCampaign()} disabled={campaign.readOnly}>
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
