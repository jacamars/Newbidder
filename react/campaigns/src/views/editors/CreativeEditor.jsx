import React, { useState, useEffect } from "react";

// reactstrap components
import {
  Badge,
  Button,
  ButtonGroup,
  Card,
  CardHeader,
  CardBody,
  CardFooter,
  Form,
  FormGroup,
  Input,
  Row,
  Col
} from "reactstrap";
import BannerEditor from "./BannerEditor";
import VideoEditor from "./VideoEditor";
import AudioEditor from "./AudioEditor";
import DealEditor from "./DealEditor";
import NativeEditor from "./NativeEditor";
import SiteOrAppEditor from "./SiteOrAppEditor";
import CreativeSizeEditor from "./CreativeSizeEditor";
import { useViewContext } from "../../ViewContext";
import {attrOptions} from "../../Utils"

import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";


var undef;

const CreativeEditor = (props) => {

const [count, setCount] = useState(0);
const [creative, setCreative] = useState(props.creative);
const [startDate, setStartDate] = useState(new Date(props.creative.interval_start));
const [endDate, setEndDate] = useState(new Date(props.creative.interval_end));
const [privateDeals, setPrivateDeals] = useState(props.creative.dealType == 2);
const [appnexusSSP, setAppnexus] = useState(false);
const [bidswitchSSP, setBidswitch] = useState(false);
const [googleSSP, setGoogle] = useState(false);
const [stroerSSP, setStroer] = useState(false);
const [siteorapp, setSiteorapp] = useState(props.creative.siteorapp);

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

    // Form up the creative, except deals, the callback update in Creatives.jsx will rewrite the deals.
    var x = creative;
    if (x.isBanner)
      x.type = "banner";
    else
    if (x.isVideo)
      x.type = "video";
    else
    if (x.isAudio)
      x.type = "audio";
    else
    if (x.isNative)
      x.type = "native";
    else {
      alert("Type can't be set for " + JSON.stringify(x,null,2))
      return;
    }
    if (x.id === undef) {
      alert("NO ID field in creative!");
      return;
    }

    x.name = document.getElementById("name").value;
    x.price = Number(document.getElementById("price").value);
    x.cur = document.getElementById("currency").value;
    x.total_budget = Number(document.getElementById("total_budget").value);
    x.hourly_budget = Number(document.getElementById("hourly_budget").value);
    x.daily_budget = Number(document.getElementById("daily_budget").value);

    x.interval_start = startDate.getTime();
    x.interval_end = endDate.getTime();

    x.rules = [...document.getElementById("rules").options]
      .filter((x) => x.selected)
      .map((x)=>Number(x.value));

    x.attr = [...document.getElementById("attributes").options]
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
        if (!(x.isAudio || x.isNative)) {
          alert("Don't know what size type this creative is");
          return;
        }
    }

    if (x.isBanner) {
      if (!x.imageurl) {
        alert("You must provide an image url");
        return;
      }
    }

    x.forwardurl = undef;       // will be passed in possibly, but will cause errors

    if (x.isVideo) {
      var linearity = document.getElementById("vast_video_linearity").value;
      var duration = document.getElementById("vast_video_duration").value;
      var bitrate = document.getElementById("vast_video_bitrate").value;
      var protocol = document.getElementById("vast_video_protocol").value;

      if (linearity === "") {
        alert("Please fill in video linearity");
        return;
      }
      if (duration === "") {
        alert("Please fill in video duration");
        return;
      }
      if (bitrate === "") {
        alert("Please fill in bitrate");
        return;
      }
      if (protocol === "") {
        alert("Please fill in protocol");
        return;
      }

      x.htmltemplate = document.getElementById("outgoingfile").value;
      x.mime_type = document.getElementById("mime_type").value;
      x.vast_video_width = x.width;
      x.vast_video_height = x.height;
      x.width = undef;
      x.height = undef;
      x.vast_video_linearity = Number(linearity);
      x.vast_video_duration = Number(duration);
      x.vast_video_bitrate = Number(bitrate);
      x.vast_video_protocol = Number(protocol);

      alert(x.htmltemplate);

    }

    if (x.isAudio) {
      x.vast_video_width = undef;
      x.vast_video_height = undef;
      x.width = undef;
      x.height = undef;
      x.vast_video_linearity = undef;
      x.vast_video_duration = undef;
      x.vast_video_bitrate = undef;
      x.vast_video_protocol = undef;
      x.vast_video_inearity = undef;
      x.audio_duration = Number(x.audio_duration);
      x.audio_bitrate = Number(x.audio_bitrate);
    }

    if (privateDeals) {
      if (x.deals === undef || x.deals.length === 0) {
        alert("Sorry, you can't have private deals with no deals specified");
        return;
      }
    }

    if (x.price === 0 && (x.deals === undef || x.deals.length == 0)) {
      alert("You can't have a 0 price AND no deals defined");
      return;
    }

    if (x.deals !== undef) {
      for (var i=0;i<x.deals.length;i++) {
        var d = x.deals[i];
        if (d.id === "") {
          alert("Deal #" + i + " has no id, not allowed");
          return;
        }
      }
    }

    // handle extensions
    var ext = [];

    var clickthrough_url = getValue(document.getElementById("clickthrough_url"));

    var appnexus_crid = getValue(document.getElementById("appnexus_crid"));
    var agency_name = getValue(document.getElementById("agency_name"));
    var advertiser_name = getValue(document.getElementById("advertiser_name"));
    var billing_id = getValue(document.getElementById("billing_id"));
    var avr = getValue(document.getElementById("avr"));
    var avn = getValue(document.getElementById("avn"));
    var cnames = [...document.getElementById("categories").options]
        .filter((x) => x.selected)
        .map((x)=>x.value);

    if (appnexus_crid !== "") ext.push("appnexus_crid:#:"+appnexus_crid);
    if (agency_name !== "") ext.push("agency_name"+":#:"+agency_name);
    if (advertiser_name !== "") ext.push("advertiser_name"+":#:"+advertiser_name);
    if (billing_id !== "") ext.push("billing_id"+":#:"+billing_id);
    if (avr !== "") ext.push("avr"+":#:"+avr);
    if (avn !== "") ext.push("avn"+":#:"+avn);
    if (cnames.length !== 0) {
      ext.push("categories"+":#:"+cnames.join());
    }
  

    if (siteorapp !== "")
      ext.push("site_or_app:#:"+siteorapp);
    if (clickthrough_url !== "")
      ext.push("clickthrough_url:#:"+clickthrough_url);
    if (ext.length !== 0) 
      x.ext_spec = ext;
    else
      x.ext_spec = undef;



    // alert(JSON.stringify(x,null,2));
    props.callback(x);
  }

  const getValue = (x) => {
    if (x === undef || x === null)
      return "";
    return x.value;
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
    if (t === 2) {
      setPrivateDeals(true);
      creative.price = 0;
      document.getElementById("price").value = 0.0;
    } else {
      creative.deals = undef;
      setPrivateDeals(false);
    }
    setCreative(creative);
    setCount(count+1);
  }

  const setDealsArray = (deals) => {
    creative.deals = deals;
    setCreative(creative);
    setCount(count+1);
  }

  const changeDeal = (index) => {
    var price = document.getElementById("deal-price-"+index).value;
    var id = document.getElementById("deal-id-"+index).value;
    creative.deals[index].price = Number(price);
    creative.deals[index].id = id;
    setCreative(creative);
  }

  const changeSiteOrApp = (value) => {
    setSiteorapp(value);
  }

  const setHtml = (e,type) => {
    creative[type]=e.target.value;
    setCreative(creative);
    setCount(count+1);
  }

  const setNative = (e,type) => {
    creative.nativead[type]=e.target.value;
    setCreative(creative);
    setCount(count+1);
  }

  // Set multi selection keys in the creative (like from NativeEditor)
  const setMulti = (e,key) => {
    var s = [...document.getElementById(key).options]
      .filter((x) => x.selected)
      .map((x)=>Number(x.value));
    creative[key] = s;
    setCreative(creative);
  }

  const getCategoryList = () => {
    var checks = [];
    if (creative.categories !== undef)
      checks = creative.categories;

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

  const getAttrList = () => {
    var checks = [];
    if (creative.attr !== undef)
      checks = creative.attr;
    return attrOptions(checks);
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
                                  disabled={privateDeals}
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

                          <SiteOrAppEditor creative={creative} value={siteorapp} change={changeSiteOrApp} />
                          {! (creative.isAudio || creative.isNative) && 
                            <CreativeSizeEditor 
                              creative={creative} 
                              callback={setSize} 
                              selector={setSizeType}/>}

                          <DealEditor creative={creative} changeDeal={changeDeal} selector={setDealType} setdeals={setDealsArray}/>

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

                          { creative.isNative && 
                            <NativeEditor key={'native-creative'-count} 
                              creative={creative} 
                              mult={setMulti}
                              callback={setNative}/>}

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
                                    <label>Creative's Categories</label>
                                    <Input type="select" id="categories" multiple>
                                        {getCategoryList()}
                                    </Input>     
                                </FormGroup>    
                            </Col>

                            <Col className="pr-md-1" md="4">
                                <FormGroup>
                                    <label>Creative's Attributes</label>
                                    <Input type="select" id="attributes" multiple>
                                        {getAttrList()}
                                    </Input>     
                                </FormGroup>    
                            </Col>
                          </Row>

                          <Row>
                            <Col className="pr-md-1" md="4">
                            <h4>Specialty Exchange Attributes</h4>
                            </Col>
                          </Row>

                          <Row>
                            <Col className="pr-md-1" md="1"></Col>
                            <Col className="pr-md-1" md="2">
                              <ButtonGroup>
                                <Button size="sm" className="btn-fill" color="success" onClick={()=>setAppnexus(!appnexusSSP)}>Appnexus</Button>
                              </ButtonGroup>
                              <FormGroup>
                                { appnexusSSP && <>
                                   <label>Assigned Creative ID:</label>
                                   <Input
                                    id="appnexus_crid"
                                    defaultValue={creative.extensions["appnexus_crid"]}
                                    type="text">
                                </Input>
                                </>}
                                </FormGroup>
                                </Col>

                              <Col className="pr-md-1" md="2">
                              <ButtonGroup>
                                <Button size="sm" className="btn-fill" color="success" onClick={()=>setBidswitch(!bidswitchSSP)}>Bidswitch</Button>
                               </ButtonGroup>
                                { bidswitchSSP && <>
                                  <FormGroup>
                                  <label>Agency Name:</label>
                                   <Input
                                    id="agency_name"
                                    defaultValue={creative.extensions["agency_name"]}
                                    type="text">
                                    </Input>
                                <label>Advertiser Name:</label>
                                   <Input
                                    id="advertiser_name"
                                    type="text"
                                    defaultValue={creative.extensions["advertiser_name"]}/>
                                    </FormGroup>
                                </>}
                                </Col>
                            </Row>
                            <Row>
                            <Col className="pr-md-1" md="1"></Col>
                            <Col className="pr-md-1" md="2">
                              <ButtonGroup>
                                <Button size="sm" className="btn-fill" color="success" onClick={()=>setGoogle(!googleSSP)}>Google</Button>
                              </ButtonGroup>
                              <FormGroup>
                                { googleSSP && <>
                                  <label>Assigned Billing Id:</label>
                                   <Input
                                    id="billing_id"
                                    defaultValue={creative.extensions["billing_id"]}
                                    type="text">
                                </Input>
                                </>}
                                </FormGroup>
                                </Col>

                              <Col className="pr-md-1" md="2">
                              <ButtonGroup>
                                <Button size="sm" className="btn-fill" color="success" onClick={()=>setStroer(!stroerSSP)}>Stroer</Button>
                               </ButtonGroup>
                                { stroerSSP && <>
                                  <FormGroup>
                                  <label>AVR:</label>
                                   <Input
                                    id="avr"
                                    defaultValue={creative.extensions["avr"]}
                                    type="text">
                                    </Input>
                                <label>AVN:</label>
                                   <Input
                                    id="avn"
                                    defaultValue={creative.extensions["avn"]}
                                    type="text">
                                    </Input>
                                    </FormGroup>
                                </>}
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