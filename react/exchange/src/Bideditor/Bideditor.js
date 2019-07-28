import React from 'react';
import "./Bideditor.css";
import JSONInput from 'react-json-editor-ajrm';
import locale from 'react-json-editor-ajrm/locale/en';
import InputGroup from 'react-bootstrap/InputGroup';
import FormControl from 'react-bootstrap/FormControl';
import Card from 'react-bootstrap/Card';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';


const bideditor = (props, bidTypeChangeHandler, jsonHandler, bidSender, restore) => {

    const style = {
        backgroundColor: 'yellow',
        font: 'inherit',
        border: '4x solid blue',
        padding: '1px',
        cursor: 'pointer'

    }

    let list = props.exchanges;
    let optionItems = list.map((exchange) =>
        <option key={exchange.name}>{exchange.name}</option>
    );

    let estyle = {
        overflow: 'scroll'
    }

    list = props.bidTypes;
    let bidTypes = list.map((bid) =>
        <option key={bid.name}>{bid.name}</option>
    );

    return (
        <Card bg="primary" text="white" style={{ width: '100%' }}>
            <Card.Body>
                <Card.Title>Select Request Type &nbsp;
                <select style={style} onChange={bidTypeChangeHandler}>
                    {bidTypes}
                </select>
                </Card.Title>
                <Row>
                    <Col>
                        <JSONInput
                            id='json_bid'
                            placeholder={props.json}
                            theme='light_mitsuketa_tribute'
                            locale={locale}
                            height='260px'
                            width='500px'
                            onChange={jsonHandler}
                        />
                    </Col>
                    <Col>
                        <textarea disabled value={props.response}
                            rows="10"
                            cols="50" />
                </Col>
                </Row>
            </Card.Body>
        </Card>
    )
}

export default bideditor;