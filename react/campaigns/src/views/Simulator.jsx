import React, { useState } from 'react';
import axios from 'axios';
import http from 'http';
import Endpoint from './simulator/Endpoint';
import Bideditor from './simulator/Bideditor';
import Windisplay from './simulator/Windisplay';
import { Logo, Tips, SampleBanner, SampleVideo, SampleAudio, SampleNative } from './simulator/Utils';
import { SSL_OP_LEGACY_SERVER_CONNECT } from 'constants';
import { useViewContext } from "../ViewContext";

const httpAgent = new http.Agent({ keepAlive: true });
const axiosInstance = axios.create({
  httpAgent,  // httpAgent: httpAgent -> for non es6 syntax
});
const cannedResponse = {"response": "will go here"};

let ssp = "Nexage";
var undef;

const Simulator = (props) =>  {

  const vx = useViewContext();

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
    json: vx.bidobject, //SampleBanner,
    uri: vx.uri,
    url: vx.url,
    bid: vx.bidvalue, 
    response: vx.bidresponse,
    adm: vx.adm,
    nurl: vx.nurl,
    winSent: vx.winsent,
    selectedBidType: vx.bidtype,
    xtime: vx.xtime,
    jsonError: false
  });

  const exchangeChangedHandler = (event, id) => {
    const name = event.target.value;
    var uri;
    for (var i in vars.exchanges) {
      var exchange = vars.exchanges[i]
      if (exchange.name === name) {
        vx.changeSsp(name);
        vx.changeUri(exchange.uri);
        uri = exchange.uri;
        ssp = name;
      }
    }

    vars.uri = uri;
    vars.url = vx.url;
    setVars(vars);

    console.log("NEW URI: " + vars.uri)
    redraw();
  }

  const redraw = () => {
    setCount(count + 1);
  }

  const bidTypeChangedHandler = (event, id) => {
    const name = event.target.value;

    if (name === "Video") {
      vars.isVideo = true;
    } else {
      vars.isVideo = false;
    }

    var file = '?';
    for (var i in vars.bidTypes) {
      var bt = vars.bidTypes[i]
      if (bt.name === name) {
        file = bt.file;
        vx.changeBidtype(bt.name);
        vx.changeBidresponse(cannedResponse);
      }
    }
    vars.response = cannedResponse;
    vars.selectedBidType = name;
    vars.json = copy(file);
    vars.bid = JSON.stringify(file, null, 2);


    vars.nurl = '';
    vars.adm = '';
    vars.creative = '';
    vars.winSent = false;
    vx.setWinSent = false;
    setVars(vars);


    redraw();
  }

  const copy = (obj) => {
    const s = JSON.stringify(obj);
    return JSON.parse(s);
  }

  const jsonChangedHandler = (obj) => {
    try {
      var x = eval('(' + obj.plainText+ ')');
      x = JSON.stringify(x,null,2);
      console.log("CHANGED: " + x);    
      vx.changeBidvalue(x);
      vars.bid = x;
    } catch (e) {
      // is an error but the editor will handle it.
    }
    vars.jsonError = obj.error;
    setVars(vars);
  }

  const rootHandler = (event, id) => {
    const newval = event.target.value;
    vars.url = newval;
    vx.changeUrl(newval);
    console.log("ROOTHANDLER: " + event.target.value)
    setVars(vars);
    redraw();
  }

  const composite = () => {
    return vars.state.url + vars.state.uri;
  }

  const show = () => {
    alert(JSON.stringify(this.state))
  };


  const sendBid = async  (event, id) => {

    wClearHandler();
    
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
    vars.adm = '';
    vars.creative = '';
    vars.winSent = false;
    vx.setWinSent = false;
    setVars(vars);
 
    var rtt =  performance.now();
    var xtime;
    
    try {
      const response = await axiosInstance.post(endpoint,bid);
      rtt = "rtt: " + (performance.now() - rtt);
      xtime = "xtime: " + response.headers['x-time'];
      vars.xtime = rtt + ", " + xtime;
      vx.changeXtime(rtt + ", " + xtime)
      if (response.status !== 200) {
        alert("NOBID: Response was: " + response.status + ", rtt: " + (performance.now()-rtt) + ", xtime: " + xtime);
        return;
      }
      //console.log("RESPONSE: " + JSON.stringify(response.data));
      vx.changeBidresponse(response.data);
      vars.nurl =  response.data.seatbid[0].bid[0].nurl;
      vx.changeNurl(vars.nurl);
      vars.response = response.data;
      vars.adm = response.data.seatbid[0].bid[0].adm;
      vars.creative = response.data.seatbid[0].bid[0].adm;
      vx.changeAdm(response.data.seatbid[0].bid[0].adm);
      setVars(vars);
      redraw();
    } catch (error) {
      vx.changeBidresponse({"oops": error});
      vars.nurl =  '';
      vars.response = {"oops": error};
      vars.adm = '';
      vars.creative = '';
      vars.winSent = false;
      vx.changeWinsent(false)
      setVars(vars);
      vx.changeXtime("rtt: 0, xtime: 0");
      vx.setAdm('');
      redraw();
      alert("ERROR: " + error + " " + endpoint);
    console.error(error);
  }
}

const sendPixel = async () => {
  var data = vx.adm; 
  // alert(data);
  var usesPixel = false;
  var i = data.indexOf("/pixel");
  if (i === -1) {
    i = data.indexOf("/callback?target=pixel");
    if (i === -1) {
      alert("ADM has no PIXEL FIRE");
      return;
    }
  } else {
    usesPixel = true;
  }

  var j = i;
  var q = undef;

  while(j>0 && q === undef) {
    if (j>0) 
      j--;
    if (data.charAt(j)==="'" || data.charAt(j)==='"')
      q = data.charAt(j);
  }

  if (j > 0)
    j++;
  
  var k = data.indexOf(q,i);
  k = k - j;

  var pixel=data.substr(j,k);
  pixel += "&debug=true"

  data = await vx.sendCallback(pixel);


}

  const sendWinNotice = async (event, id) => {
    var nurl = vars.nurl
    nurl = nurl.replace("${AUCTION_PRICE}", "1.23")
    vx.changeNurl(nurl);
    console.log("NURL: " + nurl)

    try {
      const response = await axiosInstance.get(nurl);
      console.log("RESPONSE: " + response.data);
      vars.winSent = true;
      vx.changeWinsent(true);
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
    vars.response = {};
    setVars(vars);

    redraw();
  }

  const wClearHandler = (event, id) => {
    vars.adm = '';
    vars.nurl = '';
    vars.response = cannedResponse;
    vx.changeAdm('');
    vx.changeNurl('');
    vx.changeBidresponse(cannedResponse);
    setVars(vars);

    redraw();
  }

    return (
        <>
            <div className="content">
              <Endpoint key={"ep-"+count} vars={vars} ssp={ssp} rootHandler={rootHandler} exchangeHandler={exchangeChangedHandler} />
              <Bideditor vars={vars} bidTypeChangedHandler={bidTypeChangedHandler}   clearHandler={wClearHandler} 
                jsonChangedHandler={jsonChangedHandler} sendBid={sendBid} restore={restore} />
              <Windisplay vars={vars} sendPixel={sendPixel} sendWinNotice={sendWinNotice} />
            </div>
        </>
    );
}


export default Simulator;