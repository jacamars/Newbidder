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
import LoginModal from '../LoginModal';
import RuleEditor from './editors/RuleEditor.jsx';
import RulesView from './RulesView.jsx';

var undef;

const Rules = (props) => {
  useEffect(() => {
  }, []);


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
      refresh();
   }

  const update = async(e) => {
      if (e != null) {
        await vx.addNewRule(e);
      }

      setRule(null);
      refresh();
  }

  return (
    <div className="content">
    { !vx.isLoggedIn && <LoginModal callback={setInstances} />}
        <Row id={"rules-"+count}>
            <Col xs="12">
            { rule == null && <>
              <RulesView key={"ruleview-"+count}
                  refresh={refresh}
                  editRule={editRule}
                  deleteRule={deleteRule}
                  viewRule={viewRule}
                  makeNew={makeNew}/>
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