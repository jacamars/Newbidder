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

const CampaignEditor = (props) => {

  const [count, setCount] = useState(0);
  const [campaign, setCampaign] = useState(props.campaign);
  const [startDate, setStartDate] = useState(new Date());
  const vx = useViewContext();

  const nameChangedHandler = (event) => {
      campaign.adId = event.target.value;
      setCampaign(campaign);
  }

  const adxChangedHandler = (event) => {
    campaign.isAdx = event.target.value;
    setCampaign(campaign);
}

const domainChangedHandler = (event) => {
    campaign.adomain = event.target.value;
    setCampaign(campaign);
    console.log(event.target.value);
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

const getSelectedExchangeOptions = () => {
    return(
        <>
        <option>Adx</option>
        <option>Bidswitch</option>
        <option>Nexage</option>
        <option>Openx</option>
        <option>Smaato</option>
        <option>Stroer</option>
        </>
    );
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
    return(
        <>
        <option>US</option>
        <option>APAC</option>
        <option>EUROPE</option>
        <option>Russia</option>
        </>
    )
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
                            <Col className="pr-md-1" md="1">
                              <FormGroup>
                                <label>SQL ID (disabled)</label>
                                <Input
                                  defaultValue={campaign.id}
                                  disabled
                                  type="text"
                                />
                              </FormGroup>
                            </Col>
                            <Col className="px-md-1" md="3">
                              <FormGroup>
                                <label>Ad Id</label>
                                <Input
                                  defaultValue={campaign.adId}
                                  placeholder="Campaign Name (Required)"
                                  type="text"
                                />
                              </FormGroup>
                            </Col>
                            <Col className="pl-md-1" md="4">
                              <FormGroup>
                                <label htmlFor="addomain">
                                  Ad Domain
                                </label>
                                <Input placeholder="Ad domain (required)"
                                  defaultValue={campaign.adomain}
                                  type="text" />
                              </FormGroup>
                            </Col>
                            <Col className="pl-md-1" md="4">
                              <FormGroup>
                              <Label for="exampleSelect" >Fraud</Label>
                                <Input type="select" name="select" id="exampleSelect">
                                    {getTrueFalseOptions(campaign.forensiq)}
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
                             <Col className="px-md-1" md="3">
                              <FormGroup>
                                <label>Spend Rate/Minute</label>
                                <Input id="spendRate"
                                  defaultValue={campaign.assignedSpendRate}
                                  type="text"
                                />
                              </FormGroup>
                            </Col>
                          </Row>
                          <Row>
                            <Col md="4">
                              <FormGroup>
                                <label>Frequency Cap Variable</label>
                                <Input id="freq_variable"
                                  defaultValue="request.device.ip"
                                  placeholder="RTB variable to frequency cap on"
                                  type="text"
                                />
                              </FormGroup>
                            </Col>
                            <Col md="4">
                              <FormGroup>
                                <label>Frequency Limit</label>
                                <Input id='freq_limit"'
                                  defaultValue="0"
                                  placeholder="Max number"
                                  type="text"
                                />
                              </FormGroup>
                            </Col>
                            <Col md="4">
                              <FormGroup>
                                <label>Duration</label>
                                <Input id="duration"
                                  defaultValue="0"
                                  placeholder="Duration in minutes"
                                  type="text"
                                />
                              </FormGroup>
                            </Col>
                          </Row>
                          <Row>
                            <Col className="pr-md-1" md="4">
                              <FormGroup>
                                <label>Region</label>
                                <Input type="select" id="region" multiple>
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
                                <Input placeholder="0" type="number" id="total_budget"/>
                              </FormGroup>
                            </Col>
                            <Col className="pl-md-1" md="2">
                              <FormGroup>
                                <label>Daily Budget</label>
                                <Input placeholder="0" type="number" id="daily_budget"/>
                              </FormGroup>
                            </Col>
                            <Col className="pl-md-1" md="2">
                              <FormGroup>
                                <label>Hourly Budget</label>
                                <Input placeholder="0" type="number" id="hourly_budget" />
                              </FormGroup>
                            </Col>
                          </Row>
                        </Form>
                      </CardBody>
                      <CardFooter>
                        <Button className="btn-fill" color="primary" type="submit">
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
 export default CampaignEditor;