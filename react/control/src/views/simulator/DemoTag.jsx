import React from 'react';
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