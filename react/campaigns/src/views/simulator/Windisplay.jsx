import React, { useState } from 'react';
import {
    Input,
    Card,
    CardHeader,
    Row,
    Col,
    Button
} from "reactstrap";

import DemoTag from './DemoTag';

var undef;

const Windisplay = (props) => {

    const [showPixelFire, setShowPixelFire] = useState(false);
    
    const textAreaStyle = {
        fontSize: 12
    };

    console.log("WIN NURL: " + props.vars.nurl);
    console.log("ADM: " + props.vars.adm);
    const show = !(props.vars.nurl === '');
    console.log("SHOW: " + show);

    const sendWinNotice = () => {
        props.sendWinNotice();
        setShowPixelFire(true);
    }

    return (
        <div>
            {show ? (
                <Card  text="white" style={{ width: '100%' }}>
                    <CardHeader>
                        <h5 className="title">Process Win</h5>
                   </CardHeader>
                   <Row>
                       <Col xs="1">
                            <Button color="success" onClick={(e) => sendWinNotice()} size="sm">Send Win</Button>
                       </Col>
                       { showPixelFire &&
                          <Col xs="1">
                            <Button color="warning" onClick={props.sendPixel} size="sm">Fire Pixel</Button>
                           </Col>
                        }
                        <Col xs="1">
                         {' '}   
                        </Col>
                        <Col xs="2">
                            {props.vars.xtime}
                       </Col>
                   </Row>
                    <Row>
                        <Input
                                value={props.vars.nurl}
                                style={textAreaStyle}
                                id='winurl' />
                    </Row>
                        <Row>
                            <Col xs="6">
                                <textarea 
                                    style={textAreaStyle}
                                    value={props.vars.adm} 
                                    rows="14" cols="65" disabled />
                            </Col>
                            <Col xs="6">
                                { props.vars.winSent &&
                                  <DemoTag isVideo={props.vars.isVideo} adm={props.vars.adm} />
                                }
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

export default Windisplay;