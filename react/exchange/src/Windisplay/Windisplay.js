import React from 'react';
import './Windisplay.css';
import InputGroup from 'react-bootstrap/InputGroup';
import FormControl from 'react-bootstrap/FormControl';
import Card from 'react-bootstrap/Card';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Button from 'react-bootstrap/Button';

const windisplay = (props, sendWinNotice, clearHandler) => {


    return (
        <Card bg="primary" text="white" style={{ width: '100%' }}>
            <Card.Body>
                <Card.Title>Process Win</Card.Title>
                <InputGroup className="mb-3">
                    <InputGroup.Prepend>
                        <InputGroup.Text id="basic-addon1">
                            <Button variant="success" onClick={sendWinNotice} size="sm">Send Win</Button>
                        </InputGroup.Text>
                    </InputGroup.Prepend>
                    <FormControl
                        value={props.nurl} 
                        disabled
                        id='winurl'/>
                </InputGroup>
                <Row>
                    <Col>
                        <textarea value={props.creative} rows="10" cols="49" disabled />
                    </Col>
                    <Col>
                        <textarea value={props.adm} rows="10" cols="49" disabled />
                    </Col>
                </Row>
                <Button variant="danger" onClick={clearHandler} size="sm">Clear</Button>
            </Card.Body>
        </Card>
    )
}

export default windisplay;