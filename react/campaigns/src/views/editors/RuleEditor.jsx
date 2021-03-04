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

import LeafMap from "../LeafMap.jsx"
import GeoEditor from "./GeoEditor";
import { useViewContext } from "../../ViewContext";
import { undef, blackStyle, whiteStyle, stringify} from "../../Utils";

import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

var ops = [ "DOMAIN","EQUALS","EXISTS","GREATER THAN","GREATER THAN EQUALS","IDL", "INRANGE","INTERSECTS", "LESS THAN","LESS THAN EQUALS",
  "MEMBER","NOT DOMAIN","NOT EXISTS", "NOT IDL", "NOT INRANGE", "NOT INTERSECTS", "NOT EQUALS","NOT MEMBER","NOT REGEX","NOT STRING","REGEX","STRINGIN"];

var types =["integer","string","double"];
var ords =["scalar","list"];

const RuleEditor = (props) => {

  const [showMap, setShowMap] = useState(false);
  const [geo, setGeo] = useState([]);
  const [zoom, setZoom] = useState(1);
  const [center, setCenter] = useState([44.414165,8.942184]);

  useEffect(() => {
    if (props.rule.op === 'IDL' || props.rule.op === 'NOT IDL')
      setVisible(false);
    if (props.rule.op === 'INRANGE') {
      var parts = props.rule.operand.split(",");
      if (parts.length > 0) {
        var x = [];
        for (var i=0;i<parts.length;i++) {
          x.push(Number(parts[i]));
          setZoom(8);
        }
        setGeo(x);
        setCenter([x[0],x[1]]);
      }
      setShowMap(true)
    }
  }, []);


  const [count, setCount] = useState(0);
  const [visible, setVisible] = useState(props.rule.op === 'IDL' || props.rule.op === 'NOT IDL');
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

const completeMap = (pos) => {
  if (pos === undef) { 
    pos = getOldGeoValues();
    setGeo(pos);
  } else {
    var str = "";
      rule.operand = [];
      for (var i=0; i<pos.length-1;i++) {
        str += pos[i].toString() + ",";
      }
    str += pos[pos.length-1].toString();
    rule.operand = str;
    setRule(rule);
  }
}

const getOldGeoValues = () => {
  var parts = props.rule.operand.split(",");
  if (parts.length > 0) {
    var x = [];
    for (var i=0;i<parts.length;i++) {
      x.push(Number(parts[i]));
    }
    setGeo(x);
    setCenter([x[0],x[1]]);
  }
  return x;
}

const addNewRule = () => {
  if (!rule.id)
    rule.id = 0;
  rule.value = undef;
  if ( document.getElementById("operand") === null)  {
    if (rule.operand === undefined) {
      rule.operand = "0,0,0"
    } else {
      rule.operand_type = "double"
      rule.operand_ordinal = "array";
    }
  }
  else {
    rule.operand = document.getElementById("operand").value;
    if (document.getElementById('ordinal') !== null)
      rule.operand_ordinal = document.getElementById("ordinal").value;
    else
      rule.operand_ordinal= 'scalar';
    if (document.getElementById('type') !== null)
      rule.operand_type = document.getElementById("type").value;
  }
  rule.name = document.getElementById("name").value;
  rule.rtbspecification = document.getElementById("hierarchy").value;
  rule.op = document.getElementById("operator").value;
  if (document.getElementById("required").value ==="true")
    rule.notPresentOk=false;
  else
    rule.notPresentOk=true;

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
  if (op === 'INRANGE') {
    document.getElementById("hierarchy").value = "device.geo";
    setShowMap(true);
  } else
  if (op === 'IDL' || op === 'NOT IDL') {
    document.getElementById('hierarchy').value = 'user.ext.eids';
    //document.getElementById('type').value = 'string';
    setVisible(false);
  } else
    setVisible(true);

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
                          { showMap ? 
                            <GeoEditor rule={true} callback={completeMap} setGeo={setGeo} 
                                        geo={geo} setZoom={setZoom} zoom={zoom} center={center} setCenter={setCenter}/>
                            :
                          <Row>  
                             <Col className="pl-md-1" md="3">
                              <FormGroup>
                                <label>Operand Value</label>
                                <Input type="input" id="operand" defaultValue={rule.operand}/>   
                              </FormGroup>
                            </Col> 
                            {visible && <>
                            <Col className="px-md-1" md="3">
                              <FormGroup>
                              <label>Operand Type</label>
                                <Input type="select" id="type"> 
                                  {getOperandType()}
                                </Input>
                              </FormGroup>
                              </Col>
                              <Col className="px-md-1" md="3">
                              <FormGroup>
                              <label>Operand Ordinal</label>
                                <Input type="select" id="ordinal">   
                                  {getOperandOrdinal()}
                                </Input>
                              </FormGroup>
                            </Col>
                            </>}
                          </Row>}
                        </Form>
                      </CardBody>
                      <CardFooter>
                        <Button className="btn-fill" color="primary" 
                            type="submit" onClick={() => addNewRule()} disabled={rule.readOnly}>
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