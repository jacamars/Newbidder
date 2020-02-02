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
import TargetEditor from './editors/TargetEditor.jsx'

var undef;

 const Targets = (props) => {

  const vx = useViewContext();
  const [target, setTarget] = useState(null);
  const [count, setCount] = useState(0);

  const redraw = () => {
      setCount(count+1);
  }

  const makeNew = async() => {
    if (target !== null)
    return;

    var targ = await vx.getNewTarget("My New Target");
    setTarget(targ);
  }

  const update = (x) => {
    if (x !== null) {
        // update database;
    }
    setTarget(null)
    redraw();
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
        <Row>
            <Col xs="12">
            { target == null && <>
            <Button size="sm" className="btn-fill" color="success" onClick={redraw}>Refresh</Button>
            <Button size="sm" className="btn-fill" color="danger" onClick={makeNew}>New</Button>
                <Card className="card-chart">
                    <CardHeader>
                        <Row>
                            <CardTitle tag="h2">Targets in DB</CardTitle>
                        </Row>
                    </CardHeader>
                    <CardBody>
                      <Table key={"bidders-table-"+count} size="sm">
                        <thead>
                          <tr>
                            <th>#</th>
                            <th className="text-center">Name</th>
                            <th className="text-right">SQL-ID</th>
                            <th className="text-right">Target</th>
                          </tr>
                      </thead>
                      <tbody>
                        { getTargetsView() }
                      </tbody>
                    </Table>
                  </CardBody>
                </Card>
                </>
                }
                { target !== null &&
                    <TargetEditor key={"targ-"+count} target={target} callback={update} />
                }
            </Col>
        </Row>
    </div>
  );
 }

 export default Targets;