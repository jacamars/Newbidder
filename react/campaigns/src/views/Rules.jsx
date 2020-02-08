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

    const refresh = async () => {
      await vx.listRules();
      redraw();
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
    console.log("GetRulesView, rows = " + vx.rules.length);

    return(
       vx.rules.map((row, index) => (
         <tr key={'rulesview-' + row}>
           <td>{index}</td>
           <td key={'rules-name-' + index} className="text-left">{row.name}</td>
           <td key={'rules-id-' + index} className="text-right">{row.id}</td>
           <td key={'rules-hierarchy' + index} className="text-right">{row.hierarchy}</td>
           <td key={'rules-edit-'+ index} className="text-center">
             <Button color="success" size="sm" onClick={()=>viewRule(row.id)}>View</Button>
             &nbsp;
             <Button color="warning" size="sm" onClick={()=>editRule(row.id)}>Edit</Button>
             &nbsp;
             <Button color="danger" size="sm" onClick={()=>deleteRule(row.id)}>Delete</Button>
           </td>
         </tr>))
     ); 
   }

   const editRule = async(id) => {
      var r = await vx.getRule(id);
      setRule(r);
   }

   const viewRule = async(id) => {
    var r = await vx.getRule(id);
    r.readOnly = true;
    setRule(r);
  }

   const deleteRule = async(id) => {
      await vx.deleteRule(id);
      await vx.listRules();
      setRule(null);
      redraw();
   }

  const update = async(e) => {
      if (e != null) {
        await vx.addNewRule(e);
      }

      setRule(null);
      redraw();
  }

  return (
    <div className="content">
    { !vx.isLoggedIn && <LoginModal callback={setInstances} />}
        <Row>
            <Col xs="12">
            { rule == null && <>
            <Button size="sm" className="btn-fill" color="success" onClick={refresh}>Refresh</Button>
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