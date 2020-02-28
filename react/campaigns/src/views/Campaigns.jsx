import React, { useState, useEffect } from "react";

// reactstrap components
import {
  Alert,
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
import DecisionModal from "../DecisionModal";
import LoginModal from '../LoginModal'
import CampaignEditor from './editors/CampaignEditor.jsx'

var undef;

 const Campaigns = (props) => {

  // Called when page first loads
  const loadDataOnce = async() => {
    await vx.getDbCampaigns();
    await vx.listCampaigns();
    redraw();
  }

  const [count, setCount] = useState(0);
  const [campaign, setCampaign] = useState(null);
  const vx = useViewContext();

  const redraw = () => {
      setCount(count+1);
  }

  const setInstances = () => {

  };

  const refresh = async() => {
      loadDataOnce();
      redraw();
  }

  const makeNew = async() => {
    if (campaign !== null)
        return;

    var camp = await vx.getNewCampaign("My New Campaign");
    var date = new Date();
    camp.activate_time = date.getTime();
    date.setDate(date.getDate() + 30);
    camp.expire_time = date.getTime();
    camp.daypartSchedule = undef;
    setCampaign(camp);
  }

  const editCampaign = async (id, ro) => {
    var x = await vx.getDbCampaign(id);
    if (x === undef) {
      alert("Database error on campaign id: " + id);
      return;
    }

    if (ro) {
      x.readOnly = true;
    }

    if (x.day_parting_utc === undef)
      x.daypartSchedule = undef;
    else
      x.daypartSchedule = JSON.parse(x.day_parting_utc);

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

  const report = async (id) => {
    var reasons = await vx.getReasons(id);
    alert(reasons);
  }

  const checkRunning = (name) => {
    for (var i=0;i<vx.runningCampaigns.length;i++) {
      var x = vx.runningCampaigns[i];
      if (x === name)
        return true;
    }
    return false;
  }

  const startCampaign = (id) => {

  }

  const RED = {
    backgroundColor: 'red'
  }
  const GREEN = {
    backgroundColor: 'green'
  }
  const GRAY = {
    backgroundColor: 'gray'
  }
  const YELLOW = {
    backgroundColor: 'goldenrod'
  }

  const getStyle = (status,name) => {
    var running = checkRunning(name);
    if (status !== 'runnable') {
      if (running)
        return YELLOW;
      return GRAY
    }
    
    if (checkRunning(name))
      return GREEN;
    return RED;
  }


  const getCampaignsView = () => {

    console.log("GetCampaigsView, rows = " + vx.campaigns.length);

   return(
      vx.campaigns.map((row, index) => (
        <tr key={'campaignsview-' + index} style={getStyle(row.status,row.name)}>
          <td>{index}</td>
          <td key={'campaignsview-name-' + index} className="text-left">{row.name}</td>
          <td key={'campaignsview-id-' + index} className="text-right">{row.id}</td>
          <td key={'campaignsview-status-' + index} className="text-right">{row.status}</td>
          <td key={'campaignsview-running-'+ index} className="text-right">{""+checkRunning(row.name)}</td>
          <td key={'campaignsview-edit-'+ index} className="text-center">
            <Button color="success" size="sm" onClick={()=>editCampaign(row.id,true)}>View</Button>{' '}
            <Button color="warning" size="sm" onClick={()=>editCampaign(row.id,false)}>Edit</Button>{' '}
            {!checkRunning(row.name)
            ? <Button color="info" size='sm' onClick={()=>report(row.id)}>Report</Button>
            : <>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;</>}
            &nbsp; &nbsp; &nbsp;<Button color="danger" size="sm" onClick={()=>showModal(row.id)}>Delete</Button>
            </td>
        </tr>))
    ); 
  }

  // deleteCampaign(row.id)

  const update = (e) => {
      setCampaign(null);
      if (e !== null) {
        //alert("NEW CAMPAIGN: " + JSON.stringify(e,null,2));
        vx.addNewCampaign(JSON.stringify(e));
        setCampaign(null);

        setTimeout(refresh,3000);
      }
  }

  ////////////////////////////// DELETE CAMPAIGN ///////////////////////////////////
  const [modal, setModal] = useState(false);
  const [id, setId] = useState(0);
  const modalCallback = (doit) => {
    if (doit) {
      deleteCampaign(id)
    }
    setModal(!modal);

  }
  const showModal = (x) => {
    setId(x);
    setModal(true);
  }
  /////////////////////////////////////////////////////////////////////////////////////

  return (
    <div className="content">
    { modal &&
      <DecisionModal title="Really delete campaign?" 
                     message="Only the db admin can undo this if you delete it!!!" 
                     name="DELETE"
                     callback={modalCallback} />}
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