import React, { Component } from 'react';
import './App.css';
import Radium from 'radium';
import Exchanges from './Exchanges/Exchanges';
import Bideditor from './Bideditor/Bideditor';
import Windisplay from './Windisplay/Windisplay';
import Biddisplay from './Biddisplay/Biddisplay';


class App extends Component {
  state = {
    exchanges: [
      { name: 'Nexage', uri: '/rtb/bids/nexage'},
      { name: 'Mobfox', uri: '/rtb/bids/mobfox'},
      { name: 'Bidswitch', uri: '/rtb/bids/bidswitch'}
    ],
    json: { a:100, b:200, c: 300, d: 'this is a test'},
    selected: 'Nexage',
    uri: '/rtb/bids/nexage',
    url: 'http://localhost:8080',
    bid: 'I am the bid',
    response: 'I am the response',
    creative: 'Creative',
    adm: 'ADM',
    nurl: 'Win URL: '
  };

  exchangeChangedHandler = (event, id) => {
    const name = event.target.value;
    var uri = '?';
    for(var i in this.state.exchanges) {
      var exchange = this.state.exchanges[i]
        if (exchange.name === name) {
          uri = exchange.uri;
        }
    }

    this.setState({selected:name});
    this.setState({uri:uri});
  }

  jsonChangedHandler = (event, id) => {

  }

  rootHandler = (event, id) => {
    const newval = event.target.value;
    this.setState({url: newval});
  }

  composite = () => {
    return this.state.url + this.state.uri;
  }

  show = () => {
    alert(JSON.stringify(this.state))
    this.forceUpdate();
  };

  sendBid = (event,id) => {
    const endpoint = this.composite()
    const nbid = JSON.stringify(this.state.json,null,2);
    this.setState({bid:nbid});
  }

  sendWinNotice = (event,id) => {

  }

  brClearHandler = (event,id) => {
    this.setState({bid:''});
    this.setState({response:''});
  }

  wClearHandler = (event,id) => {
    this.setState({creative:''});
    this.setState({adm:''});
    this.setState({nurl: 'Win URL: '});
  }


  render() {

    let style = {
      backgroundColor: 'white',
      font: 'inherit',
      border: '4x solid blue',
      padding: '8px',
      cursor: 'pointer',
      ':hover': {
        backgroundColor: 'green',
        color: 'black'
      }
    };

    return (
      <div className="App">
        <header className="App-header">
          <p>Root: <input type="text" value={this.state.url} onChange={this.rootHandler} size='35'/>
            Endpoint: <input type="text" value={this.composite()} disabled size='35'/>
          </p>
          <p>
           {Exchanges(this.state, this.exchangeChangedHandler)}
          </p>   
          <p>
            {Bideditor(this.state,this.jsonChangedHandler, this.sendBid)}
          </p>
          <p>
            {Windisplay(this.state,this.sendWinNotice,this.wClearHandler)}
          </p>
          <p>
            {Biddisplay(this.state,this.brClearHandler)}
          </p>
          <p><button style={style} onClick={this.show}>Check</button></p>
        </header>
      </div>
    );
  }
}

export default Radium(App);
