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

const getDomainTypes = () => {
    return(
        <>
        </>
    );
}

const getDomainValues = () => {
    return(
        <>
        </>
    );
}


const getBigDataSet = () => {
    return(
        <>
        </>
    );
}

const getSelectedCountries = () => {
    return(
        <>
        </>
    )
}

const getSelectedModels = () => {
    return(
        <>
        </>
    )
}

const getSelectedMakes = () => {
    return(
        <>
        </>
    )
}

const getSelectedDeviceTypes = () => {
    return(
        <>
        </>
    )
}

const getWhiteList = () => {
    return(
        <>
        </>
    )
}

const getBlackList = () => {
    return(
        <>
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
      if (target.id === 0)
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
                                  defaultValue={target.name}
                                  placeholder="Target Name (Required)"
                                  type="text"
                                />
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
                          </Row>
                          <Row>
                            <Col className="pr-md-1" md="4">
                              <FormGroup>
                                <label>Domain List Type</label>
                                <Input type="select" id="region" >
                                    {getDomainTypes()}
                                </Input>
                              </FormGroup>
                            </Col>
                            <Col className="px-md-1" md="4">
                              <FormGroup>
                                <label>Domain Values</label>
                                <Input type="select" name="select" id="exchanges" multiple>
                                    {getDomainValues()}
                                </Input>     
                              </FormGroup>
                            </Col>
                            <Col className="pl-md-1" md="4">
                              <FormGroup>
                                <label>Use Big Data Set for Domain Values</label>
                                <Input type="select" id="target">
                                    {getBigDataSet()}
                                </Input>
                              </FormGroup>
                            </Col>
                          </Row>
                          <Row>   
                            <Col className="pr-md-1" md="4">
                              <FormGroup>
                              <label>Geo Latitude</label>
                                <Input type="input" name="text" id="latitude" defaultValue={target.lat}/>   
                              </FormGroup>
                            </Col>
                            <Col className="px-md-1" md="2">
                              <FormGroup>
                              <label>Geo Longitude</label>
                                <Input type="input" name="text" id="longitude" defaultValue={target.lon}/>   
                              </FormGroup>
                              </Col>
                              <Col className="px-md-1" md="2">
                              <FormGroup>
                              <label>Geo Range</label>
                                <Input type="input" name="text" id="rane" defaultValue={target.range}/>   
                              </FormGroup>
                            </Col>
                          </Row>
                          <Row>
                          <Col className="pr-md-1" md="4">
                            <FormGroup>
                              <label>Geo Region</label>
                                <Input type="input" name="text" id="region" defaultValue={target.region}/>   
                              </FormGroup>
                            </Col>
                            <Col className="px-md-1" md="2">
                             <FormGroup>
                              <label>Rules</label>
                                <Input type="select" name="select" id="countries" multiple>
                                    {getSelectedCountries()}
                                </Input>     
                              </FormGroup>
                            </Col>
                            <Col className="pr-md-1" md="4">
                            <FormGroup>
                              <label>Carrier</label>
                                <Input type="input" name="text" id="region" defaultValue={target.region}/>   
                              </FormGroup>
                            </Col>
                          </Row>
                          <Row>
                           <Col className="pr-md-1" md="4">
                            <FormGroup>
                              <label>Operating System</label>
                                <Input type="input" name="text" id="os" defaultValue={target.os}/>   
                              </FormGroup>
                            </Col>
                            <Col className="px-md-1" md="2">
                             <FormGroup>
                              <label>Make</label>
                                <Input type="select" name="select" id="makes" multiple>
                                    {getSelectedMakes()}
                                </Input>     
                              </FormGroup>
                            </Col>
                            <Col className="pr-md-1" md="4">
                            <FormGroup>
                                <label>Make</label>
                                <Input type="select" name="select" id="models" multiple>
                                    {getSelectedModels()}
                                </Input>     
                            </FormGroup>
                            </Col>     
                            <Col className="pr-md-1" md="4">
                                <FormGroup>
                                    <label>Device Types</label>
                                    <Input type="select" name="select" id="device-types" multiple>
                                        {getSelectedDeviceTypes()}
                                    </Input>     
                                </FormGroup>
                            </Col>                           
                          </Row>
                          <Row>
                            <Col>
                                <FormGroup>
                                    <label>IAB Whitelist</label>
                                    <Input type="select" name="select" id="whitelist" multiple>
                                        {getWhiteList()}
                                    </Input>     
                                </FormGroup>    
                            </Col>
                            <Col>
                                <FormGroup>
                                    <label>IAB Blacklist</label>
                                    <Input type="select" name="select" id="blacklist" multiple>
                                        {getBlackList()}
                                    </Input>     
                                </FormGroup>    
                            </Col>
                          </Row>
                        </Form>
                      </CardBody>
                      <CardFooter>
                        <Button className="btn-fill" color="primary" 
                            type="submit" onClick={() => props.callback(target)}>
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