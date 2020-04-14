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
    const [userModal, setUserModal] = useState(false);

    const editUser = async(id) => {
        var u = getUserById(id);
        await vx.addNewUser(u);
        alert("User: " + u.username + " saved");
      }
    
      const getUserById = (id) => {
        var c;
        for (var i=0;i<users.length;i++) {
          if (users[i].id === id) {
            var c = users[i];
            c.index = i;
            return c;
          }
        }
        return undef;
      }
    
      const deleteUser = async (id) => {
        var c = getUserById(id);
        setRecord(c);
        console.log("DELETE: " + c.username);
        setUserModal(true);
      }
    

    return (
        props.users.map((row, index) => {
            <Alert color="info" key={"users-"+index}>
              <Row>
                <Col md="12">
                  <Card>
                    <CardHeader>
                      <Row>
                        <Input 
                          disabled={vx.user.customer_id != 'rtb4free'}
                          onChange={(e) => changeUserField(e,index,'customer_id')}
                          type="select">{customerNames(affiliates,row)}</Input>
                      </Row>
                    </CardHeader>
                    <CardBody>
                      <Row>
                        <Col className="pr-md-1" md="2">
                          <FormGroup>
                          <label>Username</label>
                          <Input 
                            defaultValue={row.username}
                            onChange={(e) => changeUserField(e,index,'username')}/>
                          </FormGroup>
                        </Col>
                        <Col className="pr-md-1" md="2">
                          <FormGroup>
                          <label>Role</label>
                          <Input 
                            defaultValue={row.sub_id}
                            onChange={(e) => changeUserField(e,index,'sub_id')}/>
                          </FormGroup>
                      </Col>
                      <Col className="pr-md-1" md="2">
                          <FormGroup>
                          <label>Password</label>
                          <Input 
                            defaultValue={row.password}
                            onChange={(e) => changeUserField(e,index,'password')}/>
                          </FormGroup>
                      </Col>
                    </Row>
                    <Row>
                      <Button color="success" size="sm" onClick={()=>editUser(row.id)}>Save</Button>
                      &nbsp;
                      <Button color="warning" size="sm" onClick={()=>deleteUser(row.id)}>Delete</Button>
                    </Row>
                    </CardBody>
                  </Card>
                </Col>
              </Row>
            </Alert>
        })
    );
}

export default ESUser;
