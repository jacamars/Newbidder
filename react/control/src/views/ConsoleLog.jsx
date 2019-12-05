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
import React from "react";

// reactstrap components
import {
  Card,
  CardHeader,
  CardBody,
  CardTitle,
  Table,
  Row,
  Col
} from "reactstrap";

 const ConsoleLog = (props) => {

  const RED = {
    backgroundColor: 'red'
  }
  const GREY = {
    backgroundColor: 'grey'
  }
  const YELLOW = {
    backgroundColor: 'goldenrod'
  }

    return (
      <>
        <div className="content">
          <Row>
            <Col md="12">
              <Card>
                <CardHeader>
                  <CardTitle tag="h4">RTB4FREE Console Log</CardTitle>
                </CardHeader>
                <CardBody>
                  <Table className="tablesorter" responsive>
                    <thead className="text-primary">
                      <tr>
                        <th>Time</th>
                        <th>Level</th>
                        <th>Component</th>
                        <th>Message</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr style={RED}>
                        <td>2019-12-04 16:18:34 INFO</td>
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
                        <td>2019-12-04 16:18:34 INFO</td>
                        <td>WARN</td>
                        <td>Crosstalk:169</td>
                        <td className="text-left">CROSSTALK budgeting has started</td>
                      </tr>
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
