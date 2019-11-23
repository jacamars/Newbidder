import React from 'react';
import {
    Input,
    Card,
    CardHeader,
    Row,
    Col,
    Button
} from "reactstrap";

import DemoTag from './DemoTag';

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
                <Card  text="white" style={{ width: '100%' }}>
                    <CardHeader>
                        <h5 className="title">Process Win</h5>
                   </CardHeader>
                   <Row>
                       <Col>
                            <Button color="success" onClick={props.sendWinNotice} size="sm">Send Win</Button>
                        </Col>
                        <Col>
                            {props.vars.xtime}
                       </Col>
                   </Row>
                    <Row>
                        <Input
                                value={props.vars.nurl}
                                style={textAreaStyle}
                                disabled
                                id='winurl' />
                    </Row>
                        <Row>
                            <Col xs="6">
                                <textarea 
                                    style={textAreaStyle}
                                    value={props.vars.creative} 
                                    rows="14" cols="65" disabled />
                            </Col>
                            <Col xs="6">
                                <DemoTag isVideo={props.vars.isVideo} adm={props.vars.adm} />
                            </Col>
                        </Row>
                </Card>
            ) : (
                <Card  text="white" style={{ width: '100%' }}>
                    <CardHeader>
                        <h5 className="title">On Win, markup will appear here...</h5>
                   </CardHeader>
                </Card>
            )}
        </div>
            
    );
}

export default windisplay;