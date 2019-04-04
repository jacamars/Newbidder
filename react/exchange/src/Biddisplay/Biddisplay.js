import React from 'react';
import "./Biddisplay.css";
const biddisplay = (props,clearHandler) => {
    return (
        <div className="Biddisplay">
         <textarea value={props.bid}/><textarea disabled value={props.response}/>
         <button onClick={clearHandler}>Clear</button>
        </div>
    )
}

export default biddisplay;