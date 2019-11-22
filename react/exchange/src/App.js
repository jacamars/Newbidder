import React, { useState } from 'react';
import axios from 'axios';
import http from 'http';
import './App.css';
import Endpoint from './Endpoint/Endpoint';
import Bideditor from './Bideditor/Bideditor';
import Windisplay from './Windisplay/Windisplay';
import { Logo, Tips, SampleBanner, SampleVideo, SampleAudio, SampleNative } from './Utils';
import Container from 'react-bootstrap/Container';

const httpAgent = new http.Agent({ keepAlive: true });
const axiosInstance = axios.create({
  httpAgent,  // httpAgent: httpAgent -> for non es6 syntax
});

const App = () =>  {
  
  const [count, setCount] = useState(0);

  const [vars, setVars] = useState({
    exchanges: [
      { name: 'Nexage', uri: '/rtb/bids/nexage' },
      { name: 'Bidswitch', uri: '/rtb/bids/bidswitch' },
      { name: 'Admedia', uri: '/rtb/bids/admedia'},
      { name: 'Adprudence', uri: '/rtb/bids/adprudence' },
      { name: 'Appnexus', uri: '/rtb/bids/appnexus' },
      { name: 'Adventurefeeds', uri: '/rtb/bids/adventurefeeds' },
      { name: 'Atomx', uri: '/rtb/bids/atomx' },
      { name: 'Axonix', uri: '/rtb/bids/axionix' },
      { name: 'Bidswitch', uri: '/rtb/bids/bidswitch' },
      { name: 'c1x', uri: '/rtb/bids/c1x' },
      { name: 'Cappture', uri: '/rtb/bids/cappture' },
      { name: 'Citenko', uri: '/rtb/bids/citenko' },
      { name: 'Epomx', uri: '/rtb/bids/epomx' },
      { name: 'Fyber', uri: '/rtb/bids/fyber' },
      { name: 'Gotham', uri: '/rtb/bids/gotham' },
      { name: 'Google', uri: '/rtb/bids/google' },
      { name: 'Index', uri: '/rtb/bids/index' },
      { name: 'Intango', uri: '/rtb/bids/intango' },
      { name: 'Kadam', uri: '/rtb/bids/kaddam' },
      { name: 'Medianexusnetwork', uri: '/rtb/bids/medianexusnetwork' },
      { name: 'Mobfox', uri: '/rtb/bids/mobfox' },
      { name: 'Openssp', uri: '/rtb/bids/openssp' },
      { name: 'Openx', uri: '/rtb/bids/openx' },
      { name: 'Pokkt', uri: '/rtb/bids/pookt' },
      { name: 'Pubmatic', uri: '/rtb/bids/pubmatic' },
      { name: 'Republer', uri: '/rtb/bids/republer' },
      { name: 'Smaato', uri: '/rtb/bids/smaato' },
      { name: 'Smartyads', uri: '/rtb/bids/smartyads' },
      { name: 'Smartadserver', uri: '/rtb/bids/smartadserver' },
      { name: 'Spotx', uri: '/rtb/bids/spotx' },
      { name: 'Ssphwy', uri: '/rtb/bids/ssphwy' },
      { name: 'Stroer', uri: '/rtb/bids/stroer' },
      { name: 'Taggify', uri: '/rtb/bids/taggify' },
      { name: 'Tappx', uri: '/rtb/bids/tappx' },
      { name: 'Vdopia', uri: '/rtb/bids/vdopia' },
      { name: 'Ventuno', uri: '/rtb/bids/ventuno' },
      { name: 'Vertamedia', uri: '/rtb/bids/vertamedia' },
      { name: 'Waardx', uri: '/rtb/bids/waardx' },
      { name: 'Wideorbit', uri: '/rtb/bids/wideorbit' }
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
    xtime: 'xtime: 0, rtt: 0',
    isVideo: false,
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
    var x = eval('(' + obj.plainText+ ')');
    x = JSON.stringify(x,null,2);
    console.log("CHANGED: " + x);    

    vars.bid = x;
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


  const sendBid = async  (event, id) => {
    console.log("SENDING A BID");
    if (vars.jsonError !== false) {
      alert("Can't send, error at line " + vars.jsonError.line + "\n" +
        vars.jsonError.reason);
      return;
    }
    const endpoint = document.getElementById('endpoint').value;

    var bid = vars.bid
    bid = JSON.stringify(JSON.parse(bid))
    console.log("THE BID IS: " + bid);

    vars.nurl = '';
    vars.response = '';
    vars.adm = '';
    vars.creative = '';
    vars.isVideo=false;
    setVars(vars);
 
    var rtt =  performance.now();
    var xtime;
    
    try {
      const response = await axiosInstance.post(endpoint,bid);
      rtt = "rtt: " + (performance.now() - rtt);
      xtime = "xtime: " + response.headers['x-time'];
      vars.xtime = rtt + ", " + xtime;
      if (response.status !== 200) {
        alert("NOBID: Response was: " + response.status + ", rtt: " + (performance.now()-rtt) + ", xtime: " + xtime);
        return;
      }
      console.log("RESPONSE: " + JSON.stringify(response.data));
      vars.nurl =  response.data.seatbid[0].bid[0].nurl;
      vars.response = JSON.stringify(response.data, null, 2);
      vars.adm = response.data.seatbid[0].bid[0].adm;
      vars.creative = response.data.seatbid[0].bid[0].adm;

      setVars(vars);
      redraw();
    } catch (error) {
      vars.nurl =  '';
      vars.response = '';
      vars.adm = '';
      vars.creative = '';
      setVars(vars);
      redraw();
      alert("ERROR: " + error + " " + endpoint);
    console.error(error);
  }
}

  const sendWinNotice = async (event, id) => {
    var nurl = vars.nurl
    nurl = nurl.replace("${AUCTION_PRICE}", "1.23")
    console.log("NURL: " + nurl)

    try {
      const response = await axiosInstance.get(nurl);
      console.log("RESPONSE: " + response.data);
      vars.isVideo = nurl.indexOf("Video") > -1;
      setVars(vars);
      redraw();
    } catch (error) {
      alert("ERROR: " + error);
    }
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

    return (
      <div>
      <Container>
        <Endpoint vars={vars} rootHandler={rootHandler} exchangeHandler={exchangeChangedHandler} />
        <Bideditor vars={vars} bidTypeChangedHandler={bidTypeChangedHandler}   clearHandler={wClearHandler} 
          jsonChangedHandler={jsonChangedHandler} sendBid={sendBid} restore={restore} />
        <Windisplay vars={vars} sendWinNotice={sendWinNotice} />
      </Container>
      </div>
    );
}

export default App;
