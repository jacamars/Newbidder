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

                     <tr style={RED}>
                        <td>2019-12-04 16:18:34</td>
                        <td>WARN</td>
                        <td>Crosstalk:169</td>
                        <td className="text-left">CROSSTALK budgeting has started</td>
                      </tr>
                      <tr style={YELLOW}>
                        <td>2019-12-04 16:26:34</td>
                        <td>INFO</td>
                        <td>RTBServer:845</td>
                        <td className="text-left">Heartbeat leader: true, total-errors=0, openfiles=448, cpu=13.5%, mem=159M (5.306%), freedsk=18.123%, threads=156, low-on-threads= false, qps=0.00, avgBidTime=0.00ms, avgNoBidTime=0.00ms, total=34, requests=13, bids=13, nobids=0, fraud=0, cidrblocked=0, wins=6, pixels=5, clicks=0, exchanges=[wins=0, qps=0.0, bids=0, name=nexage, requests=0, errors=0], stopped=false, campaigns=9
</td>
                      </tr>
                      <tr style={GREY}>
                        <td>2019-12-04 16:18:34</td>
                        <td>WARN</td>
                        <td>Crosstalk:169</td>
                        <td className="text-left">CROSSTALK budgeting has started</td>
                      </tr>
*/
import React, { useState } from "react";

// reactstrap components
import {
  Button,
  Card,
  CardHeader,
  CardBody,
  CardTitle,
  Table,
  Row,
  Col
} from "reactstrap";
import { useViewContext } from "../ViewContext";

 const ConsoleLog = (props) => {

  const vx = useViewContext();
  const [logview, setLogview] = useState('init');

  const RED = {
    backgroundColor: 'red'
  }
  const GREY = {
    backgroundColor: 'grey'
  }
  const YELLOW = {
    backgroundColor: 'goldenrod'
  }

  const redraw = () => {
    setLogview(setConsoleView(vx.logdata));
  }

  const  loggerCallback = (spec,logname) => {
    var previous_response_length = 0;
    var xhr = new XMLHttpRequest()
    xhr.open("GET", "http://" + spec + "/subscribe?topic=" + logname, true);
    xhr.onreadystatechange = checkData;
    xhr.send(null);
    
    function checkData() {
      if (xhr.readyState === 3) {
        var response = xhr.responseText;
        var chunk = response.slice(previous_response_length);
        console.log("GOT SOME LOG CHUNK DATA: " + chunk);
        var i = chunk.indexOf("{");
        if (i < 0)
          return;
        if (chunk.trim().length === 0)
          return;
        chunk = chunk.substring(i);
        previous_response_length = response.length;
        
        console.log("GOT LOG DATA: " + chunk);
        var lines = chunk.split("\n");
        var rows = []
        for (var j = 0; j < lines.length; j++) {
          var line = lines[j];
          line = line.trim();
          if (line.length > 0) {
            var y = JSON.parse(line);
            rows.push(y);
          }
        }
        console.log("BEFORE: " + vx.logdata.length);
        vx.addLogdata(rows)
        console.log("AFTER: " + vx.logdata.length)
        setLogview(setConsoleView(vx.logdata));
      }
    }
    ;
  }

  const getStyle = (value) => {
    if (value === "INFO")
      return GREY;
    if (value === "WARN")
      return YELLOW;
    return RED;
  }

  const setConsoleView = (rows) => {
    var index = rows.length;
    return(
      rows.map((row, i) => (<tr key={'"console-pos-' + i + "'"} style={getStyle(row.sev)}>
         <td>
           {index--}
         </td>
         <td>
           time?
         </td>
         <td>
           {row.source}
         </td>
         <td>
           {row.sev}
         </td>
         <td>
           {row.instance}
         </td>
         <td>
           {row.message}
         </td>
       </tr>))
    )
  }


  const clear = () => {
    vx.clearLogdata();
    setLogview(setConsoleView([]));
  }

  if (vx.consoleLogspec === '') {
    vx.changeConsoleLogspec("localhost:7379");
    console.log("WE HAVE SET THE CONSOLE LOG")
    loggerCallback("localhost:7379","logs");
  }

  if (logview === 'init') {
    setLogview(setConsoleView(vx.logdata));
  }

    return (
      <>
        <div className="content">
          <Row>
            <Col md="12">
              <Card>
                <CardHeader>
                  <CardTitle tag="h4">RTB4FREE Console Log</CardTitle>
                  <Button size="sm" color="info" onClick={redraw}>Refresh</Button>
                  <Button size="sm" color="warn" onClick={clear}>Clear</Button>
                </CardHeader>
                <CardBody>
                  <Table className="tablesorter" responsive>
                    <thead className="text-primary">
                      <tr>
                        <th>Index</th>
                        <th>Time</th>
                        <th>Source</th>
                        <th>Level</th>
                        <th>Instance</th>
                        <th>Message</th>
                      </tr>
                    </thead>
                    <tbody>
                      {logview}
                    </tbody>
                  </Table>
                </CardBody>
              </Card>
            </Col>
          </Row>
        </div>
      </>
    );
}

export default ConsoleLog;
