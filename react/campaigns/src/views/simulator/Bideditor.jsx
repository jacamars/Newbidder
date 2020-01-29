import React from 'react';
import JSONInput from 'react-json-editor-ajrm';
import locale from 'react-json-editor-ajrm/locale/en';
import {
    Button,
    Card,
    CardHeader,
    Row,
    Col
 } from 'reactstrap';
 import { useViewContext } from "../../ViewContext";


const Bideditor = (props) => {

    const vx = useViewContext();

    const style = {
        backgroundColor: 'yellow',
        font: 'inherit',
        border: '4x solid blue',
        padding: '1px',
        cursor: 'pointer'

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
                <Button color="danger" onClick={props.sendBid} size="sm">Send Bid</Button>
                </Col>
                <Col xs="1">
                <Button color="success" onClick={props.clearHandler} size="sm">Clear</Button>
                </Col>
                </Row>

                <Row >
                    <Col xs="6">
                        <JSONInput
                            id='json_bid'
                            placeholder={props.vars.json}
                            theme='dark'
                            locale={locale}
                            height='266px'
                            width='95%'
                            onChange={props.jsonChangedHandler}
                        />
                    </Col>
                    <Col xs="6">
                        <JSONInput
                            id='json_response'
                            placeholder={props.vars.response}
                            theme='dark'
                            locale={locale}
                            height='266px'
                            width='95%'
                        />
                </Col>
                </Row>
        </Card>
    )
}

export default Bideditor;