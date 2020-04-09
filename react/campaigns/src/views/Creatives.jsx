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
import LoginModal from '../LoginModal'
import CreativeEditor from './editors/CreativeEditor'
import DecisionModal from '../DecisionModal';

var undef;

 const Creatives = (props) => {

  // This should be called when the page first loads
  const loadDataOnce = async() => {
    await vx.listCreatives();
  }

  const vx = useViewContext();
  const [creative, setCreative] = useState(null);
  const [count, setCount] = useState(0);
  useEffect(() => {
    if (vx.loggedIn)
      loadDataOnce();
  }, []);

  const redraw = () => {
      setCount(count+1);
  }

  const setDates = (c) => {
    var date = new Date();
    c.interval_start = date.getTime();
    c.interval_end = date.setDate(date.getDate() + 30);
    return c;
  }

  const makeNewBanner = async() => {
    if (creative !== null)
    return;

    var c = await vx.getNewCreative("banner");
    c = setDates(c);
    c.id = 0;
    c.isVideo = false;
    c.isAudio = false;
    c.isNative = false;
    c.isBanner = true;
    c.type = "banner";
    c.price = c.bid_ecpm;
    c.siteorapp = "";
    c.extensions = [];
    setCreative(c);
  }

  const editCreative = async(mode,id, key) => {
    var c = await vx.getCreative(id,key);
    if (!c) {
      alert("Server error!");
      return;
    }
    if (mode === 'VIEW')
      c.readOnly = true;

    // Rewrite the deals so we cn deal with them. Also, set the dealType which is used by the
    // DealEditor, 1 is no deal, 2 is private (price is 0) and 3 is preferred.
    if (c.dealSpec !== undef) {
      var rc = c.dealSpec.split(",");
      var array = [];
      for (var i = 0; i < rc.length;i++) {
        var d = rc[i];
        array.push({id:d.split(":")[0],price:d.split(":")[1]})
      }
      if (c.price === 0)
        c.dealType = 2;
      else
        c.dealType = 3;
      c.deals = array;
    } else {
      c.dealType = 1;
      c.deals = undef;
    }

    if (c.ext_spec !== undef) {
      var map = {};
      for (var i=0;i<c.ext_spec.length;i++) {
        var str = c.ext_spec[i];
        var n = str.split(":#:");
        map[n[0]] = n[1];
      }
      c.extensions = map;
      if (map["site_or_app"] !== undef)
        c.siteorapp = map["site_or_app"];
    }

    if (c.nativead) {
      c.nativead.assets = [];
      for (var i=0;i<c.nativead.native_assets.length;i++) {
        var asset = JSON.parse(c.nativead.native_assets[i]);
        c.nativead.assets.push(asset);
      }
    }

    c.bid_ecpm = c.price;
    setCreative(c);
  }

  const deleteCreative = async(id,key) => {
    await vx.deleteCreative(id,key);
    await vx.listCreatives();
    setCreative(null);
    redraw();
 }

  const makeNewVideo = async() => {
    if (creative !== null)
      return;

    var c = await vx.getNewCreative("video");
    c = setDates(c);
    c.isVideo = true;
    c.isAudio = false;
    c.isNative = false;
    c.isBanner = false;
    c.price = c.bid_ecpm;
    c.siteorapp = "";

    setCreative(c);
  }

  const makeNewNative = async() => {
    if (creative !== null)
    return;

    var c = await vx.getNewCreative("native");
    c = setDates(c);
    c.isVideo = false;
    c.isAudio = false;
    c.isNative = true;
    c.isBanner = false;
    c.nativead = {};
    c.nativead.assets = [];
    c.nativead.native_link = "";
    c.nativead.native_trk_urls = [];
    c.nativead.native_js_tracker = "";
    c.nativead.native_context = 1;
    c.nativead.native_contextsubtype = 10;
    c.nativead.native_plcmttype = 1;
    c.nativead.plcmtcnt = 1;

    c.price = c.bid_ecpm;
    c.siteorapp = "";

    setCreative(c);
  }

  const refresh = async() => {
    await vx.listCreatives();
    redraw();
  }

  const makeNewAudio = async() => {
    if (creative !== null)
      return;

    var c = await vx.getNewCreative("audio");
    c = setDates(c);
    c.isVideo = false;
    c.isAudio = true;
    c.isNative = false;
    c.isBanner = false;
    c.price = c.bid_ecpm;
    c.siteorapp = "";
    setCreative(c);
  }

  const update = (x) => {
    if (x !== null) {
      // rewrite the deals if they are present
      if (x.deals !== undef && x.deals.length > 0) {
        var deals = x.deals;
        var str = "";
        for (var i=0; i<deals.length;i++) {
          str += deals[i].id + ":" + deals[i].price;
          if (i+1 < deals.length)
            str += ","
        }
        x.deals = str;
      }
      
      x.bid_ecpm = x.price;

      /** Map the nativead to the database equivalent */
      if (x.nativead) {

        alert(JSON.stringify(x.nativead.assets,null,2));

        x.native_context = x.nativead.native_context;
        x.native_contextsubtype = x.nativead.native_contextsubtype;
        x.native_plcmttype = x.nativead.native_plcmttype;
        x.native_plcmtcnt = x.nativead.native_plcmtcnt = 1;
        x.native_link = x.nativead.native_link;
        x.native_trk_urls = [];
        if (x.nativead.native_trk_urls !== '')
          x.native_trk_urls.push(x.nativead.native_trk_urls);
        x.native_js_tracker = x.nativead.native_js_tracker;
        x.native_assets = [];
        for (var j = 0; j<x.nativead.assets.length;j++) {
          var asset = x.nativead.assets[j];
          x.native_assets.push(JSON.stringify(asset));
        }
      }

      vx.addNewCreative(x);
    }

    setCreative(null)
    setTimeout(refresh,2000);
  }

  const getBannersView = () => {
    return(
      vx.creatives.filter((e) => e.type === "banner").map((row, index) => (
        <tr key={'banner-' + row}>
          <td>{index}</td>
          <td key={'banner-name-' + index} className="text-left">{row.name}</td>
          {vx.user.sub_id === 'superuser' &&
              <td key={'banner-cust-' + index} className="text-left">{row.customer_id}</td>
          }
          <td key={'banner-id-' + index} className="text-right">{row.id}</td>
          <td className="text-center">
            <Button color="success" size="sm" onClick={()=>editCreative('VIEW',row.id,'banner')}>View</Button>
            &nbsp;
            <Button color="warning" size="sm" onClick={()=>editCreative('EDIT',row.id,'banner')}>Edit</Button>
            &nbsp;
            <Button color="danger" size="sm" onClick={()=>showModal(row.id,'banner')}>Delete</Button>
          </td>
        </tr>))
    ); 
  }

  const getVideosView = () => {
    return(
      vx.creatives.filter((e) => e.type === "video").map((row, index) => (
        <tr key={'video-' + row}>
          <td>{index}</td>
          <td key={'video-name-' + index} className="text-left">{row.name}</td>
          {vx.user.sub_id === 'superuser' &&
              <td key={'video-cust-' + index} className="text-left">{row.customer_id}</td>
          }
          <td key={'video-id-' + index} className="text-right">{row.id}</td>
          <td className="text-center">
            <Button color="success" size="sm" onClick={()=>editCreative('VIEW',row.id,'video')}>View</Button>
            &nbsp;
            <Button color="warning" size="sm" onClick={()=>editCreative('EDIT',row.id,'video')}>Edit</Button>
            &nbsp;
            <Button color="danger" size="sm" onClick={()=>showModal(row.id,'video')}>Delete</Button>
          </td>
        </tr>))
    ); 
}

const getAudiosView = () => {
  return(
    vx.creatives.filter((e) => e.type === "audio").map((row, index) => (
      <tr key={'audio-' + row}>
        <td>{index}</td>
        <td key={'audio-name-' + index} className="text-left">{row.name}</td>
        {vx.user.sub_id === 'superuser' &&
              <td key={'audio-cust-' + index} className="text-left">{row.customer_id}</td>
          }
        <td key={'audio-id-' + index} className="text-right">{row.id}</td>
        <td className="text-center">
          <Button color="success" size="sm" onClick={()=>editCreative('VIEW',row.id,'audio')}>View</Button>
          &nbsp;
          <Button color="warning" size="sm" onClick={()=>editCreative('EDIT',row.id,'audio')}>Edit</Button>
          &nbsp;
          <Button color="danger" size="sm" onClick={()=>showModal(row.id,'audio')}>Delete</Button>
        </td>
      </tr>))
  ); 
}

const getNativesView = () => {
  return(
    vx.creatives.filter((e) => e.type === "native").map((row, index) => (
      <tr key={'native-' + row}>
        <td>{index}</td>
        <td key={'native-name-' + index} className="text-left">{row.name}</td>
        {vx.user.sub_id === 'superuser' &&
              <td key={'native-cust-' + index} className="text-left">{row.customer_id}</td>
          }
        <td key={'native-id-' + index} className="text-right">{row.id}</td>
        <td className="text-center">
          <Button color="success" size="sm" onClick={()=>editCreative('VIEW',row.id,'native')}>View</Button>
          &nbsp;
          <Button color="warning" size="sm" onClick={()=>editCreative('EDIT',row.id,'native')}>Edit</Button>
          &nbsp;
          <Button color="danger" size="sm" onClick={()=>showModal(row.id,'native')}>Delete</Button>
        </td>
      </tr>))
  );
}

  const setInstances = () => {

  };

   ////////////////////////////// DELETE CREATIVE ///////////////////////////////////
   const [modal, setModal] = useState(false);
   const [id, setId] = useState(0);
   const [type, setType] = useState('');

   const modalCallback = (doit) => {
     if (doit) {
       deleteCreative(id,type)
     }
     setModal(!modal);
 
   }
   const showModal = (x,y) => {
     setId(x);
     setType(y);
     setModal(true);
   }
   /////////////////////////////////////////////////////////////////////////////////////

  return (
    <div className="content">
    { !vx.isLoggedIn && <LoginModal callback={setInstances} />}
    { modal &&
      <DecisionModal title="Really delete creative?" 
                     message="Only the db admin can undo this if you delete it!!!" 
                     name="DELETE"
                     callback={modalCallback} />}
    { creative === null && <>
        <Row>
            <Col xs="12">
            <Button size="sm" className="btn-fill" color="success" onClick={refresh}>Refresh</Button>
            <Button size="sm" className="btn-fill" color="danger" onClick={makeNewBanner}>New</Button>
                <Card className="card-chart">
                    <CardHeader>
                        <Row>
                            <CardTitle tag="h2">Banners</CardTitle>
                        </Row>
                    </CardHeader>
                    <CardBody>
                      <Table key={"banners-table-"+count} size="sm">
                        <thead>
                          <tr>
                            <th>#</th>
                            <th className="text-center">Name</th>
                            {vx.user.sub_id === 'superuser' &&
                              <th className="text-center">Customer</th>
                            }
                            <th className="text-right">SQL-ID</th>
                            <th className="text-center">Actions</th>
                          </tr>
                      </thead>
                      <tbody>
                        { getBannersView() }
                      </tbody>
                    </Table>
                  </CardBody>           
                </Card>
            </Col>
        </Row>

        <Row>
            <Col xs="12">
            <Button size="sm" className="btn-fill" color="success" onClick={refresh}>Refresh</Button>
            <Button size="sm" className="btn-fill" color="danger" onClick={makeNewVideo}>New</Button>
                <Card className="card-chart">
                    <CardHeader>
                        <Row>
                            <CardTitle tag="h2">Videos</CardTitle>
                        </Row>
                    </CardHeader>
                    <CardBody>
                      <Table key={"banners-table-"+count} size="sm">
                        <thead>
                          <tr>
                            <th>#</th>
                            <th className="text-center">Name</th>
                            {vx.user.sub_id === 'superuser' &&
                              <th className="text-center">Customer</th>
                            }
                            <th className="text-right">SQL-ID</th>
                            <th className="text-right">Name</th>
                          </tr>
                      </thead>
                      <tbody>
                        { getVideosView() }
                      </tbody>
                    </Table>
                  </CardBody>           
                </Card>
            </Col>
        </Row>

        <Row>
            <Col xs="12">
            <Button size="sm" className="btn-fill" color="success" onClick={refresh}>Refresh</Button>
            <Button size="sm" className="btn-fill" color="danger" onClick={makeNewAudio}>New</Button>
                <Card className="card-chart">
                    <CardHeader>
                        <Row>
                            <CardTitle tag="h2">Audio</CardTitle>
                        </Row>
                    </CardHeader>
                    <CardBody>
                      <Table key={"banners-table-"+count} size="sm">
                        <thead>
                          <tr>
                            <th>#</th>
                            <th className="text-center">Name</th>
                            {vx.user.sub_id === 'superuser' &&
                              <th className="text-center">Customer</th>
                            }
                            <th className="text-right">SQL-ID</th>
                            <th className="text-right">Name</th>
                          </tr>
                      </thead>
                      <tbody>
                        { getAudiosView() }
                      </tbody>
                    </Table>
                  </CardBody>           
                </Card>
            </Col>
        </Row>

        <Row>
            <Col xs="12">
            <Button size="sm" className="btn-fill" color="success" onClick={refresh}>Refresh</Button>
            <Button size="sm" className="btn-fill" color="danger" onClick={makeNewNative}>New</Button>
                <Card className="card-chart">
                    <CardHeader>
                        <Row>
                            <CardTitle tag="h2">Native</CardTitle>
                        </Row>
                    </CardHeader>
                    <CardBody>
                      <Table key={"banners-table-"+count} size="sm">
                        <thead>
                          <tr>
                            <th>#</th>
                            <th className="text-center">Name</th>
                            {vx.user.sub_id === 'superuser' &&
                              <th className="text-center">Customer</th>
                            }
                            <th className="text-right">SQL-ID</th>
                            <th className="text-right">Name</th>
                          </tr>
                      </thead>
                      <tbody>
                        { getNativesView() }
                      </tbody>
                    </Table>
                  </CardBody>           
                </Card>
            </Col>
        </Row>

        </>
        }
        { creative !== null &&
            <CreativeEditor key={"creative-"+count} creative={creative} callback={update} />
        }
    </div>
  );
 }

 export default Creatives;