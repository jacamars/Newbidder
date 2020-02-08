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

  const deleteTarget = async(id) => {
    await vx.deleteTarget(id);
    await vx.listTargets();
    redraw();
  }

  const editTarget = async(id) => {
    var t = await vx.getTarget(id);
    if (t) {
      setTarget(t);
    }
  }

  const update = async (x) => {
    if (x !== null) {
      await vx.addNewTarget(x);
    }
    setTarget(null)
    await vx.listTargets();
    redraw();
  }

  const setInstances = () => {

  };

  const getTargetsView = () => {
    console.log("GetTargetsView, rows = " + vx.targets.length);

    return(
       vx.targets.map((row, index) => (
         <tr key={'targetsview-' + row}>
           <td>{index}</td>
           <td key={'targets-name-' + index} className="text-left">{row.name}</td>
           <td key={'targets-id-' + index} className="text-right">{row.id}</td>
           <td className="text-center"><Button color="success" size="sm" onClick={()=>editTarget(row.id)}>Edit</Button>
           &nbsp;
           <Button color="danger" size="sm" onClick={()=>deleteTarget(row.id)}>Delete</Button></td>
         </tr>))
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
                            <th className="text-center">Actions</th>
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