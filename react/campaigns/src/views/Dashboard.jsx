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

 const Dashboard = (props) => {

  const vx = useViewContext();
  const [count, setCount] = useState(0);
  useEffect(() => {
    if (vx.loggedIn)
      refresh();
  }, []);

  const refresh = async() => {
    await vx.listCampaigns();
    await vx.getBidders();
    await vx.getAccounting();
    setCount(count+1);
  }

  const setInstances = () => {
    refresh();
  };

  const setBiddersView = (rows) => {
    console.log("ROWS: " + JSON.stringify(rows,null,2));
        if (rows === undef)
          return null;
        return(
          rows.map((row, index) => (
            <tr key={'bidders-' + index}>
              <td key={'bidders-index-' + index}>{index+1}</td>
              <td key={'bidders-address-' + index} className="text-left">{row.from}</td>
              <td key={'bidders-leader-' + index} className="text-right">{row.leader.toString()}</td>
              <td key={'bidders-stopped-' + index} className="text-right">{row.stopped.toString()}</td>
              <td key={'bidders-error-' + index} className="text-right">{row.request.toString()}</td>
              <td key={'bidders-bid-' + index} className="text-right">{row.bid.toString()}</td>
              <td key={'bidders-nobid-' + index} className="text-right">{row.nobid.toString()}</td>
              <td key={'bidders-qps-' + index} className="text-right">{row.qps.toString()}</td>
              <td key={'bidders-avgx-' + index} className="text-right">{row.avgx[row.avgx.length-1].toString()}</td>
            </tr>))
        )
      }

    const setCampaignsView = (rows,acc) => {
        //console.log("ACC: " + JSON.stringify(acc,null,2));
        //console.log("ROWS: " + JSON.stringify(rows,null,2));
        return(
          rows.map((row, index) => (
            <tr key={'camps-' + index}>
              <td key={'camps-index-' + index}>{index+1}</td>
              <td key={'camps-name-' + index} className="text-left">{row}</td> 
              <td key={'camps-bids-' + index} className="text-right">{vx.getCount(acc,row,".bids")}</td>
              <td key={'camps-wins-' + index} className="text-right">{vx.getCount(acc,row,".wins")}</td>
              <td key={'bidders-pixels-' + index} className="text-right">{vx.getCount(acc,row,".pixels")}</td>
              <td key={'bidders-clicks-' + index} className="text-right">{vx.getCount(acc,row,".clicks")}</td>
              <td key={'bidders-adspend-' + index} className="text-right">{vx.getCount(acc,row,".total")}</td>
            </tr>))
        );
      }

  return (
    <div className="content">
    { !vx.isLoggedIn && <LoginModal callback={setInstances} />}
        <Row>
            <Col xs="12">
            <Button size="sm" color="error" onClick={refresh}>Refresh</Button>
                <Card className="card-chart">
                    <CardHeader>
                        <Row>
                            <CardTitle tag="h2">Instances</CardTitle>
                        </Row>
                    </CardHeader>
                    <CardBody>
                      <Table key={"bidders-table-"+count} size="sm">
                        <thead>
                          <tr>
                            <th>#</th>
                            <th className="text-center">Instance</th>
                            <th className="text-right">Leader</th>
                            <th className="text-right">Stopped</th>
                            <th className="text-right">Requests</th>
                            <th className="text-right">Bid</th>
                            <th className="text-right">Nobid</th>
                            <th className="text-right">QPS</th>
                            <th className="text-right">AVGX</th>
                          </tr>
                      </thead>
                      <tbody>
                        { setBiddersView(vx.bidders) }
                      </tbody>
                    </Table>
                  </CardBody>
                </Card>
                <Card className="card-chart">
                    <CardHeader>
                        <Row>
                            <CardTitle tag="h2">Running Campaigns</CardTitle>
                        </Row>
                    </CardHeader>
                    <CardBody>
                      <Table key={"camps-table-"+count}size="sm">
                        <thead>
                          <tr>
                            <th>#</th>
                            <th className="text-center">Campaign</th>
                            <th className="text-right">Bids</th>
                            <th className="text-right">Wins</th>
                            <th className="text-right">Pixels</th>
                            <th className="text-right">Clicks</th>
                            <th className="text-right">Spend</th>
                          </tr>
                      </thead>
                      <tbody key={"div-"+count}>
                        { setCampaignsView(vx.runningCampaigns,vx.accounting) }
                      </tbody>
                    </Table>
                  </CardBody>
                </Card>
            </Col>
        </Row>
    </div>
  );
 }

 export default Dashboard;