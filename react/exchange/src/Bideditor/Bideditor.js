import React from 'react';
import "./Bideditor.css";
const bideditor = (props,changeHandler, bidSender) => {
    
    return (
        <div className="Bideditor">
            <p>
                <button>Banner</button><button>Video</button><button>Native</button>
            </p>
            <p>
                <button onClick={bidSender}>Send Bid</button>
            </p>
         <textarea>Bid editor goes here</textarea>
        </div>
    )
}

export default bideditor;