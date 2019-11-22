import React from 'react';
import './Windisplay.css';
import InputGroup from 'react-bootstrap/InputGroup';
import FormControl from 'react-bootstrap/FormControl';
import Card from 'react-bootstrap/Card';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Button from 'react-bootstrap/Button';
import ReactPlayer from 'react-player'
import DemoTag from '../DemoTag/DemoTag';

const windisplay = (props) => {

    const textAreaStyle = {
        fontSize: 12
    };

    console.log("WIN NURL: " + props.vars.nurl);
    console.log("ADM: " + props.vars.adm);
    console.log("VIDEO: " + props.vars.isVideo);
    const show = !(props.vars.nurl === 'Win URL Will Appear Here' || props.vars.nurl === '');
    console.log("SHOW: " + show);

    return (
        <div>
            {show ? (
                <Card bg="primary" text="white" style={{ width: '100%' }} >
                    <Card.Body>
                        <Card.Title>Process Win&nbsp;
                            <Button variant="success" onClick={props.sendWinNotice} size="sm">Send Win</Button>
                            &nbsp;
                            {props.vars.xtime}
                        </Card.Title>
                        <InputGroup className="mb-3">
                            <FormControl
                                value={props.vars.nurl}
                                style={textAreaStyle}
                                disabled
                                id='winurl' />
                        </InputGroup>
                        <Row>
                            <Col md="6">
                                <textarea 
                                    style={textAreaStyle}
                                    value={props.vars.creative} 
                                    rows="14" cols="65" disabled />
                            </Col>
                            <Col md="6">
                                <DemoTag isVideo={props.vars.isVideo} adm={props.vars.adm} />
                            </Col>
                        </Row>
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