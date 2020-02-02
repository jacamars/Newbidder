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
import CampaignEditor from './editors/CampaignEditor.jsx'

var undef;

 const Campaigns = (props) => {

  const [count, setCount] = useState(0);
  const [campaign, setCampaign] = useState(null);
  const vx = useViewContext();

  const redraw = () => {
      setCount(count+1);
  }

  const setInstances = () => {

  };

  const makeNew = async() => {
    if (campaign !== null)
        return;

    var camp = await vx.getNewCampaign("My New Campaign");
    setCampaign(camp);
  }

  const getCampaignsView = () => {

    return(
        <div>
        </div>
    );
  }

  const update = (e) => {
      setCampaign(null);
      if (e !== null) {
        // update database
        setCampaign(null);
      }
      redraw();
  }

  return (
    <div className="content">
    { !vx.isLoggedIn && <LoginModal callback={setInstances} />}
        <Row>
            <Col xs="12">
            { campaign == null && <>
            <Button size="sm" className="btn-fill" color="success" onClick={redraw}>Refresh</Button>
            <Button size="sm" className="btn-fill" color="danger" onClick={makeNew}>New</Button>
                <Card className="card-chart">
                    <CardHeader>
                        <Row>
                            <CardTitle tag="h2">Campaigns in DB</CardTitle>
                        </Row>
                    </CardHeader>
                    <CardBody>
                      <Table key={"bidders-table-"+count} size="sm">
                        <thead>
                          <tr>
                            <th>#</th>
                            <th className="text-center">Name</th>
                            <th className="text-right">SQL-ID</th>
                            <th className="text-right">Runnable</th>
                            <th className="text-right">Is Running</th>
                            <th className="text-right"></th>
                            <th className="text-right"></th>
                          </tr>
                      </thead>
                      <tbody>
                        { getCampaignsView() }
                      </tbody>
                    </Table>
                  </CardBody>
                </Card>
                </>
                }
                { campaign !== null &&
                    <CampaignEditor key={"ce-"+count} campaign={campaign} callback={update} />
                }
            </Col>
        </Row>
    </div>
  );
 }

 export default Campaigns;