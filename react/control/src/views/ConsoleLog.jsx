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
import React, { useState, useEffect } from "react";

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

var xhr;
var undef;

 const ConsoleLog = (props) => {

  const vx = useViewContext();
  const [count, setCount] = useState(1);
  
  /*useEffect(() => {
    return () => {console.log("Logger UNMOUNTED"); vx.setLogsuspended(true); }
  }, []);

  useEffect(() => {
    console.log("Console MOUNTED");
    vx.changeLogsuspened(false);
  }, [logview]);*/

  const RED = {
    backgroundColor: 'red'
  }
  const GREY = {
    backgroundColor: 'grey'
  }
  const YELLOW = {
    backgroundColor: 'goldenrod'
  }

  const getStyle = (value) => {
    if (value === "INFO")
      return GREY;
    if (value === "WARN")
      return YELLOW;
    return RED;
  }

  const setConsoleView = (rows) => {
    if (rows === undef)
      return null;
    return(
     rows.map((row, i) => (<tr key={'"console-pos-' + i + "'"} style={getStyle(row.sev)}>
         <td>
           {row.index}
         </td>
         <td>
           {row.time}
         </td>
         <td>
           {row.source} : {row.field}
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

  const fromCallback = () => {
    setCount(count+1);
  }

  const clear = () => {
    vx.clearLogdata();
    fromCallback();
  }

  if (vx.consoleLogspec === '') {
    console.log("WE HAVE SET THE CONSOLE LOG")
    vx.loggerCallback("localhost:8080");
  }

    return (
      <>
        <div className="content">
          <Row>
            <Col md="12">
              <Card>
                <CardHeader>
                  <CardTitle tag="h4">RTB4FREE Console Log {vx.logcount}</CardTitle>
                  <Button size="sm" color="info" onClick={fromCallback}>Refresh</Button>
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
                      {setConsoleView(vx.logdata)}
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
