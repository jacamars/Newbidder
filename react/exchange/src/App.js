import React, { useState } from 'react';
import './App.css';
import Endpoint from './Endpoint/Endpoint';
import Bideditor from './Bideditor/Bideditor';
import Windisplay from './Windisplay/Windisplay';
import { Logo, Tips, SampleBanner, SampleVideo, SampleAudio, SampleNative } from './Utils';

import Container from 'react-bootstrap/Container';

const App = () =>  {
  
  const [count, setCount] = useState(0);

  const [vars, setVars] = useState({
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
    nurl: 'Win URL Will Appear Here',
    selectedBidType: 'Banner',
    jsonError: false
  });

  const exchangeChangedHandler = (event, id) => {
    const name = event.target.value;
    var uri = '?';
    for (var i in vars.exchanges) {
      var exchange = vars.exchanges[i]
      if (exchange.name === name) {
        uri = exchange.uri;
      }
    }

    vars.selected = name;
    vars.uri = uri;
    setVars(vars);

    console.log("NEW URI: " + vars.uri)
    redraw();
  }

  const redraw = () => {
    setCount(count + 1);
  }

  const bidTypeChangedHandler = (event, id) => {
    const name = event.target.value;
    var file = '?';
    for (var i in vars.bidTypes) {
      var bt = vars.bidTypes[i]
      if (bt.name === name) {
        file = bt.file;
      }
    }

    vars.selectedBidType = name;
    vars.json = copy(file);
    vars.bid = JSON.stringify(file, null, 2);
    setVars(vars);

    redraw();
  }

  const copy = (obj) => {
    const s = JSON.stringify(obj);
    return JSON.parse(s);
  }

  const jsonChangedHandler = (obj) => {
    vars.bid = obj.plainText;
    vars.jsonError = obj.error;
    setVars(vars);
  }

  const rootHandler = (event, id) => {
    const newval = event.target.value;
    vars.url = newval;
    setVars(vars);
  }

  const composite = () => {
    return vars.state.url + vars.state.uri;
  }

  const show = () => {
    alert(JSON.stringify(this.state))
  };

  const sendBid = (event, id) => {
    console.log("SENDING A BID");
    if (vars.jsonError !== false) {
      alert("Can't send, error at line " + vars.jsonError.line + "\n" +
        vars.jsonError.reason);
      return;
    }
    const endpoint = document.getElementById('endpoint').value;
    var bid = vars.bid
    bid = JSON.stringify(JSON.parse(bid))
    console.log("BID is: " + bid)

    vars.nurl = '';
    vars.response = '';
    vars.adm = '';
    vars.creative = '';

    setVars(vars);


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
        vars.nurl =  responseJson.seatbid[0].bid[0].nurl;
        vars.response = JSON.stringify(responseJson, null, 2)
        vars.adm = responseJson.seatbid[0].bid[0].adm;
        vars.creative = responseJson.seatbid[0].bid[0].adm;

        setVars(vars);
      })
      .catch((error) => {
        alert("ERROR: " + error + " " + endpoint);
        console.error(error);
      });

  }


  const sendWinNotice = (event, id) => {
    var nurl = vars.nurl
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

  const restore = () => {

  }

  const brClearHandler = (event, id) => {
    vars.bid = '';
    vars.response = '';
    setVars(vars);

    redraw();
  }

  const wClearHandler = (event, id) => {
    vars.creative = '';
    vars.adm = '';
    vars.nurl = 'Win URL Will Appear Here';
    vars.response = '';
    setVars(vars);

    redraw();
  }

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
      <div>
      <Container>
        <Endpoint vars={vars} rootHandler={rootHandler} exchangeHandler={exchangeChangedHandler} />
        <Bideditor vars={vars} bidTypeChangedHandler={bidTypeChangedHandler}
          jsonChangedHandler={jsonChangedHandler} sendBid={sendBid} restore={restore} />
        <Windisplay vars={vars} sendWinNotice={sendWinNotice}  wClearHandler={wClearHandler} />
      </Container>
      </div>
    );
}

export default App;
