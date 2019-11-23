import React from 'react';

import {
    Card,
    Col,
    Row,
    CardHeader,
    Input,
    InputGroup,
    InputGroupAddon,
    InputGroupText
 } from 'reactstrap';

const Endpoint = (props) => {

    const style = {
        backgroundColor: 'yellow',
        font: 'inherit',
        border: '4x solid blue',
        padding: '1px',
        cursor: 'pointer'

    }

    const optionItems = props.vars.exchanges.map((exchange) =>
        <option key={exchange.name}>{exchange.name}</option>
    );

    let estyle = {
        overflow: 'scroll'
    }

    let composite = props.url + props.uri

    console.log("ENDPOINT: " + props.vars.uri);

    return (
        <Card  text="white" style={{ width: '100%' }}>
        <CardHeader>
          <h5 className="title">Send Bids/Wins to RTB Server</h5>
        </CardHeader>
        <Row>
            <Col xs="3">
                <InputGroup>
                    <InputGroupAddon addonType="prepend">
                        <InputGroupText>Root</InputGroupText>
                    </InputGroupAddon>
                    <Input value={props.vars.url} onChange={props.rootHandler} />
                </InputGroup>
            </Col>
            <Col xs="2">
                <select style={style} onChange={props.exchangeHandler} width='100%'>
                    {optionItems}
                </select>
            </Col>
            <Col xs="auto">
                <InputGroup>
                    <InputGroupAddon addonType="prepend">
                        <InputGroupText>Endpoint</InputGroupText>
                    </InputGroupAddon>
                    <Input
                        value={props.vars.url + props.vars.uri}
                        onChange={props.rootHandler}
                        id='endpoint' />
                </InputGroup>
            </Col>
        </Row>
    </Card>
    );
};

export default Endpoint;