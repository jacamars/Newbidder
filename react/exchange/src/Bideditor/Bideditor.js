import React from 'react';
import "./Bideditor.css";
import JSONInput from 'react-json-editor-ajrm';
import locale    from 'react-json-editor-ajrm/locale/en';
const bideditor = (props,bidTypeChangeHandler,exchangeHandler, jsonHandler, bidSender, restore) => {
    
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

      list = props.bidTypes;
      let bidTypes = list.map((bid) =>
                <option key={bid.name}>{bid.name}</option>
            );

    return (
        <div className="Bideditor">
        <table>
            <tr>
                <td>
        <p>
            Exchange:&nbsp;
            <select style={style} onChange={exchangeHandler}>
                {optionItems}
            </select>
            &nbsp;Type:&nbsp;
            <select style={style} onChange={bidTypeChangeHandler}>
                {bidTypes}
            </select>
        </p>
        </td>
        </tr>
        <tr>
            <td>
        <JSONInput
            id          = 'json_bid'
            placeholder = { props.json }
            theme       = 'light_mitsuketa_tribute'
            locale      = { locale }
            height      = '260px'
            width       = '500px'
            onChange    = {jsonHandler}
        />
        </td>
        <td>
         <textarea disabled value={props.response} 
            rows="17" 
            cols="60"/>
            </td>
        </tr>
        </table>
        </div>
    )
}

export default bideditor;