import React, { Component } from 'react';
import './App.css';
import Radium from 'radium';
import Endpoint from './Endpoint/Endpoint';
import Bideditor from './Bideditor/Bideditor';
import Windisplay from './Windisplay/Windisplay';
import { Logo, Tips, SampleBanner, SampleVideo, SampleAudio, SampleNative } from './Utils';

import Container from 'react-bootstrap/Container';

class App extends Component {
  state = {
    exchanges: [
      { name: 'Nexage', uri: '/rtb/bids/nexage' },
      { name: 'Mobfox', uri: '/rtb/bids/mobfox' },
      { name: 'Bidswitch', uri: '/rtb/bids/bidswitch' }
    ],
    bidTypes: [
      { name: "Banner", file: SampleBanner },
      { name: "Video", file: SampleVideo },
      { name: "Audio", file: SampleAudio },
      { name: "Native", file: SampleNative }
    ],
    json: SampleBanner,
    selected: 'Nexage',
    uri: '/rtb/bids/nexage',
    url: 'http://localhost:8080',
    bid: JSON.stringify(SampleBanner, null, 2),
    response: 'I am the response',
    creative: '<a href="http://google.com">Click Here</a>',
    adm: 'ADM',
    nurl: 'Win URL Will Appear Here ',
    selectedBidType: 'Banner',
    jsonError: false
  };

  exchangeChangedHandler = (event, id) => {
    const name = event.target.value;
    var uri = '?';
    for (var i in this.state.exchanges) {
      var exchange = this.state.exchanges[i]
      if (exchange.name === name) {
        uri = exchange.uri;
      }
    }

    this.setState({ selected: name });
    this.setState({ uri: uri });

    console.log("NEW URI: " + uri)
  }

  bidTypeChangedHandler = (event, id) => {
    const name = event.target.value;
    var file = '?';
    for (var i in this.state.bidTypes) {
      var bt = this.state.bidTypes[i]
      if (bt.name === name) {
        file = bt.file;
      }
    }

    this.setState({ selectedBidType: name });
    this.setState({ json: this.copy(file), bid: JSON.stringify(file, null, 2) });
  }

  copy = (obj) => {
    const s = JSON.stringify(obj);
    return JSON.parse(s);
  }

  jsonChangedHandler = (obj) => {
    this.setState({ bid: obj.plainText });
    this.setState({ jsonError: obj.error });
  }

  rootHandler = (event, id) => {
    const newval = event.target.value;
    this.setState({ url: newval });
  }

  composite = () => {
    return this.state.url + this.state.uri;
  }

  show = () => {
    alert(JSON.stringify(this.state))
    this.forceUpdate();
  };

  sendBid = (event, id) => {
    if (this.state.jsonError !== false) {
      alert("Can't send, error at line " + this.state.jsonError.line + "\n" +
        this.state.jsonError.reason);
      return;
    }
    const endpoint = document.getElementById('endpoint').value;
    var bid = this.state.bid
    bid = JSON.stringify(JSON.parse(bid))
    console.log("BID is: " + bid)

    this.setState({ nurl: '' })
    this.setState({ response: '' })
    this.setState({ adm: '' })
    this.setState({ creative: '' })

    fetch(endpoint, {
      method: 'post',
      body: bid
    })
      .then((response) => {
        if (response.status === 200) {
          for (var pair of response.headers.entries()) {
            console.log(pair[0] + ': ' + pair[1]);
          }
          return response.json()
        }
        else
          alert("NOBID: Response was: " + response.status)
        return null
      })
      .then((responseJson) => {
        if (responseJson === null)
          return

        console.log("RESPONSE: " + JSON.stringify(responseJson, null, 2));
        this.setState({ nurl: responseJson.seatbid[0].bid[0].nurl });
        this.setState({ response: JSON.stringify(responseJson, null, 2) });
        this.setState({ adm: responseJson.seatbid[0].bid[0].adm });
        this.setState({ creative: responseJson.seatbid[0].bid[0].adm });
      })
      .catch((error) => {
        alert("ERROR: " + error + " " + endpoint);
        console.error(error);
      });

  }


  sendWinNotice = (event, id) => {
    var nurl = this.state.nurl
    nurl = nurl.replace("${AUCTION_PRICE}", "1.23")
    console.log("NURL: " + nurl)
    fetch(nurl)
      .then((response) => response.text())
      .then((responseText) => {
        alert("Text is: " + responseText)
      })
      .catch((error) => {
        alert("ERROR: " + error);
        console.error(error);
      });
  }

  brClearHandler = (event, id) => {
    this.setState({ bid: '' });
    this.setState({ response: '' });
  }

  wClearHandler = (event, id) => {
    this.setState({ creative: '' });
    this.setState({ adm: '' });
    this.setState({ nurl: 'Win URL Will Appear Here' });
    this.setState({ response: '' });

    alert(this.state.bid);
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
      <Container>
        {Endpoint(this.state, this.rootHandler, this.exchangeChangedHandler)}
        {Bideditor(this.state, this.bidTypeChangedHandler,
          this.jsonChangedHandler, this.sendBid, this.restore)}
        {Windisplay(this.state, this.sendWinNotice, this.wClearHandler)}
      </Container>
    );
  }
}

export default Radium(App);
