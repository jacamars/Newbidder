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

var undef;

const RulesView = (props) => {

    const vx = useViewContext();

    const getRulesView = () => {
        if (vx.rules === undef)
          return(null);
          
    
        var rules = vx.rules;
        rules.sort(function(a, b) {
         a = a.customer_id + a.name;
         b = b.customer_id + b.name;
         return (a > b) - (a < b);
        });
        return(
           rules.map((row, index) => (
             <tr key={'rulesview-' + row}>
               <td>{index}</td>
               <td key={'rules-name-' + index} className="text-left">{row.name}</td>
               {vx.user.sub_id === 'superuser' &&
                <td key={'rules-cust-' + index} className="text-left">{row.customer_id}</td>
               }
               <td key={'rules-id-' + index} className="text-right">{row.id}</td>
               <td key={'rules-hierarchy' + index} className="text-right">{row.hierarchy}</td>
               <td key={'rules-edit-'+ index} className="text-center">
                 <Button color="success" size="sm" onClick={()=>props.viewRule(row.id)}>View</Button>
                 &nbsp;
                 <Button color="warning" size="sm" onClick={()=>props.editRule(row.id)}>Edit</Button>
                 &nbsp;
                 <Button color="danger" size="sm" onClick={()=>props.deleteRule(row.id)}>Delete</Button>
               </td>
             </tr>))
         ); 
       }


    return(
    <Row>
    <Col xs="12">
    <Button size="sm" className="btn-fill" color="success" onClick={props.refresh}>Refresh</Button>
    <Button size="sm" className="btn-fill" color="danger" onClick={props.makeNew}>New</Button>
        <Card className="card-chart">
            <CardHeader>
                <Row>
                    <CardTitle tag="h2">Rules in DB</CardTitle>
                </Row>
            </CardHeader>
            <CardBody>
              <Table size="sm">
                <thead>
                  <tr>
                    <th>#</th>
                    <th className="text-center">Name</th>
                    {vx.user.sub_id === 'superuser' &&
                        <th className="text-center">Customer</th>
                    }
                    <th className="text-right">SQL-ID</th>
                    <th className="text-right">Hierarchy</th>
                    <th className="text-center">Actions</th>
                  </tr>
              </thead>
              <tbody>
                { getRulesView() }
              </tbody>
            </Table>
          </CardBody>
        </Card>
    </Col>
</Row>
    );
}

export default RulesView;