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
    c.isVideo = false;
    c.isAudio = false;
    c.isNative = false;
    c.isBanner = true;
    setCreative(c);
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
        // update database;
    }
    setCreative(null)
    redraw();
  }

  const getBannersView = () => {
      return(
        <>
        </>
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
            <Button size="sm" className="btn-fill" color="success" onClick={redraw}>Refresh</Button>
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
                            <th className="text-right">Name</th>
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