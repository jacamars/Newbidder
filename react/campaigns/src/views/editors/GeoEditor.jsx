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
  const [count, setCount] = useState(0);
  const [zoom, setZoom] = useState(1);
  const [center, setCenter] = useState([44.414165,8.942184]);

  useEffect(() => {
     setGeo(props.geo);
     if (props.geo !== undef && props.geo.length > 0 && props.geo[0] !== 0) {
      setZoom(8);
      setCenter([props.geo[0],props.geo[1]]);
     } else {
       setCenter([44.414165,8.942184]);
       setZoom(2);
     }
     setShowControl(false);
  }, []);


  const mapper = () => {
    setShowMap(true);
    setGeo(props.geo);
  }

  const removeGeo = (i) => {
    i = i * 3;
    var x = geo;
    x.splice(i,3)
    setGeo(x);
    setShowControl(true);
    setCount(count+1);
  }

  const makeNewGeo = () => {
    var geo = [];
    if (props.geo !== undef)
      geo = props.geo;

    if (geo.length > 0 && geo[geo.length-3] !== 0) {
      geo.push(0); 
      geo.push(0);
      geo.push(0);
    }
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

  // Forground color for delete
  const RED = {
    color: 'red'
  }
  const GREEN = {
    color: 'green'
  }

  const save = (mode) => {
    var ngeo = geo;
    for (var i = geo.length-3; i >= 0; i -=3) {
      if (geo[i] === 0) {
        geo.splice(i,3)
      }
    }
    if (mode) {
      props.callback(geo);
    } else {
      props.callback();
    }
    setShowMap(false);
    setShowControl(false);
  }

  const GetGeoView = () => {
    if (geo === undef || geo.length === 0)
      makeNewGeo();

    var x = [];
    for (var i=0;i<geo.length;i+=3) {
        var y = {"lat":geo[i], "lon":geo[i+1], "range": geo[i+2]};
        x.push(y);
    }

  if (showMap) {
      return(<LeafMap callback={save} geo={geo} setZoom={setZoom} zoom={zoom} center={props.center} setCenter={props.setCenter}/>)
  } else {
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
  }


  return(
    <>
      <Row>
        <Button onClick={()=>makeNewGeo()}  ><span aria-hidden="true" style={GREEN}>+</span></Button>
        <Button size="sm" className="btn-fill" color="success" onClick={mapper}>Map</Button>
      </Row>
      <GetGeoView key={"geoview-"+count} />
      {showControl && 
      <Row>
        <Button onClick={()=>save(true)} color="success" >Save</Button>
        <Button onClick={()=>save(false)} color="danger">Discard</Button>
      </Row>}
    </>
  );
};

export default GeoEditor;