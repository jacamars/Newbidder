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

  const refresh = async() => {
      await vx.getDbCampaigns();
  }

  const makeNew = async() => {
    if (campaign !== null)
        return;

    var camp = await vx.getNewCampaign("My New Campaign");
    setCampaign(camp);
  }

  const editCampaign = async (id) => {
    var x = await vx.getDbCampaign(id);
    setCampaign(x);
  }

  const viewCampaign = async (id) => {
    var x = await vx.getDbCampaign(id);
    x.readOnly = true;
    setCampaign(x);
  }

  const deleteCampaign = async (id) => {
    for(var i=0;i<vx.campaigns.length;i++) {
      var x = vx.campaigns[i];
      if (x.id === id) {
        await vx.deleteCampaign(id);
        await vx.getDbCampaigns();
        redraw();
        return;
      }
    }
  }

  const checkRunning = (name) => {
    for (var i=0;i<vx.runningCampaigns;i++) {
      var x = vx.runningCampaigns[i];
      if (x.name === name)
        return true;
    }
    return false;
  }

  const getCampaignsView = () => {

    console.log("GetCampaigsView, rows = " + vx.campaigns.length);

   return(
      vx.campaigns.map((row, index) => (
        <tr key={'campaignsview-' + row}>
          <td>{index}</td>
          <td key={'campaignsview-name-' + index} className="text-left">{row.name}</td>
          <td key={'campaignsview-id-' + index} className="text-right">{row.id}</td>
          <td key={'campaignsview-status-' + index} className="text-right">{row.status}</td>
          <td key={'campaignsview-running-'+ index} className="text-right">{""+checkRunning(row.name)}</td>
          <td key={'campaignsview-edit-'+ index} className="text-center">
            <Button color="success" size="sm" onClick={()=>viewCampaign(row.id)}>View</Button>
             &nbsp;
            <Button color="warning" size="sm" onClick={()=>editCampaign(row.id)}>Edit</Button>
            &nbsp;
            <Button color="danger" size="sm" onClick={()=>deleteCampaign(row.id)}>Delete</Button></td>
        </tr>))
    ); 
  }

  const update = (e) => {
      setCampaign(null);
      if (e !== null) {
        //alert("NEW CAMPAIGN: " + JSON.stringify(e,null,2));
        vx.addNewCampaign(JSON.stringify(e));
        vx.getDbCampaigns();
        setCampaign(null);
      }
      redraw();
  }

  return (
    <div className="content">
    { !vx.isLoggedIn && <LoginModal callback={setInstances} />}
        <Row id={"running-"+count}>
            <Col xs="12">
            { campaign == null && <>
            <Button size="sm" className="btn-fill" color="success" onClick={refresh}>Refresh</Button>
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
                            <th className="text-center">Actions</th>
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