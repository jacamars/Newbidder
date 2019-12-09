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
import React,  { useState, useEffect } from "react";
// nodejs library that concatenates classes
import classNames from "classnames";
// react plugin used to create charts
import { Line, Bar } from "react-chartjs-2";
// networking
import axios from 'axios';
import http from 'http';

// reactstrap components
import {
  Button,
  ButtonGroup,
  Card,
  CardHeader,
  CardBody,
  CardTitle,
  DropdownToggle,
  DropdownMenu,
  DropdownItem,
  UncontrolledDropdown,
  Label,
  FormGroup,
  Input,
  Table,
  Row,
  Col,
  UncontrolledTooltip
} from "reactstrap";

// core components
import {
  chartExample1,
  chartExample2,
  chartExample3,
  chartExample4
} from "variables/charts.jsx";

import { useViewContext } from "../ViewContext";
import { memberExpression } from "@babel/types";

const httpAgent = new http.Agent({ keepAlive: true });
const axiosInstance = axios.create({
  httpAgent,  // httpAgent: httpAgent -> for non es6 syntax
});

var undef;

const Dashboard = (props) => {

  const vx = useViewContext();

  const [count, setCount] = useState(1);
  const [instanceNames, setInstanceNames] = useState([]);

  //const [selectedInstance, setSelectedInstance] = useState('');


  const [leader,setLeader] = useState('');
  const [snapShotView, setSnapShotView] = useState('');
  const [campaigns, setCampaigns] = useState([]);
  
  useEffect(() => {
    // Update the document title using the browser API
    doGetStatusCmd();
  },[]);

  //const setBgChartData = name => {
  //  setBigChartData(name);
  //};

  const setInstances = (list) => {
    var output = '';
    var leader = '';
    var selected = vx.selectedHost;
    if (list.length===1) {
      output  = <option>{list[0].name + '*'}</option>;
      vx.setSelectedHost(list[0].name);
      setLeader(list[0].name);
    } else {
      for (var i=0;i<list.length;i++) {
        if (setLeader(list[i].values.leader)) {
          setLeader(list[i].name);
          leader = '*';
        } else
          leader = '';
        if (selected !== '') {
          if (selected === list[i].name)
            output += <option selected>{list[i].name + leader}</option>;
          else
            output += <option>{list[i].name + leader}</option>;
        } else {
          if (i === 0) {
            output += <option selected>All Instances</option>;
          }
          output += <option>{list[i].name + leader}</option>;
          vx.setSelectedInstance("All Instances");
        }
      }
    }
    setInstanceNames(output);
  }

  const setCampaignsView = (rows) => {
    setCampaigns(setCampaignsViewInternal(rows));
  }

  const setCampaignsViewInternal = (rows) => {
    if (rows ===undef)
      return null;

    return(
      rows.map((row, i) => (<tr key={'"campaign-view-' + i + '"'}>
          <td>{row}</td>
        </tr>))
    )
  }

  const doGetStatusCmd = async () => {
    if (!vx.loggedIn)
      return;
      
    try {
      var list;
      if (vx.members.length === 0)
        list = vx.getMembers();
      else 
        list = vx.members;
      if (list === undef)
        return;

      var x = list[0];
      if (x ===undef)     // can happen if the network is not connected yet
        return;

      setInstances(list);
      setSnapShotView(getSnapShotView(list));
      setCampaignsView(list[0].values.campaigns);
      redraw();
    } catch (e) {
      alert (e);
    }
  }

  const getSnapShotView = (rows) => {
    console.log("GET SNAPSHOT VIEW: " + rows);
    if (rows === undef)
      return null;
    return(
      rows.map((row, index) => (
        <tr key={'snaphotview-' + row}>
          <td>{row.name}</td>
          <td key={'snaphotview-request-' + index} className="text-right">{row.values.request}</td>
          <td key={'snaphotview-bid-' + index} className="text-right">{row.values.bid}</td>
          <td key={'snaphotview-win-' + index} className="text-right">{row.values.win}</td>
          <td key={'snaphotview-pixels-' + index} className="text-right">{row.values.pixels}</td>
          <td key={'snaphotview-clicks-' + index} className="text-right">{row.values.clicks}</td>
        </tr>))
    )
  }

  const redraw = () => {
    setCount(count + 1);
  }

    return (
        <div className="content">
          <Row>
            <Col xs="12">
              <Card className="card-chart">
                <CardHeader>
                  <Row>
                    <Col className="text-left" sm="6">
                      <h5 className="card-category">Total Events</h5>
                      <select width='100%'>
                          {instanceNames}
                      </select>
                      <Button size="sm" color="info" onClick={doGetStatusCmd}>Refresh</Button>
                      <CardTitle tag="h2">Performance</CardTitle>
                    </Col>
                    <Col sm="6">
                      <ButtonGroup
                        className="btn-group-toggle float-right"
                        data-toggle="buttons"
                      >
                        <Button
                          tag="label"
                          className={classNames("btn-simple", {
                            active: vx.bigChartData === "data1"
                          })}
                          color="info"
                          id="0"
                          size="sm"
                          onClick={() => vx.setBgChartData("data1")}
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
                            active: vx.bigChartData === "data2"
                          })}
                          onClick={() => vx.setBgChartData("data2")}
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
                            active: vx.bigChartData === "data3"
                          })}
                          onClick={() => vx.setBgChartData("data3")}
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
                            active: vx.bigChartData === "data4"
                          })}
                          onClick={() => vx.setBgChartData("data4")}
                        >
                          <input
                            className="d-none"
                            name="options"
                            type="radio"
                          />
                          <span className="d-none d-sm-block d-md-block d-lg-block d-xl-block">
                            Pixels
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
                  <div className="chart-area">
                    <Line
                      data={chartExample1[vx.bigChartData]}
                      options={chartExample1.options}
                    />
                  </div>
                </CardBody>
              </Card>
            </Col>
          </Row>
          <Row>
            <Col lg="4">
              <Card className="card-chart">
                <CardHeader>
                  <h5 className="card-category">X-Time</h5>
                  <CardTitle tag="h3">
                    <i className="tim-icons icon-bell-55 text-info" />{" "}
                    763,215
                  </CardTitle>
                </CardHeader>
                <CardBody>
                  <div className="chart-area">
                    <Line
                      data={chartExample2.data}
                      options={chartExample2.options}
                    />
                  </div>
                </CardBody>
              </Card>
            </Col>
            <Col lg="4">
              <Card className="card-chart">
                <CardHeader>
                  <h5 className="card-category">CPU</h5>
                  <CardTitle tag="h3">
                    <i className="tim-icons icon-delivery-fast text-primary" />{" "}
                    3,500€
                  </CardTitle>
                </CardHeader>
                <CardBody>
                  <div className="chart-area">
                    <Bar
                      data={chartExample3.data}
                      options={chartExample3.options}
                    />
                  </div>
                </CardBody>
              </Card>
            </Col>
            <Col lg="4">
              <Card className="card-chart">
                <CardHeader>
                  <h5 className="card-category">Memory</h5>
                  <CardTitle tag="h3">
                    <i className="tim-icons icon-send text-success" /> 12,100K
                  </CardTitle>
                </CardHeader>
                <CardBody>
                  <div className="chart-area">
                    <Line
                      data={chartExample4.data}
                      options={chartExample4.options}
                    />
                  </div>
                </CardBody>
              </Card>
            </Col>
          </Row>
          <Row>
            <Col lg="6" md="12">
              <Card className="card-tasks">
                <CardHeader>
                  <h6 className="title d-inline">Campaigns</h6>
                  <p className="card-category d-inline"> today</p>
                  <UncontrolledDropdown>
                    <DropdownToggle
                      caret
                      className="btn-icon"
                      color="link"
                      data-toggle="dropdown"
                      type="button"
                    >
                      <i className="tim-icons icon-settings-gear-63" />
                    </DropdownToggle>
                    <DropdownMenu aria-labelledby="dropdownMenuLink" right>
                      <DropdownItem
                        href="#pablo"
                        onClick={e => e.preventDefault()}
                      >
                        Action
                      </DropdownItem>
                      <DropdownItem
                        href="#pablo"
                        onClick={e => e.preventDefault()}
                      >
                        Another action
                      </DropdownItem>
                      <DropdownItem
                        href="#pablo"
                        onClick={e => e.preventDefault()}
                      >
                        Something else
                      </DropdownItem>
                    </DropdownMenu>
                  </UncontrolledDropdown>
                </CardHeader>
                <CardBody>
                  <div className="table-full-width table-responsive">
                    <Table>
                    <thead className="text-primary">
                      <tr>
                        <th className="text-center">Campaign</th>
                        <th className="text-right">Bids</th>
                        <th className="text-right">Wins</th>
                        <th className="text-right">Pixels</th>
                        <th className="text-right">Clicks</th>
                        <th className="text-right">Spend</th>
                      </tr>
                    </thead>
                      <tbody>
                        {campaigns}
                      </tbody>
                    </Table>
                  </div>
                </CardBody>
              </Card>
            </Col>
            <Col lg="6" md="12">
              <Card>
                <CardHeader>
                  <CardTitle tag="h4">Snapshot Counts<Button size="sm" color="info" onClick={doGetStatusCmd}>Refresh</Button></CardTitle>
                </CardHeader>
                <CardBody>
                  <Table className="tablesorter" responsive>
                    <thead className="text-primary">
                      <tr>
                        <th className="text-center">Instance</th>
                        <th className="text-right">Requests</th>
                        <th className="text-right">Bids</th>
                        <th className="text-right">Wins</th>
                        <th className="text-right">Pixels</th>
                        <th className="text-right">Clicks</th>
                      </tr>
                    </thead>
                    <tbody>{snapShotView}</tbody>
                  </Table>
                </CardBody>
              </Card>
            </Col>
          </Row>
        </div>
    );
}

export default Dashboard;
