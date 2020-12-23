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
import LoadSymbol from "../LoadSymbol";
import {undef, lookingGlassOptions } from "../Utils";

var symbol = { s3: "", filename:"", name: "", type: "", size:""};

const Sets = (props) => {

    useEffect(() => {
      refresh();
    }, []);

    const [bigdata, setBigdata] = useState([]);
    const [hazel, setHazel] = useState({});
    const [macros, setMacros] = useState({});
    const [count, setCount] = useState(0);
    const [modal, setModal] = useState(false);
  
    const [name, setName] = useState('');
    const [querySymbol, setQuerySymbol] = useState(false);
    const [queryHazelcast, setQueryHazelcast] = useState(false);
    const [object, setObject] = useState(undef);
    const vx = useViewContext();
  
    const redraw = () => {
        setCount(count+1);
    }

    const refresh = async (name) => {
        if (name !== undef) {
          if (name === 'macros') setMacros({});
          if (name === 'bigdata') setBigdata([]);
          if (name === 'hazel') setHazel({});
        } else {
          setMacros({})
          setBigdata([]);
          setHazel({});
        }
        redraw();

        var d = await vx.listSymbols();
        if (d === undef) {
          alert("List Symbols failed");
          return;
        }
        setBigdata(d.catalog);
        setHazel(d.hazelcast);
        var m = await vx.listMacros();
        if (m === undef) {
          alert("List Macros failed");
        } else
          setMacros(m);
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

  const query = (name) => {
    setName(name);
    setQuerySymbol(true);

  }

  const querySymbolCallback = async (valid,key) => {
    setQuerySymbol(false);
    if (!valid)
      return;

    var reply = await vx.querySymbol(name,key);
    alert(reply);
  }

  const queryHazel = (name) => {
    setName(name);
    setQueryHazelcast(true);
  }

  const queryHazelCallback = async (valid,key) => {
    setQueryHazelcast(false);
    if (!valid)
      return;

    var reply = await vx.queryHazelcast(name,key);
    alert(reply);
  }

  const reload = async (name) => {
    var d = await vx.listSymbols();
    for (var j = 0; j< d.catalog.length; j++) {
      var row = d.catalog[j];
      if (name === row.name) {
        setObject(row);
        return;
      }
    }
  }

  const makeNew = () => {
    var d = setObject({name: "@NewObject", file: "", s3: "", type: "SET", size: ""});
  }

  const makeNewSymbol = async (t) =>{
    if (t) {
      await vx.configureAwsObject(object);
    }
    setObject(undef);
    var d = await vx.listSymbols()
    setBigdata(d.catalog);
    redraw();
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
            &nbsp; &nbsp; &nbsp;<Button color="warn" size="sm" onClick={()=>reload(row.name)}>Reload</Button>
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

   const getMacroView = () => {
    return(
       Object.keys(macros).map((key,index) => (
         <tr key={'macroview-' + index}>
           <td>{index}</td>
           <td key={'macro-name-' + index} className="text-left">{key}</td>
           <td key={'macro-value-' + index} className="text-left">{macros[key]}</td>
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
    { object !== undef &&
      <LoadSymbol callback={makeNewSymbol} symbol={object}/>}
    { querySymbol &&
      <DecisionModal title="Query Symbol" 
        message="Input key to query" 
        name="Query"
        input={true}
        callback={querySymbolCallback} />}
    { queryHazelcast &&
      <DecisionModal title="Query Cache" 
        message="Input predicate" 
        name="Predicate"
        input={true}
        callback={queryHazelCallback} />}
        <Row>
            <Col xs="12">
                <div className="row mb-3">
                    <div className="col-xl-12 col-lg-12" >
                        <strong className="h3">
                            Sets/Navmap/Cidr/Bloom-Filters
                        </strong>
                        <Button size="sm" style={{float: 'right'}} className="btn-fill" color="error" onClick={(e)=>{refresh('bigdata')}}>Refresh</Button>
                        <Button size="sm" style={{float: 'right'}} className="btn-fill" color="success" onClick={(e)=>makeNew()}>Load</Button>
                    </div>
                </div>
                <Card className="card-chart">
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
                <div className="row mb-3">
                    <div className="col-xl-12 col-lg-12" >
                        <strong className="h3">
                            In-Memory Data Grid
                        </strong>
                        <Button size="sm" style={{float: 'right'}} className="btn-fill" color="error" onClick={(e)=>{refresh('hazel')}}>Refresh</Button>
                    </div>
                </div>
                <Card className="card-chart">
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

        <Row>
            <Col xs="12">
                <div className="row mb-3">
                    <div className="col-xl-12 col-lg-12" >
                        <strong className="h3">
                            Macros
                        </strong>
                        <Button size="sm" style={{float: 'right'}} className="btn-fill" color="error" onClick={(e)=>{refresh('macros')}}>Refresh</Button>
                    </div>
                </div>
                <Card className="card-chart">
                    <CardBody>
                      <Table key={"sets-table-"+count} size="sm">
                        <thead>
                          <tr>
                            <th>#</th>
                            <th className="text-center">Name</th>
                            <th className="text-center">Value</th>
                          </tr>
                      </thead>
                      <tbody>
                        { getMacroView() }
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