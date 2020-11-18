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
import React, { useState } from "react";
import {Badge} from "reactstrap"
import classNames from "classnames";
import { Map as LeafletMap, TileLayer, Marker, Popup, CircleMarker } from 'react-leaflet';
// reactstrap components
import { Button, ButtonGroup, Card, CardHeader, CardBody, CardTitle, Row, Col } from "reactstrap";
import { useViewContext } from "../ViewContext";
import LoginModal from '../LoginModal'

/*
                  <CircleMarker center={[34.052235, -118.243683]} radius={10}>
                    <Popup>
                      Popup for any custom information.
                    </Popup>
                  </CircleMarker>
                  <Marker position={[34.152235, -118.143683]}>
                    <Popup>
                      Popup for any custom information.
                    </Popup>
                  </Marker>
                  */

var undef;
var map;

const LeafMap = (props) => {

  const vx = useViewContext();
  const [positions, setPositions] = useState([]);

  const handlePositions = (rows) => {
    console.log("SPV: " + rows.length);
    var old = positions;
    for(var i=0;i<rows.length;i++) {
      old.push(rows[i]);
    }
    setPositions(old);
  }


  const leafStyle = {
    height: '900px',
    width: '100%'
  };
  
  const setPositionsView = (rows) => {
    return(<>
    <CircleMarker center={[34.052235, -118.243683]} radius={100}>
    <Popup>
      Popup for any custom information.
    </Popup>
  </CircleMarker>
  <Marker position={[34.152235, -118.143683]}>
    <Popup>
      Popup for any custom information.
    </Popup>
  </Marker>
  </>);
  }

  const clear = () => {
    setPositions([]);
  }

  const setType = (data, server) => {

  }


  const handleZoom = (e) => {
   // alert('Current zoom level -> ' +  map.getCenter());
  };



  function stringify(value) {
		var seen = [];

		return JSON.stringify(value, function(key, val) {
   			if (val != null && typeof val == "object") {
        		if (seen.indexOf(val) >= 0) {
            		return;
        		}
        		seen.push(val);
    		}
    		return val;
			}, 2);
	}

  const setInstances = (na,server) => {
    setType("bids",server);
  }

  const show = () =>{
    console.log("Update badge count to: " + positions.length);
    return positions.length;
  }

  const save = () => {
    props.callback(true,positions)
  }

  const discard = () => {
    props.callback(false);
  }
 
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
              </Row>
              </CardHeader>
              <CardBody>
                  <LeafletMap style={leafStyle}
                    center={[34.052235, -118.243683]}
                    zoom={13}
                    onzoom={handleZoom}
                    ref={(ref) => { map = ref; }}
                  >
                  <TileLayer
                      url='http://{s}.tile.osm.org/{z}/{x}/{y}.png'
                  />
                  {setPositionsView(positions)}
                  </LeafletMap>
                </CardBody>
              </Card>
            </Col>
          </Row>
        </div>
      </>
    );
}

export default LeafMap;