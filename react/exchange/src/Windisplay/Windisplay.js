import React from 'react';
import './Windisplay.css';
import InputGroup from 'react-bootstrap/InputGroup';
import FormControl from 'react-bootstrap/FormControl';
import Card from 'react-bootstrap/Card';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Button from 'react-bootstrap/Button';
import ReactPlayer from 'react-player'

const windisplay = (props) => {

    const textAreaStyle = {
        fontSize: 12
    };

    const pausedNotice = (e) =>{
        console.log("PAUSED: " + e);
    }

    console.log("WIN NURL: " + props.vars.nurl);
    console.log("ADM: " + props.vars.adm)
    const show = props.vars.nurl != ('Win URL Will Appear Here' || props.vars.nurl === '');

    return (
        <div>
            {show ? (
                <Card bg="primary" text="white" style={{ width: '100%' }} >
                    <Card.Body>
                        <Card.Title>Process Win&nbsp;
                    <Button variant="success" onClick={props.sendWinNotice} size="sm">Send Win</Button>
                        </Card.Title>
                        <InputGroup className="mb-3">
                            <FormControl
                                value={props.vars.nurl}
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
                                <div>
                                { props.vars.isVideo ? (
                                    <ReactPlayer 
                                        width='100%'
                                        height='265px'
                                        onPause={pausedNotice}
                                        url={props.vars.adm} playing />
                                ) : (
                                    <textarea value={props.vars.adm} rows="14" cols="65" style={textAreaStyle} disabled />
                                 ) }
                                </div>
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