import React from 'react';
import './Windisplay.css';
import InputGroup from 'react-bootstrap/InputGroup';
import FormControl from 'react-bootstrap/FormControl';
import Card from 'react-bootstrap/Card';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Button from 'react-bootstrap/Button';

const windisplay = (props, sendWinNotice, clearHandler) => {

    const show = props.nurl == !('Win URL Will Appear Here' || props.nurl === '');

    return (
        <div>
            {show ? (
                <Card bg="primary" text="white" style={{ width: '100%' }} >
                    <Card.Body>
                        <Card.Title>Process Win&nbsp;
                    <Button variant="success" onClick={sendWinNotice} size="sm">Send Win</Button>
                        </Card.Title>
                        <InputGroup className="mb-3">
                            <FormControl
                                value={props.nurl}
                                disabled
                                id='winurl' />
                        </InputGroup>
                        <Row>
                            <Col md="6">
                                <textarea value={props.creative} rows="10" cols="45" disabled />
                            </Col>
                            <Col md="6">
                                <textarea value={props.adm} rows="10" cols="45" disabled />
                            </Col>
                        </Row>
                        <Button variant="danger" onClick={clearHandler} size="sm">Clear</Button>
                    </Card.Body>
                </Card>
            ) : (
                <Card bg="primary" text="white" style={{ width: '100%' }} >
                    <Card.Body>
                        <Card.Title>On Win, markup will appear here...</Card.Title>
                    </Card.Body>
                </Card>
            )}
        </div>
            
    );
}

export default windisplay;