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
  Spinner,
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
    await vx.listCampaigns();
    await vx.getDbCampaigns();
    redraw();
  }

  useEffect(() => {
    if (vx.loggedIn)
      loadDataOnce();
  }, []);

  const [count, setCount] = useState(0);
  const [campaign, setCampaign] = useState(null);
  const [browse, setBrowse] = useState(false);
  const vx = useViewContext();

  const redraw = () => {
      setCount(count+1);
  }

  const setInstances = () => {
    loadDataOnce();
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


  const exportCampaign = async (id) => {
    var campaign  = await vx.getDbCampaign(id);
    var rules = campaign.rules;
    var banners = campaign.banners;
    var videos = campaign.videos;
    var audios = campaign.audios;
    var natives = campaign.natives;

    var exp = {};
    exp.campaign = campaign;
    exp.rules = {};
    exp.banners = {};
    exp.videos = {};
    exp.audios = {};
    exp.natives = {};

    for (var i = 0; i < rules.length; i++) {
      var rule = rules[i];
      exp.rules[rule.id.toString()] = await vx.getRule(rule.id);
    }

    for (var i = 0; i < banners.length; i++) {
      var banner = banners[i];
      exp.banners[banner.toString()] = await vx.getCreative(banner,"banner");
    }

    for (var i = 0; i < audios.length; i++) {
      var audio = audios[i];
      exp.audios[audio.toString()] = await vx.getCreative(audio,"audio");
    }

    for (var i = 0; i < videos.length; i++) {
      var video = videos[i];
      exp.videos[video.toString()] = await vx.getCreative(video,"video");
    }

    for (var i = 0; i < natives.length; i++) {
      var native = natives[i];
      exp.natives[native.toString()] = await vx.getCreative(native,"native");
    }

    localStorage.setItem("import",JSON.stringify(exp,null,2));
    navigator.clipboard.writeText(JSON.stringify(exp,null,2));
    alert("Copied to Local Storage!");
    refresh();
  }

  const delta = async (index) => {
    var row = vx.campaigns[index];
    var x = await vx.getDbCampaign(row.id);
    if (x === undef) 
      return;
    
    if (row.status === "runnable") {
        x.status = "offline";
    } else {
        x.status = "runnable";
    }
    vx.addNewCampaign(JSON.stringify(x));

    setTimeout(refresh,3000);
    redraw();
  }


  const importFile = () => {
    setBrowse(true);
  }

  const completeImport = async (mode,data) => {
    if (mode) {     // Ok, let's fill it all in.
      var v = "";
      try {
        v = JSON.parse(data);
        var campaign = v.campaign;
        var rules = campaign.rules;
        var banners = campaign.banners;
        var videos = campaign.videos;
        var audios = campaign.audios;
        var natives = campaign.natives;
        
        for (var i=0;i<banners.length;i++) {
          var id = banners[i];
          var banner = v.banners[id.toString()];
          banner.id = 0;
          var rc = await vx.addNewCreative(banner,"banners");
          alert(rc);
        }
      } catch (e) {

      } 
    }
    setBrowse(false);
  }


  const getCampaignsView = () => {

   var campaigns = vx.campaigns;
   campaigns.sort(function(a, b) {
    a = a.customer_id + a.name;
    b = b.customer_id + b.name;
    return (a > b) - (a < b);
   });
   return(
      campaigns.map((row, index) => (
        <tr key={'campaignsview-' + index} style={getStyle(row.status,row.name)}>
          <td>{index}</td>
          <td key={'campaignsview-name-' + index} className="text-left">{row.name}</td>

          {vx.user.sub_id === 'superuser' &&
            <td key={'campaignsview-cust-' + index} className="text-right">{row.customer_id}</td>
          }

          <td key={'campaignsview-id-' + index} className="text-right">{row.id}</td>
          <td key={'campaignsview-status-' + index} className="text-center">
            <Button color="link" size="sm" onClick={()=>delta(index)}>{row.status}</Button>
          </td>
          <td key={'campaignsview-running-'+ index} className="text-right">{""+checkRunning(row.name)}</td>
          <td key={'campaignsview-edit-'+ index} className="text-center">
            <Button color="success" size="sm" onClick={()=>editCampaign(row.id,true)}>View</Button>{' '}
            <Button color="warning" size="sm" onClick={()=>editCampaign(row.id,false)}>Edit</Button>{' '}
            <Button color="info" size="sm" onClick={()=>exportCampaign(row.id)}>Export</Button>{' '}
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

  //             <Button size="sm" className="btn-fill" color="danger" onClick={importClipboard}>Import</Button>

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
            <Button size="sm" className="btn-fill" color="danger" onClick={importFile}>Import</Button>
            { browse &&
                <DecisionModal title="Import Foreign Campaign" 
                     message="Paste your definitions here" 
                     name="IMPORT"
                     input={true}
                     inputValue=""
                     callback={completeImport} />}

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
                            {vx.user.sub_id === 'superuser' &&
                                <th className="text-right">Customer</th>
                            }
                            <th className="text-right">SQL-ID</th>
                            <th className="text-center">Status</th>
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