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
import DecisionModal from "../DecisionModal";

var undef;

const Sets = (props) => {

    useEffect(() => {

    }, []);

    const [bigdata, setBigdata] = useState([]);
    const [hazel, setHazel] = useState({});
    const [count, setCount] = useState(0);
    const [modal, setModal] = useState(false);
    const [name, setName] = useState('');
    const vx = useViewContext();
  
    const redraw = () => {
        setCount(count+1);
    }

    const refresh = async () => {
        var d = await vx.listSymbols();
        setBigdata(d.catalog);
        setHazel(d.hazelcast);
        redraw();
        return d;
      }

  const setInstances = () => {
    refresh();
  };

  const showModal = (n) => {
    setName(n);
    setModal(true);
  }

  const makeNew = () => {

  }

  const query = (name) => {

  }

  const queryHazel = (key) => {

  }


  const getSetsView = () => {
    return(
       bigdata.map((row, index) => (
         <tr key={'setsview-' + index}>
           <td>{index}</td>
           <td key={'sets-name-' + index} className="text-left">{row.name}</td>
           <td key={'sets-type-' + index} className="text-left">{row.type}</td>
           <td key={'sets-size-' + index} className="text-right">{row.size}</td>
           <td key={'sets-actions-' + index} className="text-center">
            <Button color="info" size='sm' onClick={()=>query(row.name)}>Query</Button>
            &nbsp; &nbsp; &nbsp;<Button color="danger" size="sm" onClick={()=>showModal(row.name)}>Delete</Button></td>
         </tr>))
     ); 
   }

   const getHazelView = () => {
    return(
       Object.keys(hazel).map((key,index) => (
         <tr key={'hazelview-' + index}>
           <td>{index}</td>
           <td key={'hazel-name-' + index} className="text-left">{key}</td>
           <td key={'hazel-count-' + index} className="text-left">{hazel[key]}</td>
           <td key={'hazel-actions-' + index} className="text-center">
            <Button color="info" size='sm' onClick={()=>queryHazel(key)}>Query</Button></td>
         </tr>))
     ); 
   }

   const modalCallback = (doit) => {
    if (doit) {
      vx.deleteSymbol(name);
    }
    setModal(!modal);
    refresh();
  }

  return (
    <div className="content">
    { !vx.isLoggedIn && <LoginModal callback={setInstances} />}
    { modal &&
      <DecisionModal title="Really delete campaign?" 
                     message="Only the db admin can undo this if you delete it!!!" 
                     name="DELETE"
                     callback={modalCallback} />}
        <Row>
            <Col xs="12">
            <Button size="sm" className="btn-fill" color="success" onClick={refresh}>Refresh</Button>
            <Button size="sm" className="btn-fill" color="danger" onClick={makeNew}>Load</Button>
                <Card className="card-chart">
                    <CardHeader>
                        <Row>
                            <CardTitle tag="h2">Sets/Navmap/Cidr/Bloom-Filters in DB</CardTitle>
                        </Row>
                    </CardHeader>
                    <CardBody>
                      <Table key={"sets-table-"+count} size="sm">
                        <thead>
                          <tr>
                            <th>#</th>
                            <th className="text-center">Name</th>
                            <th className="text-center">Type</th>
                            <th className="text-center">Records</th>
                            <th className="text-center">Actions</th>
                          </tr>
                      </thead>
                      <tbody>
                        { getSetsView() }
                      </tbody>
                    </Table>
                  </CardBody>
                </Card>
            </Col>
        </Row>

        <Row>
            <Col xs="12">
            <Button size="sm" className="btn-fill" color="success" onClick={refresh}>Refresh</Button>
                <Card className="card-chart">
                    <CardHeader>
                        <Row>
                            <CardTitle tag="h2">In Memory Data  Grid</CardTitle>
                        </Row>
                    </CardHeader>
                    <CardBody>
                      <Table key={"sets-table-"+count} size="sm">
                        <thead>
                          <tr>
                            <th>#</th>
                            <th className="text-center">Name</th>
                            <th className="text-center">Records</th>
                            <th className="text-center">Actions</th>
                          </tr>
                      </thead>
                      <tbody>
                        { getHazelView() }
                      </tbody>
                    </Table>
                  </CardBody>
                </Card>
            </Col>
        </Row>
    </div>
  );
 }

 export default Sets;