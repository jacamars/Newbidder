import React from 'react';
import './DemoTag.css';

import InputGroup from 'react-bootstrap/InputGroup';
import FormControl from 'react-bootstrap/FormControl';
import Card from 'react-bootstrap/Card';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Button from 'react-bootstrap/Button';
import ReactPlayer from 'react-player'

const DemoTag = (props) => {

    const myStyle = {
        fontSize: 12
    };

    const pausedNotice = (e) =>{
        console.log("PAUSED: " + e);
    }


    let output =  <textarea value={props.adm} rows="14" cols="65" style={myStyle} disabled />;
    if (props.isVideo)
        output = 
        <ReactPlayer 
            width='100%'
            height='265px'
            onPause={pausedNotice}
            url={props.adm} playing />;

    return (
        <div>{output}</div>
    );
}

export default DemoTag;