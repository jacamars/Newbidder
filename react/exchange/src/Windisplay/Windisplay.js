import React from 'react';
import './Windisplay.css';

const windisplay = (props, sendWinNotice, clearHandler) => {
    
    return (
        <div className="Windisplay">
         <p>
             <button onClick={sendWinNotice}>Send Win</button><br/>
             {props.nurl}
         </p>
         <textarea value={props.creative} disabled/><textarea value={props.adm} disabled/>
         <button onClick={clearHandler}>Clear</button>
        </div>
    )
}

export default windisplay;