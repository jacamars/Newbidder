import React  from "react";

const contextValues = [
    {e:1,value:"1",content:"Content-centric content"},
    {e:2,value:"2",content:"Social-centric content"},
    {e:3,value:"3",content:"Product-centric content"}
];

const contextSubValues = [
    {e:10, value:"10", content:"General or mixed content"},
    {e:11, value:"11", content:"Primary article"},
    {e:12, value:"12", content:"Primarily video content"},
    {e:13, value:"13", content:"Primarily audio content"},
    {e:14, value:"14", content:"Primarily image content"},
    {e:15, value:"15", content:"User-generated content"},
    {e:20, value:"20", content:"General social content"},
    {e:21, value:"21", content:"Primarily email content"},
    {e:22, value:"22", content:"Primarily chat/IM content"},
    {e:30, value:"30", content:"Content focused on selling products"},
    {e:31, value:"31", content:"Application store/marketplace"},
    {e:32, value:"32", content:"Product review site"}
];

export const ssp = ['Nexage', 'Bidswitch','Admedia', 'Adprudence', 'Appnexus', 'Adventurefeeds','Atomx','Axonix','Bidswitch','c1x', 
    'Cappture', 'Citenko','Epomx', 'Fyber', 'Gotham','Google', 'Index','Intango', 'Kadam', 'Medianexusnetwork', 'Mobfox', 'Openssp', 
    'Openx','Pokkt', 'Pubmatic', 'Republer', 'Smaato', 'Smartyads', 'Smartadserver', 'Spotx', 'Ssphwy','Stroer', 'Taggify', 'Tappx', 
    'Vdopia', 'Ventuno', 'Vertamedia','Waardx', 'Wideorbit' ];

export const deviceTypes = [
    "unknown",
    "mobile",
    "desktop",
    "smarttv",
    "phone",
    "tablet",
    "mobile-not(phone or tablet)"];

export const mimeTypes = (e) => { 
    return (
        <>
        <option selected={e === 'image/gif'}>image/gif</option>
        <option selected={e === 'image/png'}>image/png</option>
        <option selected={e === 'image/gif'}>image/jpg</option>
        <option selected={e === 'image/svg+xml'}>image/svg+xml</option>
        <option selected={e === 'application/javascript'}>application/javascript</option>
        <option selected={e === 'application/xml'}>application/xml</option>
        <option selected={e === 'audio/au'}>audio/au</option>
        <option selected={e === 'audio/basic'}>audio/basic</option>
        <option selected={e === 'audio/mid'}>audio/mid</option>
        <option selected={e === 'audio/mpeg'}>audio/mpeg</option>
        <option selected={e === 'audio/vorbis'}>audio/vorbis</option>
        <option selected={e === 'audio/x-aiff'}>audio/x-aiff</option>
        <option selected={e === 'audio/x-mpegurl'}>audio/mpeg</option>
        <option selected={e === 'audio/x-pn-realaudio'}>audio/x-pn-realaudio</option>
        <option selected={e === 'audio/x-wav'}>audio/x-wav</option>
        <option selected={e === 'text/css'}>text/css</option>
        <option selected={e === 'text/plain'}>text/plain</option>
        <option selected={e === 'video/avi'}>video/avi</option>
        <option selected={e === 'video/mp4'}>video/mp4</option>
        <option selected={e === 'video/pgg'}>video/ogg</option>
        </>
    );
}

export const contextType = (e) => {
    var items = [];
    contextValues.map(row => {
        items.push(<option selected={e.indexOf(row.e) !== -1} value={row.value}>{row.content}</option>);
    });
    return items;
};

export const contextSubType = (e) => {
    var items = [];
        contextSubValues.map(row => {
            items.push(<option selected={e.indexOf(row.e) !== -1} value={row.value}>{row.content}</option>);
    });
    return items;
}

export const protocolOptions = (e) => {
    return(
        <>
        <option value="1" selected={1 === e}>VAST 1.0</option>
        <option value="2" selected={2 === e}>VAST 2.0</option>
        <option value="3" selected={3 === e}>VAST 3.0</option>
        <option value="4" selected={4 === e}>VAST 1.0 Wrapper</option>
        <option value="5" selected={5 === e}>VAST 2.0 Wrapper</option>
        <option value="6" selected={6 === e}>VAST 3.0 Wrapper</option>
        <option value="7" selected={7 === e}>VAST 4.0</option>
        <option value="8" selected={8 === e}>VAST 4.0 Wrapper</option>
        <option value="9" selected={9 === e}>DAAST 1.0</option>
        <option value="9" selected={10 === e}>DAAST 1.0 Wrapper</option>
        </>
    );
}

/*
*/

export const fromCommaList = (str) => {
    if (!str)
        return "";
    return str.split(",").join("\n");
}

export const asTextAreaList = (list) => {
    var str = "";
    if (!list)
      return str;
    return list.join();
}

export const getTrueFalseOptions = (value)  =>{
    if (value === true) {
        return(
            <>
            <option>true</option>
            <option>false</option>
            </>
        );
    }
    return(
        <>
        <option>true</option>
        <option>false</option> 
    </>);
}
