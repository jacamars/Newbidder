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
import { undef, blackStyle, whiteStyle} from "../../Utils";

import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

var ops = [ "DOMAIN","EQUALS","EXISTS","GREATER THAN","GREATER THAN EQUALS","IDL", "INRANGE","INTERSECTS", "LESS THAN","LESS THAN EQUALS",
  "MEMBER","NOT DOMAIN","NOT EXISTS", "NOT IDL", "NOT INRANGE", "NOT INTERSECTS", "NOT EQUALS","NOT MEMBER","NOT REGEX","NOT STRING","REGEX","STRINGIN"];

var types =["integer","string","double"];
var ords =["scalar","list"];

const RuleEditor = (props) => {

  const [count, setCount] = useState(0);
  const [rule, setRule] = useState(props.rule);
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

const addNewRule = (r) => {
  if (!rule.id)
    rule.id = 0;

  var operand = document.getElementById("operand").value;
  var type = document.getElementById("type").value;
  rule.operand_ordinal = document.getElementById("ordinal").value;

  rule.name = document.getElementById("name").value;
  rule.rtbspecification = document.getElementById("hierarchy").value;
  rule.op = document.getElementById("operator").value;
  rule.operand = operand
  rule.operand_type = type;
  if (document.getElementById("required").value ==="true")
    rule.notPresentOk=false;
  else
    rule.notPresentOk=true;
  alert("XXX: " + JSON.stringify(rule,null,2));
  props.callback(rule);
}

const getOperator = () => {
  var items = []; 
    for (var i=0;i<ops.length;i++) {
      var x = ops[i];
      if (rule.op === x)
        items.push(<option key={"regions-"+x} selected>{x}</option>);
      else
        items.push(<option key={"exchanges-"+x}>{x}</option>);
    }
    return(items);
}

const opchange = (e) => {
  var op = e.target.value;
  if (op === 'IDL') {
    document.getElementById('hierarchy').value = 'user.ext.eids';
    document.getElementById('type').value = 'string';
  }

}

const getOperandType = () => {
  var items = []; 
    for (var i=0;i<types.length;i++) {
      var x = types[i];
      if (rule.operand_type === x)
        items.push(<option key={"operand-"+x} selected>{x}</option>);
      else
        items.push(<option key={"operand-"+x}>{x}</option>);
    }
    return(items);
}

const getOperandOrdinal = () => {
  var items = []; 
  for (var i=0;i<ords.length;i++) {
    var x = ords[i];
    if (rule.operand_ordinal === x)
      items.push(<option key={"ords-"+x} selected>{x}</option>);
    else
      items.push(<option key={"ords-"+x}>{x}</option>);
  }
  return(items);
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
                                  style={(document.body.classList.contains("white-content")) 
                                    ? blackStyle : whiteStyle}
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
                                  id="name"
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
                                <Input type="input" id="hierarchy" defaultValue={rule.hierarchy}/>
                              </FormGroup>
                            </Col>
                            <Col className="px-md-1" md="4">
                              <FormGroup>
                                <label>Operator</label>
                                <Input type="select" name="select" id="operator" onChange={(e)=>{opchange(e)}}>
                                    {getOperator()}
                                </Input>     
                              </FormGroup>
                            </Col>
                          </Row>
        
                          <Row>  
                             <Col className="pl-md-1" md="4">
                              <FormGroup>
                                <label>Operand Value</label>
                                <Input type="input" id="operand" defaultValue={rule.operand}/>   
                              </FormGroup>
                            </Col> 
                            </Row>
                            <Row>
                            <Col className="px-md-1" md="4">
                              <FormGroup>
                              <label>Operand Type</label>
                                <Input type="select" id="type"> 
                                  {getOperandType()}
                                </Input>
                              </FormGroup>
                              </Col>
                              <Col className="px-md-1" md="4">
                              <FormGroup>
                              <label>Operand Ordinal</label>
                                <Input type="select" id="ordinal">   
                                  {getOperandOrdinal()}
                                </Input>
                              </FormGroup>
                            </Col>
                          </Row>
                    
                        </Form>
                      </CardBody>
                      <CardFooter>
                        <Button className="btn-fill" color="primary" 
                            type="submit" onClick={() => addNewRule(rule)} disabled={rule.readOnly}>
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