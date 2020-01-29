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

var undef;

 const Campaigns = (props) => {

  const [count, setCount] = useState(0);
  const vx = useViewContext();

  const setInstances = () => {

  };

  return (
    <div className="content">
    { !vx.isLoggedIn && <LoginModal callback={setInstances} />}
        <Row>
            <Col xs="12">
                <Card className="card-chart">
                    <CardHeader>
                        <Row>
                            <CardTitle tag="h2">Campaigns</CardTitle>
                        </Row>
                    </CardHeader>
                </Card>
            </Col>
        </Row>
    </div>
  );
 }

 export default Campaigns;