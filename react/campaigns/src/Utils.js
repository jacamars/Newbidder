import React  from "react";

var undef;

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

const plcmtIds = [
    {e:0, value:"0", content:"Not specified."},
    {e:1, value:"1", content:"In the feed of content"},
    {e:2, value:"2", content:"In the atomic unit of the content."},
    {e:3, value:"3", content:"Outside the core content"},
    {e:4, value:"4", content:"Recommendation widget"},
];

const assetTypes = [
    {e:'0', value:"0", content:"None (Not recommended)."},
    {e:'1', value:"1", content:"Sponsored"},
    {e:'2', value:"2", content:"Descriptive text"},
    {e:'3', value:"3", content:"Rating, formatted as a number"},
    {e:'4', value:"4", content:"Likes, formatted as a string"},
    {e:'5', value:"5", content:"Downloads, formatted as a number"},  
    {e:'6', value:"6", content:"Price of product. Include currency symbol"}, 
    {e:'7', value:"7", content:"Saleprice, if on sale"}, 
    {e:'8', value:"8", content:"Phone number"}, 
    {e:'9', value:"9", content:"Address"}, 
    {e:'10', value:"10", content:"Additional descriptive text"}, 
    {e:'11', value:"11", content:"Displayurl"}, 
    {e:'12', value:"5", content:"Call to Action Button Text"}, 
];


export const uuidv4 = () => {
    return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
      (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
    );
  }

export const ssp = ['Nexage','Admedia', 'Adprudence', 'Appnexus', 'Adventurefeeds','Atomx','Axonix','Bidswitch','c1x', 
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

export const customerIds = (cids,value) => {
    var items = [];
    if (cids === undef) 
        return items;
    cids.map(row => {
        items.push(<option selected={row.customer_id === value} value={row.customer_id}>{row.customer_id}</option>);
    })
    return items;
}

export const placementType = (value) => {
    var items = [];
    if (value === undef) 
        value = 0;

    plcmtIds.map(row => {
        items.push(<option selected={row.e === value} value={row.value}>{row.content}</option>);
    });
    return items;
}

export const assetDataType = (value) => {
    var items = [];
    //alert("ASSET: " + value);
    if (value === undef) 
        value = 0;

    assetTypes.map(row => {
        items.push(<option selected={row.e === value} value={row.value}>{row.content}</option>);
    });
    return items;
}

export const contextType = (value) => {
    var items = [];
    if (!value) 
        return items;

    contextValues.map(row => {
        items.push(<option selected={row.e === value} value={row.value}>{row.content}</option>);
    });
    return items;
};

export const contextSubType = (value) => {
    var items = [];
    if (!value)
        return items;
        
        contextSubValues.map(row => {
            items.push(<option selected={row.e === value} value={row.value}>{row.content}</option>);
    });
    return items;
}

export const protocolOptions = (e) => {
    return(
        <>
        <option value="0" selected={0 === e}>None Specified</option>
        <option value="1" selected={1 === e}>VAST 1.0</option>
        <option value="2" selected={2 === e}>VAST 2.0</option>
        <option value="3" selected={3 === e}>VAST 3.0</option>
        <option value="4" selected={4 === e}>VAST 1.0 Wrapper</option>
        <option value="5" selected={5 === e}>VAST 2.0 Wrapper</option>
        <option value="6" selected={6 === e}>VAST 3.0 Wrapper</option>
        <option value="7" selected={7 === e}>VAST 4.0</option>
        <option value="8" selected={8 === e}>VAST 4.0 Wrapper</option>
        <option value="9" selected={9 === e}>DAAST 1.0</option>
        <option value="10" selected={10 === e}>DAAST 1.0 Wrapper</option>
        <option value="11" selected={11 === e}>VAST 4.1</option>
        <option value="12" selected={12 === e}>VAST 4.1 Wrapper</option>
        </>
    );
}

export const apiOptions = (e) => {
    return(
        <>

        <option value="0" selected={0 === e}>None Specified</option>
        <option value="1" selected={1 === e}>VPAID 1.0</option>
        <option value="2" selected={2 === e}>VPAID 2.0</option>
        <option value="3" selected={3 === e}>MRAID 1.0</option>
        <option value="4" selected={4 === e}>ORMMA</option>
        <option value="5" selected={5 === e}>MRAID 2.0</option>
        <option value="6" selected={6 === e}>MRAID 3.0</option>
        <option value="7" selected={7 === e}>OMID</option>

        </>
    );
}

export const attrOptions = (e) => {
    return (
        <>
        <option value="1" selected={e.indexOf(1) > -1}>Audio Ad (autoplay)</option>
        <option value="2" selected={e.indexOf(2) > -1}>Audio Ad (user-initiated)</option>
        <option value="3" selected={e.indexOf(3) > -1}>Expandable (automatic)</option>
        <option value="4" selected={e.indexOf(4) > -1}>Expandable (user initiated: Click)</option>
        <option value="5" selected={e.indexOf(5) > -1}>Expandable (rollver)</option>
        <option value="6" selected={e.indexOf(6) > -1}>In banner video ad (autoplay)</option>
        <option value="7" selected={e.indexOf(7) > -1}>In banner video ad (user-initiated)</option>
        <option value="8" selected={e.indexOf(8) > -1}>Pop (over, under, or on-exit)</option>
        <option value="9" selected={e.indexOf(9) > -1}>Provocative or suggestive imagery</option>
        <option value="10" selected={e.indexOf(10) > -1}>Shaky, Flashing, Flickering, Extreme Animation, Smileys</option>
        <option value="11" selected={e.indexOf(11) > -1}>Surveys</option>
        <option value="12" selected={e.indexOf(12) > -1}>Text only</option>
        <option value="13" selected={e.indexOf(13) > -1}>User interactive (e.g. embedded game)</option>
        <option value="14" selected={e.indexOf(14) > -1}>Windows dialog or alert style</option>
        <option value="15" selected={e.indexOf(15) > -1}>Has audio on/off button</option>
        <option value="16" selected={e.indexOf(16) > -1}>Ad provides a skip button</option>
        <option value="17" selected={e.indexOf(17) > -1}>Adobe Flash</option>


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
