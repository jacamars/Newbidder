import React , { useState, useEffect } from "react";
import {useViewContext } from "../ViewContext";

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
  InputGroup,
  InputGroupAddon,
  Row,
  Table,
  Col
} from "reactstrap";
import { PayPalButton } from "react-paypal-button-v2";
import {uuidv4, customerIds, customerNames} from "../Utils"
import DecisionModal from "../DecisionModal";

var undef;


const ESAffilates = (props) => {

    const vx = useViewContext();

    const doBudget = (index) => {
        if (vx.user.customer_id === 'rtb4free') {
            var amount = document.getElementById("index-"+index).value;
            props.setDirectBudget(index,amount);
        } else {
            props.setPaypalBudget(index);
        }
    }

    const getCompanies = () => {
        var items = [];
        props.affiliates.map((row, index) => {
          items.push(
          <Alert color="success" key={"companies-"+index}>
          <Row>
          <Col md="12">
            <Card>
              <CardHeader>
                <Row>
                  <Col className="pr-md-1" md="2">
                    <FormGroup>
                      <label>Company Name</label>
                        <Input
                          onChange={(e) => props.changeCompanyField(e,index,'customer_name')}
                          defaultValue={row.customer_name}/>
                    </FormGroup>
                  </Col>
                  <Col className="pr-md-1" md="2">
                    <FormGroup>
                      <label>ID (Disabled)</label>
                      <Input
                          disabled={true}
                          defaultValue={row.customer_id}/>
                    </FormGroup>
                  </Col>
                  <Col className="pr-md-1" md="3">
                    <FormGroup>
                    <label>Budget</label>
                    <InputGroup>
                        <InputGroupAddon addonType="prepend"><Button size="sm" onClick={()=>doBudget(index)}>Update</Button></InputGroupAddon>
                        &nbsp;
                        <Input 
                            id={"index-"+index}
                            disabled={!(vx.user.customer_id === 'rtb4free')}
                            defaultValue={row.budget}/>
                        </InputGroup>
                    </FormGroup>
                  </Col>
                </Row>
              </CardHeader>
              <CardBody>
              <Form>
                <Row>
                  <Col className="pr-md-1" md="2">
                    <FormGroup>
                      <label>E-Mail</label>
                      <Input
                        onChange={(e) => props.changeCompanyField(e,index,'email')}
                        defaultValue={row.email}/>
                    </FormGroup>
                  </Col>
                  <Col className="pr-md-1" md="2">
                    <FormGroup>
                      <label>Telephone</label>
                      <Input
                        onChange={(e) => props.changeCompanyField(e,index,'telephone')}
                        defaultValue={row.telephone}/>
                    </FormGroup>
                  </Col>
                  <Col className="pr-ms-1" md="2">
                    <FormGroup>
                      <label>First name</label>
                      <Input
                        onChange={(e) => props.changeCompanyField(e,index,'firstname')}
                        defaultValue={row.firstname}/>
                    </FormGroup>
                  </Col>
                  <Col className="pr-ms-1" md="2">
                    <FormGroup>
                      <label>Last name</label>
                      <Input
                        onChange={(e) => props.changeCompanyField(e,index,'lastname')}
                        defaultValue={row.lastname}/>
                    </FormGroup>
                  </Col>
                </Row>
                <Row>
                  <Col className="pr-md-1" md="3">
                    <FormGroup>
                      <label>Address</label>
                      <Input
                        onChange={(e) => props.changeCompanyField(e,index,'address')}
                        defaultValue={row.address}/>
                    </FormGroup>
                  </Col>
                  <Col className="pr-md-1" md="2">
                    <FormGroup>
                      <label>City, State</label>
                      <Input
                        onChange={(e) => props.hangeCompanyField(e,index,'citystate')}
                        defaultValue={row.citystate}/>
                    </FormGroup>
                  </Col>
                  <Col className="pr-md-1" md="2">
                    <FormGroup>
                      <label>Postal Code</label>
                      <Input
                        onChange={(e) => props.changeCompanyField(e,index,'postal_code')}
                        defaultValue={row.postalcode}/>
                    </FormGroup>
                  </Col>
                </Row>
                <Row>
                  <Button color="success" size="sm" onClick={()=>props.editAffiliate(index)}>Save</Button>
                  &nbsp;
                  <Button color="warning" size="sm" onClick={()=>props.deleteAffiliate(index)}>Delete</Button>
                  &nbsp;
                  <Button color="info" size="sm" onClick={()=>props.addBudget(index)}>Budget</Button>
                </Row>
              </Form>
              </CardBody>
            </Card>
            </Col>
            </Row>
            </Alert>
          );
        });
        return items;
      }

      return(
       <>
       { getCompanies() }   
       </>
      );
    
}

export default ESAffilates;
