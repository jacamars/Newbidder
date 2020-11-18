import React, { useState, useEffect } from "react";

// reactstrap components
import {
  Button,
  ButtonGroup,

  FormGroup,
  Input,
  Row,
  Col
} from "reactstrap";
import LeafMap from "../LeafMap.jsx"


var undef;

const GeoEditor = (props) => {

  const [geo, setGeo] = useState([]);
  const [showControl, setShowControl] = useState();
  const [showMap, setShowMap] = useState(false);

  useEffect(() => {
     setGeo(props.geo);
     setShowControl(false);
  }, []);


  const mapper = () => {
    setShowMap(true)
  }

  const completeMap = (save,pos) => {
    setShowMap(false);
  }

  const removeGeo = (i) => {
    geo.splice(i,3);
    setGeo(geo);
    setShowControl(true);
  }

  const makeNewGeo = () => {
    var geo = [];
    if (props.geo !== undef)
      geo = props.geo;;

    geo.push(0); 
    geo.push(0);
    geo.push(0);
    props.setGeo(geo);
    setShowControl(true);
  }

  const change = (e,what,index) => {
      var geo = props.geo;
      index *=3;
      var v = e.target.value;
      if (what === "lat") {
        geo[index] = Number(v);
      } else
      if (what === "lon") {
          geo[index+1] = Number(v);
      } else {
        geo[index+2] = Number(v);
      }
      setGeo(geo);
      setShowControl(true);
  }

  const colorize = (x) => {
    if (x)
      return "primary";
    return "secondary";
  }

  // Forground color for delete
  const RED = {
    color: 'red'
  }
  const GREEN = {
    color: 'green'
  }

  const save = (mode) => {
    if (mode)
      props.setGeo(geo);
    else
      props.setGeo(props.geo);
    setShowControl(false);
  }


  const getGeoView = () => {
    if (props.geo === undef || props.geo.length === 0)
      makeNewGeo();

    var x = [];
    for (var i=0;i<props.geo.length;i+=3) {
      var y = {"lat":props.geo[i], "lon":props.geo[i+1], "range": props.geo[i+2]};
      x.push(y);
    }

  if (showMap)
    return(<LeafMap callback={completeMap}/>);
  else
    return (
      x.map((geo, index) => (
        <Row>
        <Col className="px-md-2" md="3">
          <FormGroup>
            <label>Latitude</label>
            <Input
              onChange={ (e) => change(e,"lat",index)}
              defaultValue={geo.lat}
              type="text"
            />
          </FormGroup>
        </Col>
        <Col className="px-md-2" md="3">
          <FormGroup>
            <label>Longitude</label>
            <Input
              onChange={ (e) => change(e,"lon",index)}
              defaultValue={geo.lon}
              type="number"
            />
          </FormGroup>
        </Col>
        <Col className="px-md-4" md="3">
          <FormGroup>
            <label>Range</label>
            <Input
              onChange={ (e) => change(e,"range",index)}
              defaultValue={geo.range}
              type="number"
            />
          </FormGroup>
        </Col>
        <Col className="px-md-4" md="1">
          <br/>
          <ButtonGroup>
           <Button close onClick={(e)=>removeGeo(index)}  ><span aria-hidden="true" style={RED}>–</span></Button>
          </ButtonGroup>
        </Col>
      </Row>))
    );
  }

  return(
    <>
      <Row>
        <Button onClick={()=>makeNewGeo()}  ><span aria-hidden="true" style={GREEN}>+</span></Button>
        <Button size="sm" className="btn-fill" color="success" onClick={mapper}>Map</Button>
      </Row>
      {getGeoView()}
      {showControl && 
      <Row>
        <Button onClick={()=>save(true)} color="success" >Save</Button>
        <Button onClick={()=>save(false)} color="danger">Discard</Button>
      </Row>}
    </>
  );
};

export default GeoEditor;