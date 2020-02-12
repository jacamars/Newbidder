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
import BannerEditor from "./BannerEditor";
import VideoEditor from "./VideoEditor";
import AudioEditor from "./AudioEditor";
import DealEditor from "./DealEditor";
import CreativeSizeEditor from "./CreativeSizeEditor";
import { useViewContext } from "../../ViewContext";

import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";


var undef;

const CreativeEditor = (props) => {

  const [count, setCount] = useState(0);
  const [creative, setCreative] = useState(props.creative);
  const [startDate, setStartDate] = useState(new Date());
  const vx = useViewContext();

const getAttachedCampaign = () => {
    return(
        <>
        <option>None</option>
        </>
    );
}

const getSelectedRules = () => {
    var items = []; 
    for (var i=0;i<vx.rules.length;i++) {
      var x = vx.rules[i];
      //alert("x = " + JSON.stringify(x,null,2) + ", rules = " + JSON.stringify(campaign.rules,null,2));
      if (creative.rules.indexOf(x.id) != -1)
        items.push(<option key={"rules-"+x.id} selected value={x.id}>{x.name}</option>);
      else
        items.push(<option key={"rules-"+x.id} value={x.id}>{x.name}</option>);
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
      if (creative.id === 0)
        return (<div>Save</div>);
      return(<div>Update</div>);
  }

  const addNewCreative = () => {
    var x = creative;
    if (x.isBanner)
      x.react_type = "BANNER";
    else
    if (x.isVideo)
      x.react_type = "VIDEO";
    else
    if (x.isAudio)
      x.react_type = "AUDIO";
    else
    if (x.isNative)
      x.react_type = "NATIVE";
    else {
      alert("Type can't be set for " + JSON.stringify(x,null,2))
      return;
    }
    if (x.id === undef) {
      alert("NO ID field in creative!");
      return;
    }

    x.name = document.getElementById("name").value;
    x.bid_ecpm = Number(document.getElementById("price").value);
    x.cur = document.getElementById("currency").value;
    x.total_budget = Number(document.getElementById("total_budget").value);
    x.hourly_budget = Number(document.getElementById("hourly_budget").value);
    x.daily_budget = Number(document.getElementById("daily_budget").value);
    x.interval_start = 0;
    x.interval_end = 0;

    x.rules = [...document.getElementById("rules").options]
      .filter((x) => x.selected)
      .map((x)=>Number(x.value));

    switch(x.sizeType) {
      case 1: // width and height are 0
        x.width = 0;
        x.height = 0;
        break;
      case 2:
        x.width = Number(x.width);
        x.height = Number(x.height);
        x.width_height_list = undef;
        x.width_range = undef;
        x.height_range = undef;
        break;
      case 3:
        x.width = 0;
        x.height = 0;
        x.width_height_list = undef;
        break;
      case 4:
        x.width = 0;
        x.height = 0;
        x.width_range = undef;
        x.height_range = undef;
        break;
      default:
        alert("Don't know what size type this creative is");
        return;
    }

    if (x.isVideo) {
      x.vast_video_width = x.width;
      x.vast_video_height = x.height;
      x.width = null;
      x.height = null;
      x.vast_video_linearity = Number(x.vast_video_linearity);
      x.vast_video_duration = Number(x.vast_video_duration);
      x.vast_video_bitrate = Number(x.vast_video_bitrate);
      x.vast_video_protocol = Number(x.vast_video_protocol);
      x.vast_video_inearity = Number(x.vast_video_linearity);
    }

    alert(JSON.stringify(x,null,2));
    props.callback(x);
  }

  // Callback for w/h in CreativeSizeEditor
  const setSize = (e, key) => {
    if (e == null) {
      creative[key] = "0";
      return;
    }
    creative[key] = e.target.value;
    setCreative(creative);
  }

  // Set the dimension type
  const setSizeType = (t) => {
    creative.sizeType = t;
    setCreative(creative);
  }

  // Set the deal
  const setDealType = (t) => {
    creative.dealType = t;
  }

  const setHtml = (e,type) => {
    creative[type]=e.target.value;
    setCreative(creative);
    setCount(count+1);
  }

        return (
            <>
              <div className="content">
                <Row>
                  <Col>
                    <Card>
                      <CardHeader>
                        <h5 className="title">Edit Creative Details ({creative.name})</h5>
                      </CardHeader>
                      <CardBody>
                        <Form>
                          <Row>
                            <Col className="pr-md-1" md="2">
                              <FormGroup>
                                <label>SQL ID (disabled)</label>
                                <Input
                                  defaultValue={creative.id}
                                  disabled
                                  type="text"
                                />
                              </FormGroup>
                            </Col>
                            <Col className="px-md-1" md="3">
                              <FormGroup>
                                <label>Name</label>
                                <Input
                                  id="name"
                                  defaultValue={creative.name}
                                  placeholder="Creative Name (Required)"
                                  type="text"
                                />
                              </FormGroup>
                            </Col>
                            <Col className="pr-md-1" md="2">
                              <FormGroup>
                                <label>ECPM/Price</label>
                                <Input
                                  id="price"
                                  defaultValue={creative.price}
                                  placeholder="Creative Price (Required)"
                                  type="text"
                                />
                              </FormGroup>
                            </Col>
                            <Col className="px-md-1" md="2">
                              <FormGroup>
                                <label>Currency</label>
                                <Input
                                  id="currency"
                                  defaultValue={creative.cur}
                                  placeholder="Creative Currency (Required)"
                                  type="text"
                                />
                              </FormGroup>
                            </Col>
                          </Row>

                          {!creative.isAudio && 
                            <CreativeSizeEditor 
                              creative={creative} 
                              callback={setSize} 
                              selector={setSizeType}/>}

                          <DealEditor creative={creative} selector={setDealType}/>
                          { creative.isBanner &&
                            <BannerEditor key={"banner-creative-"-count} 
                              creative={creative} 
                              callback={setHtml}/>}

                          { creative.isVideo && 
                            <VideoEditor key={'video-creative'-count} 
                              creative={creative} 
                              callback={setHtml}/>}

                          { creative.isAudio && 
                            <AudioEditor key={'audio-creative'-count} 
                              creative={creative} 
                              callback={setHtml}/>}

                          <Row>
                            <Col className="pr-md-1" md="12">
                              <FormGroup>
                                <label>Campaign</label>
                                <Input
                                    id="campaign"
                                    type="select">
                                    {getAttachedCampaign()}>
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
                                <Input placeholder="0" type="number" id="total_budget" defaultValue={creative.budget.totalBudget}/>
                              </FormGroup>
                            </Col>
                            <Col className="pl-md-1" md="2">
                              <FormGroup>
                                <label>Daily Budget</label>
                                <Input placeholder="0" type="number" id="daily_budget" defaultValue={creative.budget.dailyBudget}/>
                              </FormGroup>
                            </Col>
                            <Col className="pl-md-1" md="2">
                              <FormGroup>
                                <label>Hourly Budget</label>
                                <Input placeholder="0" type="number" id="hourly_budget" defaultValue={creative.budget.hourlyBudget}/>
                              </FormGroup>
                            </Col>
                          </Row>
                        </Form>
                      </CardBody>
                      <CardFooter>
                        <Button className="btn-fill"
                          color="primary" 
                          type="submit"
                          onClick={() => addNewCreative()} disabled={creative.readOnly}>
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
 export default CreativeEditor;