import React from 'react';
//import JSONInput from 'react-json-editor-ajrm';
//import locale from 'react-json-editor-ajrm/locale/en';
import ReactJson from 'react-json-view'
import {
    Button,
    Card,
    CardHeader,
    Row,
    Col,
    Collapse
 } from 'reactstrap';
 import { useViewContext } from "../../ViewContext";
 import MDEditor from '@uiw/react-md-editor';


const Bideditor = (props) => {

    const vx = useViewContext();

    const style = {
        backgroundColor: 'yellow',
        font: 'inherit',
        border: '4x solid blue',
        padding: '1px',
        cursor: 'pointer'

    }

    const edit = (e) => {
        props.jsonChangedHandler(e.updated_src);
        return true;
    }

    const del = (e) => {
        props.jsonChangedHandler(e.updated_src);
        return true;
    }

    const add = (e) => {
        props.jsonChangedHandler(e.updated_src);
        return true;
    }

    const collapse = (e) => {
        return true;
    }

    const textAreaStyle = {
        fontSize: 12
    };


    let estyle = {
        overflow: 'scroll'
    }

    let bidTypes = props.vars.bidTypes.map((bid) =>
        <option  selected={bid.name===vx.bidtype} key={bid.name}>{bid.name}</option>
    );

    return (
        <Card  text="white" style={{ width: '100%' }}>
        <CardHeader>
          <h5 className="title">Select Request Type</h5>
        </CardHeader>
                <Row>
                    <Col xs="auto">
                        <select style={style} onChange={props.bidTypeChangedHandler}>
                            {bidTypes}
                        </select>
                    </Col>
                <Col xs="1">
                    <Button color="danger" onClick={(e)=>props.sendBid()} size="sm">Bid</Button>
                </Col>
                <Col xs="1">
                    <Button color="warning" onClick={(e)=>props.sendBid('123')} size="sm">Test</Button>
                </Col>
                <Col xs="2">
                    <Button color="success" onClick={props.clearHandler} size="sm">Clear</Button>
                </Col>
                </Row>

                <Row >
                    <Col xs="5">
                        { props.vars.clipboard === false ?
                        <ReactJson
                            id='json_bid'
                            name="bidrequest"
                            src={props.vars.json}
                            sortKeys={true}
                            shouldCollapse={(e)=>{collapse(e)}}
                            displayDataTypes={false}
                            onEdit={(e)=>{edit(e)}}
                            onDelete={(e)=>{del(e)}}
                            onAdd={(e)=>{add(e)}}
                            enableClipboard={true}
                            theme='monokai'
                            height='266px'
                            width='95%'
                        />
                        :
                        <MDEditor
                        value={""}
                        commands={[]}
                        height={300}
                        preview="edit"
                        onChange={(e)=>props.fromClipboard(e)}
                      />
                        }
                    </Col>
                    <Col xs="7">
                        <ReactJson
                            id='json_response'
                            name="bidresponse"
                            src={props.vars.response}
                            displayDataTypes={false}
                            theme='monokai'
                            height='266px'
                            width='95%'
                        />
                </Col>
                </Row>
        </Card>
    )
}

export default Bideditor;