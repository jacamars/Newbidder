import React from 'react';
import "./Exchanges.css";

const exchanges = (props,changeHandler) => {

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
            
    return (
       <div className="Exchanges">
            Exchange:&nbsp;
        <select style={style} onChange={changeHandler}>
           {optionItems}
        </select>
        </div>
    )
}

export default exchanges;