import React from 'react';
import "./Endpoint.css";

import Card from 'react-bootstrap/Card';

import InputGroup from 'react-bootstrap/InputGroup';
import FormControl from 'react-bootstrap/FormControl';

const endpoint = (props) => {

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
        <Card bg="primary" text="white" style={{ width: '100%' }}>
            <Card.Header>RTB4FREE Exchange Simulator</Card.Header>
            <Card.Body>
                <Card.Title>Set the Endpoint</Card.Title>
                <InputGroup className="mb-3">
                    <InputGroup.Prepend>
                        <InputGroup.Text id="basic-addon1">Root</InputGroup.Text>
                    </InputGroup.Prepend>
                    <FormControl
                        value={props.vars.url}
                        onChange={props.rootHandler}
                    />
                    <select style={style} onChange={props.exchangeHandler}>
                        {optionItems}
                    </select>
                    <InputGroup.Prepend>
                        <InputGroup.Text id="basic-addon1">Endpoint</InputGroup.Text>
                    </InputGroup.Prepend>
                    <FormControl
                        value={props.vars.url + props.vars.uri}
                        onChange={props.rootHandler}
                        disabled
                        id='endpoint'
                    />
                </InputGroup>
            </Card.Body>
        </Card>
    )
};

export default endpoint;