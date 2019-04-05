import React from 'react';
import "./Biddisplay.css";
const biddisplay = (props,clearHandler) => {
    return (
        <div className="Biddisplay">
         <textarea value={props.bid} 
            rows="10" 
            cols="30"/>
        <textarea disabled value={props.response} 
            rows="10" 
            cols="30"/>
        <button onClick={clearHandler}>Clear</button>
        </div>
    )
}

export default biddisplay;