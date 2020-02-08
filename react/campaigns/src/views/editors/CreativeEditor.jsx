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

    x.name = document.getElementById("name").value;
    x.bid_ecpm = Number(document.getElementById("price").value);
    x.cur = document.getElementById("currency").value;
    x.total_cost = 0;
    x.hourly_cost = 0;
    x.daily_cost = 0;
    x.interval_start = 0;
    x.interval_end = 0;

    x.iurl = "";
    x.contenttype = "html";
    x.width = 0;
    x.height = 0;
    x.htmltemplate = "helpme";

    x.rules = [...document.getElementById("rules").options]
      .filter((x) => x.selected)
      .map((x)=>Number(x.value));

    alert(JSON.stringify(x,null,2));
    props.callback(x);
  }

        return (
            <>
              <div className="content">
                <Row>
                  <Col>
                    <Card>
                      <CardHeader>
                        <h5 className="title">Edit Creative Details ({creative.react_type})</h5>
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
                        <Button className="btn-fill" c
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