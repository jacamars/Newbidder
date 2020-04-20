import React , { useState, useEffect } from "react";
import {useViewContext } from "./ViewContext";
import LoginModal from './LoginModal'

// reactstrap components
import {
  Alert,
  Button,
  ButtonGroup,
  Card,
  CardHeader,
  CardBody,
  CardFooter,
  CardText,
  FormGroup,
  Form,
  Input,
  Row,
  Table,
  Col
} from "reactstrap";
import { PayPalButton } from "react-paypal-button-v2";
import ViewAssets from "./views/editors/ViewAssets";
import {uuidv4, customerIds, customerNames} from "./Utils"
import DecisionModal from "./DecisionModal";

var undef;


const ESUser = (props) => {

    const vx = useViewContext();

    const showUsers = () => {
        var items  =[];
        props.users.map((row, index) => {
            items.push(
            <Alert color="info" key={"alert-users-"+index}>
              <Row>
                <Col className="pr-md-1" md="2">
                    <Input 
                        disabled={vx.user.customer_id != 'rtb4free'}
                        onChange={(e) => props.changeUserField(e,index,'customer_id')}
                        type="select">{customerNames(props.affiliates,row)}</Input>
                </Col>
              </Row>
              <Row>
                  <Col className="pr-md-1" md="2">
                      <FormGroup>
                        <label>Username</label>
                          <Input 
                            defaultValue={row.username}
                            onChange={(e) => props.changeUserField(e,index,'username')}/>
                      </FormGroup>
                  </Col>
                  <Col className="pr-md-1" md="2">
                      <FormGroup>
                        <label>Role</label>
                          <Input 
                            defaultValue={row.sub_id}
                            onChange={(e) => props.changeUserField(e,index,'sub_id')}/>
                        </FormGroup>
                    </Col>
                    <Col className="pr-md-1" md="2">
                      <FormGroup>
                        <label>Password</label>
                          <Input 
                            defaultValue={row.password}
                            onChange={(e) => props.changeUserField(e,index,'password')}/>
                      </FormGroup>
                    </Col>
                    </Row>
                    <Row>
                      <Button color="success" size="sm" onClick={()=>props.editUser(row.id)}>Save</Button>
                      &nbsp;
                      <Button color="warning" size="sm" onClick={()=>props.deleteUser(row.id)}>Delete</Button>
                    </Row>
            </Alert>
            )
        });
        return items;
    }

    return (
        <>
        { 
          showUsers() 
        }
        </>
    );
}

export default ESUser;
