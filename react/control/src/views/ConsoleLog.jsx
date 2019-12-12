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

// reactstrap components
import {
  Badge,
  Button,
  ButtonGroup,
  ButtonToolbar,
  Card,
  CardHeader,
  CardBody,
  CardTitle,
  Table,
  Row,
  Col
} from "reactstrap";
import { useViewContext } from "../ViewContext";
import LoginModal from '../LoginModal'

var undef;

 const ConsoleLog = (props) => {

  const vx = useViewContext();
  const [rSelected, setRSelected] = useState(3);     // buttons
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

  const getSev = (key) => {
    if (key === "ERROR")
      return 1;
    if (key === "WARN")
      return 2;
    if (key === "INFO")
      return 3;
    return 4;
  }

  const setConsoleView = (rows) => {
    if (rows === undef)
      return null;
    return(
     rows.filter(item => getSev(item.sev) <= rSelected).map((row, i) => (<tr key={'"console-pos-' + i + "'"} style={getStyle(row.sev)}>
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
  }

  const setInstances = async(list,selectedHost) => {
    vx.loggerCallback(selectedHost,fromCallback);
  }

    return (
      <>
        <div className="content">
        { !vx.isLoggedIn && <LoginModal callback={setInstances} />}
          <Row>
            <Col md="12">
              <Card>
                <CardHeader>
                  <CardTitle tag="h4">
                    RTB4FREE Console Log <Badge color="primary">{vx.logcount}</Badge>
                  </CardTitle>
  
                  <Button size="sm" color="info" onClick={fromCallback}>Refresh</Button>
                  <Button size="sm" color="warn" onClick={clear}>Clear</Button>
                  <ButtonToolbar>
                    <ButtonGroup>
                      <Button color="danger"  onClick={() => setRSelected(1)} active={rSelected === 1}>ERROR</Button>
                      <Button color="warning" onClick={() => setRSelected(2)} active={rSelected === 2}>WARN</Button>
                      <Button color="secondary" onClick={() => setRSelected(3)} active={rSelected === 3}>INFO</Button>
                      <Button color="success"  onClick={() => setRSelected(4)} active={rSelected === 4}>DEBUG</Button>
                    </ButtonGroup>
                  </ButtonToolbar>

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
