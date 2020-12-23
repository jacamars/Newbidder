/*!

=========================================================
* Black Dashboard React v1.0.0
=========================================================

* Product Page: https://www.creative-tim.com/product/black-dashboard-react
* Copyright 2019 Creative Tim (https://www.creative-tim.com)
* Licensed under MIT (https://github.com/creativetimofficial/black-dashboard-react/blob/master/LICENSE.md)

* Coded by Creative Tim

=========================================================

* The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

*/
import React, { useState, useEffect } from "react";
import { MapContainer, TileLayer, Marker, Popup, CircleMarker, Circle, useMapEvents, ZoomControl } from 'react-leaflet';
// reactstrap components
import { Button, ButtonGroup, Card, CardHeader, CardBody, CardTitle, Input, Label, Row, Col } from "reactstrap";
import { useViewContext } from "../ViewContext";
import { stringify, undef} from "../Utils";
import LoginModal from '../LoginModal'


var lat = 44.414165;
var lon = 8.942184

const LeafMap = (props) => {

  const vx = useViewContext();
  const [geo, setGeo] = useState([]);
  const [count, setCount] = useState(0);

  useEffect(() => {
    //console.log("REF"+JSON.stringify(props.geo,null,2)+", zoom="+props.zoom);
    setGeo(props.geo);
  }, []);

  const leafStyle = {
    height: '500px',
    width: '100%'
  };
  
  const SetPositionsView = () => {
    const map = useMapEvents({
      click(e) {
        var x = geo;
        if (e.originalEvent.ctrlKey) {
          if (x === undef) {
            x = [];
          }
          x.push(e.latlng.lat);
          x.push(e.latlng.lng);
          x.push(10000);
          props.setCenter([e.latlng.lat,e.latlng.lng])
          setGeo(x);
          props.setZoom(map.getZoom());
          setCount(count+1);
        }
      },
    })
    return(null);
  }

  const doit = (lat) =>{
    var x = geo;
    for (var i=0;i<x.length;i+=3) {
      if (x[i] === lat) {
        x.splice(i,3);
        break;
      }
    }
    setGeo(x);
    setCount(count+1);
  }


  /*
    <Popup>
          <button onClick={()=>doit(index)}>Delete</button>{pos.range}
        </Popup>
        */

  const ShowPoints = () => {


    var x = [];
    if (geo === undef)
      return (null);

    for (var i=0;i<geo.length;i+=3) {
      if (geo[i] !== 0) {
        var y = {"lat":geo[i], "lon":geo[i+1], "range": geo[i+2]};
        x.push(y);
      } 
    }

    if (x.length === 0)
      return null;

    return (
      x.map((pos, index) => (
        <Marker position={[pos.lat, pos.lon]} >
          <Popup maxWidth={720}>
              <ItemGrid lat={pos.lat} range={pos.range}/>
          </Popup>
        <Circle center={[pos.lat, pos.lon]} radius={pos.range}/>
        </Marker>
     ))
    );
  }

  const changeRange = (e,lat) => {
    if (e == null) {
      setCount(count+1);
    } else {
      var v = Number(e.target.value);
      var x = geo;
      for (var i=0;i<x.length;i+=3) {
        if (x[i] === lat) {
          x[i+2] = v;
          setGeo(x);
          return;
       } 
      }
    }
  }

  const ItemGrid = (props) => {
    return(
      <>
      <Button onClick={()=>doit(props.lat)}>Delete</Button><br/>
      {"Range: "}<Input id="rangefinder" color="error" type="text" defaultValue={props.range} onChange={(e)=>changeRange(e,props.lat)}/>
      <Button color="success" onClick={()=>changeRange()}>Done</Button>
      </>
    )
  }

  const setInstances = (na,server) => {

  }

  const save = () => {
    props.callback(true)
  }

  const discard = () => {
    setGeo([]);
    props.callback(false);
  }

  console.log("REF"+JSON.stringify(geo,null,2)+", zoom="+props.zoom);
 
  return (
      <>
        <div className="content">
        { !vx.isLoggedIn && <LoginModal callback={setInstances} />}
          <Row>
            <Col md="12">
              <Card className="card-plain">
              <CardHeader>
              <Row>
                <Button size="sm" className="btn-fill" color="success" onClick={save}>Save</Button>
                <Button size="sm" className="btn-fill" color="danger" onClick={discard}>Discard</Button>
                &nbsp;&nbsp;Control-click for new point. Click on marker to edit.
              </Row>
              </CardHeader>
              <CardBody>
                  <MapContainer key={"map-"+count} style={leafStyle}
                    center={props.center}
                    zoom={props.zoom}
                  >
                  <TileLayer
                      url='http://{s}.tile.osm.org/{z}/{x}/{y}.png'
                  />
                    <SetPositionsView/>
                    <ShowPoints  key={"mapper-"+count}/>
                  </MapContainer>
                </CardBody>
              </Card>
            </Col>
          </Row>
        </div>
      </>
    );
}

export default LeafMap;