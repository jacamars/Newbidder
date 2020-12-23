// React video.js, see here: https://docs.videojs.com/tutorial-react.html

import React, { useState} from 'react';
import ReactPlayer from 'react-player'
import ReactHtmlParser, { processNodes, convertNodeToElement, htmlparser2 } from 'react-html-parser';
import VideoPlayer from './VideoPlayer';
import "video.js/dist/video-js.css";
import useScript from '../../useScript';

var undef;

const DemoTag = (props) => {
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


    if (props.isVideo) {
        try {
            JSON.parse(props.adm);
        } catch  {
            return null
        }

        if (props.adm.indexOf("youtube")>-1) {
            return (<ReactPlayer 
                width='100%'
                height='265px'
                onPause={pausedNotice}
                url={props.adm} playing />);
            } else {
              var video = JSON.parse(props.adm);
              const videoJsOptions = {
                autoplay: true,
                controls: false,
                sources: [{
                src: video[0]
                }]}
                return <VideoPlayer { ...videoJsOptions } />
            }
    }
    else {
        output = output.replace(/\\"/g, "'");
        console.log("ADM OUTPUT: " + output);
        return( <div>{ ReactHtmlParser( output) }</div> );
    }
}

export default DemoTag;