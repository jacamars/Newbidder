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
import CreativeEditor from './editors/CreativeEditor.jsx'

var undef;

 const Creatives = (props) => {

  const vx = useViewContext();
  const [creative, setCreative] = useState(null);
  const [count, setCount] = useState(0);

  const redraw = () => {
      setCount(count+1);
  }

  const makeNewBanner = async() => {
    if (creative !== null)
    return;

    var c = await vx.getNewCreative("My New Banner");
    c.id = 0;
    c.isVideo = false;
    c.isAudio = false;
    c.isNative = false;
    c.isBanner = true;
    c.react_type = "BANNER";
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

    var c = await vx.getNewVideo("My New Video");
    c.isVideo = true;
    c.isAudio = false;
    c.isNative = false;
    c.isBanner = false;

    setCreative(clearInterval);
  }

  const makeNewNative = async() => {
    if (creative !== null)
    return;

    var c = await vx.getNewNative("My New Native");
    c.isVideo = false;
    c.isAudio = false;
    c.isNative = true;
    c.isBanner = false;
    setCreative(c);
  }

  const refresh = async() => {
    await vx.listCreatives();
    redraw();
  }

  const makeNewAudio = async() => {
    if (creative !== null)
    return;

    var c = await vx.getNewAudio("My New Audio");
    c.isVideo = false;
    c.isAudio = true;
    c.isNative = false;
    c.isBanner = false;
    setCreative(c);
  }

  const update = (x) => {
    if (x !== null) {
      vx.addNewCreative(x);
    }
    setCreative(null)
    redraw();
  }

  const getBannersView = () => {
    return(
      vx.creatives.filter((e) => e.type === "banner").map((row, index) => (
        <tr key={'banner-' + row}>
          <td>{index}</td>
          <td key={'banner-name-' + index} className="text-left">{row.name}</td>
          <td key={'banner-id-' + index} className="text-right">{row.id}</td>
          <td className="text-center">
            <Button color="success" size="sm" onClick={()=>editCreative('VIEW',row.id,'banner')}>View</Button>
            &nbsp;
            <Button color="warning" size="sm" onClick={()=>editCreative('EDIT',row.id,'banner')}>Edit</Button>
            &nbsp;
            <Button color="danger" size="sm" onClick={()=>deleteCreative(row.id,'banner')}>Delete</Button>
          </td>
        </tr>))
    ); 
  }

  const getVideosView = () => {
    return(
      <>
      </>
    );
}

const getAudiosView = () => {
  return(
    <>
    </>
  );
}

const getNativesView = () => {
  return(
    <>
    </>
  );
}

  const setInstances = () => {

  };

  const getTargetsView = () => {

    return(
        <div>
        </div>
    );
  }

  return (
    <div className="content">
    { !vx.isLoggedIn && <LoginModal callback={setInstances} />}
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
            <Button size="sm" className="btn-fill" color="success" onClick={redraw}>Refresh</Button>
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
            <Button size="sm" className="btn-fill" color="success" onClick={redraw}>Refresh</Button>
            <Button size="sm" className="btn-fill" color="danger" onClick={makeNewVideo}>New</Button>
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
            <Button size="sm" className="btn-fill" color="success" onClick={redraw}>Refresh</Button>
            <Button size="sm" className="btn-fill" color="danger" onClick={makeNewVideo}>New</Button>
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