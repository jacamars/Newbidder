import React, { useState} from 'react';
import ReactPlayer from 'react-player'
import ReactHtmlParser, { processNodes, convertNodeToElement, htmlparser2 } from 'react-html-parser';
import useScript from '../../useScript';
import { useViewContext } from "../../ViewContext";

const DemoTag = (props) => {

    const vx = useViewContext();

    const myStyle = {
        fontSize: 12
    };


    const pausedNotice = (e) =>{
        console.log("PAUSED: " + e);
    }

    const sendEvent = () => {
        console.log("===============================");
    }

    let output = props.adm;

    if (props.isVideo)
        return (<ReactPlayer 
            width='100%'
            height='265px'
            onPause={pausedNotice}
        url={vx.macroSub(props.adm)} playing />);
    else 
        return( <div>{ ReactHtmlParser( vx.macroSub(props.adm) ) }</div> );
}

export default DemoTag;