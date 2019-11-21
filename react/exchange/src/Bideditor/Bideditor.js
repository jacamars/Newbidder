import React from 'react';
import "./Bideditor.css";
import JSONInput from 'react-json-editor-ajrm';
import locale from 'react-json-editor-ajrm/locale/en';
import Button from 'react-bootstrap/Button';
import Card from 'react-bootstrap/Card';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';


const bideditor = (props) => {

    const style = {
        backgroundColor: 'yellow',
        font: 'inherit',
        border: '4x solid blue',
        padding: '1px',
        cursor: 'pointer'

    }

    let list = props.vars.exchanges;
    let optionItems = list.map((exchange) =>
        <option key={exchange.name}>{exchange.name}</option>
    );

    let estyle = {
        overflow: 'scroll'
    }

    list = props.vars.bidTypes;
    let bidTypes = list.map((bid) =>
        <option key={bid.name}>{bid.name}</option>
    );

    return (
        <Card bg="primary" text="white" style={{ width: '100%' }}>
            <Card.Body>
                <Card.Title>Select Request Type &nbsp;
                <select style={style} onChange={props.bidTypeChangedHandler}>
                    {bidTypes}
                </select>
                &nbsp;   <Button variant="danger" onClick={props.sendBid} size="sm">Send Bid</Button>
                </Card.Title>
                <Row className="no-gutters">
                    <Col md="6">
                        <JSONInput
                            id='json_bid'
                            placeholder={props.vars.json}
                            theme='dark'
                            locale={locale}
                            height='260px'
                            width='500px'
                            onChange={props.jsonHandler}
                        />
                    </Col>
                    <Col md="6">
                        <textarea disabled value={props.response}
                            rows="10"
                            cols="45" />
                </Col>
                </Row>
            </Card.Body>
        </Card>
    )
}

export default bideditor;