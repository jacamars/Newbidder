import React, { Component } from 'react';
import './App.css';
import Radium from 'radium';
import Bideditor from './Bideditor/Bideditor';
import Windisplay from './Windisplay/Windisplay';
import {Logo, Tips, SampleBanner, SampleVideo, SampleNative } from './Utils';


class App extends Component {
  state = {
    exchanges: [
      { name: 'Nexage', uri: '/rtb/bids/nexage'},
      { name: 'Mobfox', uri: '/rtb/bids/mobfox'},
      { name: 'Bidswitch', uri: '/rtb/bids/bidswitch'}
    ],
    json: SampleBanner,
    selected: 'Nexage',
    uri: '/rtb/bids/nexage',
    url: 'http://localhost:8080',
    bid: JSON.stringify(SampleBanner,null,2),
    response: 'I am the response',
    creative: '<a href="http://google.com">Click Here</a>',
    adm: 'ADM',
    nurl: 'Win URL: ',
    jsonError: false
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

  copy = (obj) => {
    const s = JSON.stringify(obj);
    return JSON.parse(s);
  }

  jsonChangedHandler = (obj) => {
      this.setState({ bid: obj.plainText });
      this.setState({jsonError: obj.error});
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
    if (this.state.jsonError !== false) {
      alert("Can't send, error at line " + this.state.jsonError.line + "\n" +
              this.state.jsonError.reason);
      return;
    }
    const endpoint = this.composite()
    fetch('https://facebook.github.io/react-native/movies.json')
        .then((response) => response.json())
        .then((responseJson) => {
          this.setState({response:JSON.stringify(responseJson.movies,null,2)});
        })
        .catch((error) => {
          alert("ERROR: " + error);
          console.error(error);
        });
  }


  sendWinNotice = (event,id) => {
    fetch('https://facebook.github.io/react-native/movies.json')
    .then((response) => response.json())
    .then((responseJson) => {
      alert(JSON.stringify(responseJson.movies,null,2));
    })
    .catch((error) => {
      alert("ERROR: " + error);
      console.error(error);
    });
  }

  brClearHandler = (event,id) => {
    this.setState({bid:''});
    this.setState({response:''});
  }

  wClearHandler = (event,id) => {
    this.setState({creative:''});
    this.setState({adm:''});
    this.setState({nurl: 'Win URL: '});

    alert(this.state.bid);
  }

  restore = (id) => {
    if (id === "banner") {
      this.setState({json:this.copy(SampleBanner), bid: JSON.stringify(SampleBanner)});
    } else
    if (id === "video") {
      this.setState({json:this.copy(SampleVideo),bid:JSON.stringify(SampleVideo,null,2)});
    } else
    if (id === "native") {
      this.setState({json:this.copy(SampleNative),bid:JSON.stringify(SampleNative,null,2)});
    }
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
        <table>
          <tr>
            <td>
              Root: <input type="text" value={this.state.url} onChange={this.rootHandler} size='35'/>
              Endpoint: <input type="text" value={this.composite()} disabled size='35'/>
            </td>
          </tr>
        </table>
          <p>
            {Bideditor(this.state,this.exchangeChangedHandler,
              this.jsonChangedHandler, this.sendBid,this.restore)}
              <br/>
          {Windisplay(this.state,this.sendWinNotice,this.wClearHandler)}
          </p>
          <Logo />
        </header>
      </div>
    );
  }
}

export default Radium(App);
