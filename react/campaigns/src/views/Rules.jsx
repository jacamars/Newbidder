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
import RuleEditor from './editors/RuleEditor.jsx'

var undef;

const Rules = (props) => {

    const [count, setCount] = useState(0);
    const [rule, setRule] = useState(null);
    const vx = useViewContext();
  
    const redraw = () => {
        setCount(count+1);
    }

  const setInstances = () => {

  };

  const makeNew = async() => {
    if (rule !== null)
        return;

    var r = await vx.getNewRule("My New Rule");
    setRule(r);
  }

  const getRulesView = () => {

    return(
        <div>
        </div>
    );
  }

  const update = (e) => {
      setRule(null);
      if (e !== null) {
        // update database
        setRule(null);
      }
      redraw();
  }

  return (
    <div className="content">
    { !vx.isLoggedIn && <LoginModal callback={setInstances} />}
        <Row>
            <Col xs="12">
            { rule == null && <>
            <Button size="sm" className="btn-fill" color="success" onClick={redraw}>Refresh</Button>
            <Button size="sm" className="btn-fill" color="danger" onClick={makeNew}>New</Button>
                <Card className="card-chart">
                    <CardHeader>
                        <Row>
                            <CardTitle tag="h2">Rules in DB</CardTitle>
                        </Row>
                    </CardHeader>
                    <CardBody>
                      <Table key={"bidders-table-"+count} size="sm">
                        <thead>
                          <tr>
                            <th>#</th>
                            <th className="text-center">Name</th>
                            <th className="text-right">SQL-ID</th>
                            <th className="text-right">Rule</th>
                          </tr>
                      </thead>
                      <tbody>
                        { getRulesView() }
                      </tbody>
                    </Table>
                  </CardBody>
                </Card>
                </>
                }
                { rule !== null &&
                    <RuleEditor key={"rule-"+count} rule={rule} callback={update} />
                }
            </Col>
        </Row>
    </div>
  );
 }

 export default Rules;