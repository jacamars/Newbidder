import React from 'react';
import "./Bideditor.css";
const bideditor = (props,exchangeHandler, jsonHandler, bidSender, restore) => {
    
    const style = {
        backgroundColor: 'yellow',
        font: 'inherit',
        border: '4x solid blue',
        padding: '1px',
        cursor: 'pointer'
       
      }

      let list = props.exchanges;
        let optionItems = list.map((exchange) =>
                <option key={exchange.name}>{exchange.name}</option>
            );

            let estyle = {
                overflow:'scroll'
            }

    return (
        <div className="Bideditor">
        <p>
            Exchange:&nbsp;
            <select style={style} onChange={exchangeHandler}>
            {optionItems}
            </select>
            <button onClick={() => {restore("banner")}}>Banner</button>
            <button onClick={() => {restore("video")}}>Video</button>
            <button onClick={() => {restore("native")}}>Native</button>&nbsp;
            <button onClick={bidSender}>Send Bid</button>
        </p>
         <textarea value={props.bid} cols="50" rows="20" onChange={jsonHandler}></textarea>
         <textarea disabled value={props.response} 
            rows="20" 
            cols="30"/>
        </div>
    )
}

export default bideditor;