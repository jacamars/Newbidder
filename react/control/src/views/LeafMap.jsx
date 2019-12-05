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
import classNames from "classnames";
import { Map as LeafletMap, TileLayer, Marker, Popup, CircleMarker } from 'react-leaflet';
// reactstrap components
import { Button, ButtonGroup, Card, CardHeader, CardBody, CardTitle, Row, Col } from "reactstrap";
import { useViewContext } from "../ViewContext";

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

var map;
const LeafMap = () => {

  const vx = useViewContext();

  const [count, setCount] = useState(0);

  // const [mapType, setMapType] = useState('none');
//  const [positions, setPositions] = useState([]);
  //  {'x': 34.152235, 'y': -118.143683},
   // {'x': 34.052235, 'y':-118.243683}]);

  const  loggerCallback = (spec,logname,  callback) => {
      var previous_response_length = 0;
      var xhr = new XMLHttpRequest()
      xhr.open("GET", "http://" + spec + "/shortsub"+ "?topic=" + logname, true);
      xhr.onreadystatechange = checkData;
      xhr.send(null);
      
      function checkData() {
        if (xhr.readyState == 3) {
          var response = xhr.responseText;
          var chunk = response.slice(previous_response_length);
          console.log("GOT SOME CHUNK DATA: " + chunk);
          var i = chunk.indexOf("{");
          if (i < 0)
            return;
          if (chunk.trim().length === 0)
            return;
          chunk = chunk.substring(i);
          previous_response_length = response.length;
          
          console.log("GOT DATA: " + chunk);
          var lines = chunk.split("\n");
          var rows = []
          for (var j = 0; j < lines.length; j++) {
            var line = lines[j];
            line = line.trim();
            if (line.length > 0) {
              var y = JSON.parse(line);
              rows.push(y);
            }
            callback(rows)
            redraw();
          }
        }
      }
      ;
    }

  const leafStyle = {
    height: '900px',
    width: '100%'
  };

  const redraw = () => {
    console.log(JSON.stringify(vx.mapPositions,null,2));
    setCount(count+1);
  }
  const setPositionsView = (rows) => {
    return(
      rows.map((row, i) => (<Marker key={'"position-' + i + "'"} position={[row.x, row.y]}>
         <Popup>
           Popup for any custom information.
         </Popup>
       </Marker>))
    )
  }

  const setType = (data) => {
    vx.setMapType(data);
    loggerCallback("localhost:7379",data,  vx.addMapPositions);
  }

  const handleZoom = (e) => {
    vx.setZoomLevel( map.leafletElement.getZoom() );
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


  if (vx.mapType === '')
    setType('bids');


  return (
      <>
        <div className="content">
          <Row>
            <Col md="12">
              <Card className="card-plain">
              <CardHeader>
                  <Row>
                    <Col className="text-left" sm="6">
                      <CardTitle tag="h2">Events Map</CardTitle>
                    </Col>
                    <Col sm="6">
                      <ButtonGroup
                        className="btn-group-toggle float-right"
                        data-toggle="buttons"
                      >
                        <Button
                          tag="label"
                          className={classNames("btn-simple", {
                            active: vx.mapType === "requests"
                          })}
                          color="info"
                          id="0"
                          size="sm"
                          onClick={() => vx.setMapType("requests")}
                        >
                          <input
                            defaultChecked
                            className="d-none"
                            name="options"
                            type="radio"
                          />
                          <span className="d-none d-sm-block d-md-block d-lg-block d-xl-block">
                            Requests
                          </span>
                          <span className="d-block d-sm-none">
                            <i className="tim-icons icon-single-02" />
                          </span>
                        </Button>
                        <Button
                          color="info"
                          id="1"
                          size="sm"
                          tag="label"
                          className={classNames("btn-simple", {
                            active: vx.mapType === "bids"
                          })}
                          onClick={() => vx.setMapType("bids")}
                        >
                          <input
                            className="d-none"
                            name="options"
                            type="radio"
                          />
                          <span className="d-none d-sm-block d-md-block d-lg-block d-xl-block">
                            Bids
                          </span>
                          <span className="d-block d-sm-none">
                            <i className="tim-icons icon-gift-2" />
                          </span>
                        </Button>
                        <Button
                          color="info"
                          id="2"
                          size="sm"
                          tag="label"
                          className={classNames("btn-simple", {
                            active: vx.mapType === "wins"
                          })}
                          onClick={() => vx.setMapType("wins")}
                        >
                          <input
                            className="d-none"
                            name="options"
                            type="radio"
                          />
                          <span className="d-none d-sm-block d-md-block d-lg-block d-xl-block">
                            Wins
                          </span>
                          <span className="d-block d-sm-none">
                            <i className="tim-icons icon-tap-02" />
                          </span>
                        </Button>
                        <Button
                          color="info"
                          id="3"
                          size="sm"
                          tag="label"
                          className={classNames("btn-simple", {
                            active: vx.CardHeadermapType === "conversions"
                          })}
                          onClick={() => vx.setMapType("conversions")}
                        >
                          <input
                            className="d-none"
                            name="options"
                            type="radio"
                          />
                          <span className="d-none d-sm-block d-md-block d-lg-block d-xl-block">
                            Conversions
                          </span>
                          <span className="d-block d-sm-none">
                            <i className="tim-icons icon-tap-02" />
                          </span>
                        </Button>
                      </ButtonGroup>
                    </Col>
                  </Row>
                </CardHeader>
                <CardBody>
                  <LeafletMap style={leafStyle}
                    center={[34.052235, -118.243683]}
                    zoom={vx.zoomLevel}
                    onzoom={handleZoom}
                    ref={(ref) => { map = ref; }}
                  >
                  <TileLayer
                      url='http://{s}.tile.osm.org/{z}/{x}/{y}.png'
                  />
                  {setPositionsView(vx.mapPositions)}
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