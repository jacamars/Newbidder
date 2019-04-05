import React from 'react';
import './Windisplay.css';

const windisplay = (props, sendWinNotice, clearHandler) => {

    return (
        <div className="Windisplay">
            <p>
             <button onClick={sendWinNotice}>Send Win</button>&nbsp;
             {props.nurl}
            </p>
         <textarea value={props.creative} rows="10" cols="60" disabled/><textarea value={props.adm} rows="10" cols="60" disabled/>
         <button onClick={clearHandler}>Clear</button>
        </div>
    )
}

export default windisplay;