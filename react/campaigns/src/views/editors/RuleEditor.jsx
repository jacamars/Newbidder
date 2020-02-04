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

const RuleEditor = (props) => {

  const [count, setCount] = useState(0);
  const [rule, setRule] = useState(props.rule);
  const [startDate, setStartDate] = useState(new Date());
  const vx = useViewContext();

  const nameChangedHandler = (event) => {
      rule.name = event.target.value;
      setRule(rule);
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

const getOperator = () => {
    return(
        <>
        <option>Domain</option>
        <option>Equals</option>
        <option>Exists</option>
        <option>Greater Than</option>
        <option>Greater Than Equals</option>
        <option>Inrange</option>
        <option>Less Than</option>
        <option>Less Than Equals</option>
        <option>Member</option>
        <option>Not Domain</option>
        <option>Not Equals</option>
        <option>Not Member</option>
        <option>Not Regex</option>
        <option>Not Stringin</option>
        <option>Stringin</option>
        </>
    );
}

const getOperandType = () => {
  return(
    <>
    <option>Integer</option>
    <option>String</option>
    <option>Double</option>
    </>
  );
}

const getOperandOrdinal = () => {
  return(
    <>
    <option>Scalar</option>
    <option>List</option>
    </>
  );
}

  const redraw = () => {
      setCount(count+1);
  }

        return (
            <>
              <div className="content">
                <Row>
                  <Col>
                    <Card>
                      <CardHeader>
                        <h5 className="title">Edit Rule Details</h5>
                      </CardHeader>
                      <CardBody>
                        <Form>
                          <Row>
                            <Col className="pr-md-1" md="2">
                              <FormGroup>
                                <label>SQL ID (disabled)</label>
                                <Input
                                  defaultValue={rule.id}
                                  disabled
                                  type="text"
                                />
                              </FormGroup>
                            </Col>
                            <Col className="px-md-1" md="3">
                              <FormGroup>
                                <label>Name</label>
                                <Input
                                  defaultValue={rule.name}
                                  placeholder="Target Name (Required)"
                                  type="text"
                                />
                              </FormGroup>
                            </Col>
                            <Col className="px-md-1" md="3">
                              <FormGroup>
                              <label>Required</label>
                              <Input type="select" name="select" id="required">
                                    {getTrueFalseOptions(!rule.notPresentOk)}
                                </Input>        
                              </FormGroup>
                            </Col>
                          </Row>
                          <Row>
                            <Col className="pr-md-1" md="4">
                              <FormGroup>
                                <label>RTB Specification</label>
                                <Input type="input" id="region" >
                                    {rule.specification}
                                </Input>
                              </FormGroup>
                            </Col>
                            <Col className="px-md-1" md="4">
                              <FormGroup>
                                <label>Operator</label>
                                <Input type="select" name="select" id="operator">
                                    {getOperator()}
                                </Input>     
                              </FormGroup>
                            </Col>
                          </Row>
        
                          <Row>  
                             <Col className="pl-md-1" md="4">
                              <FormGroup>
                                <label>Operand Value</label>
                                <Input type="input" name="text" id="operand" defaultValue={rule.operand}/>   
                              </FormGroup>
                            </Col> 
                            <Col className="pr-md-1" md="4">
                              <FormGroup>
                              <label>Use Set as Operand</label>
                                <Input type="input" name="text" id="set-operand"/>   
                              </FormGroup>
                            </Col>
                            </Row>
                            <Row>
                            <Col className="px-md-1" md="4">
                              <FormGroup>
                              <label>Operand Type</label>
                                <Input type="select" name="text" id="operand-type"> 
                                  {getOperandType()}
                                </Input>
                              </FormGroup>
                              </Col>
                              <Col className="px-md-1" md="4">
                              <FormGroup>
                              <label>Operand Ordinal</label>
                                <Input type="select" name="text" id="ordinal">   
                                  {getOperandOrdinal()}
                                </Input>
                              </FormGroup>
                            </Col>
                          </Row>
                    
                        </Form>
                      </CardBody>
                      <CardFooter>
                        <Button className="btn-fill" color="primary" 
                            type="submit" onClick={() => props.callback(rule)}>
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
 export default RuleEditor;