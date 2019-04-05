import React from "react";

export const SampleBanner = {
  "id": "35c22289-06e2-48e9-a0cd-94aeb79fab43",
	"Xid": "123",
	"at": 2,
	"imp": [{
		"id": "1",
		"instl": 0,
		"banner": {
			"h": 50,
			"w": 320,
			"mimes": ["image/jpg", "text/javascript" ],
			"pos": 0
		},
		"ext": {
			"nex_screen": 0
		}
	}],
	"site": {
		"id": "99201",
		"name": "rtb4free",
		"domain": "junk1.com",
		"cat": [
			"IAB1", "IAB2", "IAB3"
		],
		"keywords": "radiation",
		"page": "http://www.nexage.com",
		"ref": "http://www.iab.net",
		"search": "radiation",
		"publisher": {
			"id": "98401",
			"name": "RTB Bidder Integration Test Publisher"
		},
		"ext": {
			"nex_coppa": 0
		}
	},
	"device": {
		"didsha1": "132079238ec783b0b89dff308e1f9bdd08576273",
		"dpidsha1": "f22711a823044bb9ce7ace097955de0286eb0182",
		"ip": "166.137.138.18",
		"carrier": "ATT",
		"ua": "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_2_1 like Mac OS X; el-gr) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8C148 Safari/6533.18.5",
		"make": "Apple",
		"model": "iPhone",
		"osv": "3.1.2",
		"connectiontype": 3,
		"devicetype": 1,
		"geo": {
			"lat": 42.378,
			"lon": -71.227,
			"country": "USA"
		}
	},
	"user": {
		"id": "ASDFJKL",
		"yob": 1961,
		"gender": "F",
		"keywords": "sports",
		"geo": {
			"country": "USA",
			"city": "Waltham",
			"zip": "02451",
			"region": "MA",
			"type": 3
		},
		"ext": {
			"nex_eth": "4",
			"nex_marital": "M",
			"nex_kids": "N",
			"nex_hhi": 75000,
			"nex_dma": "Boston"
		}
	},
	"ext": {
		"coppa": 0,
		"udi": {
			"googleadid": "5e2efab6-7721-4cfe-b542-97084d5aa62f",
			"googlednt": 0,
			"atuid": "a90377ab-190b-1036-f424-ac10fdb8ffef"
		},
		"operaminibrowser": 0,
		"carriername": "Verizon Wireless"
	}
};

export const SampleVideo = {
  "Xid": "35c22289-06e2-48e9-a0cd-94aeb79fab43",
  "id": "123",
  "at": 2,
  "imp": [
      {
          "id": "35c22289-06e2-48e9-a0cd-94aeb79fab43-1",
          "instl": 0,
          "bidfloor": 0.01,
          "video": {
              "h": 200,
              "w": 400,
              "linearity": 1,
              "minduration": 5,
              "maxduration": 30,
              "protocols": [
                2, 3
              ],
       "mimes": [
         "video/x-flv",
         "video/mp4",
         "application/x-shockwave-flash",
         "application/javascript"
       ],
              "pos": 0
          },
          "ext": {
              "nex_screen": 0
          }
      }
  ],
  "site": {
      "id": "99201",
      "name": "Bidder Test Mobile WEB",
      "domain": "junk1.com",
      "cat": [
          "IAB1"
      ],
      "keywords": "radiation",
      "page": "http://www.nexage.com",
      "ref": "http://www.iab.net",
      "search": "radiation",
      "publisher": {
          "id": "98401",
          "name": "RTB Bidder Integration Test Publisher"
      },
      "ext": {
          "nex_coppa": 0
      }
  },
  "device": {
      "didsha1": "132079238ec783b0b89dff308e1f9bdd08576273",
      "dpidsha1": "f22711a823044bb9ce7ace097955de0286eb0182",
      "ip": "166.137.138.18",
      "carrier": "ATT",
      "ua": "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_2_1 like Mac OS X; el-gr) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8C148 Safari/6533.18.5",
      "make": "Apple",
      "model": "iPhone",
      "osv": "3.1.2",
      "connectiontype": 3,
      "devicetype": 1,
      "geo": {
          "lat": 42.378,
          "lon": -71.227,
          "country": "USA"
      }
  },
  "user": {
      "id": "ASDFJKL",
      "yob": 1961,
      "gender": "F",
      "keywords": "sports",
      "geo": {
          "country": "USA",
          "city": "Waltham",
          "zip": "02451",
          "region": "MA",
          "type": 3
      },
      "ext": {
          "nex_eth": "4",
          "nex_marital": "M",
          "nex_kids": "N",
          "nex_hhi": 75000,
          "nex_dma": "Boston"
      }
  }
};

export const SampleNative = {
  "Xid": "35c22289-06e2-48e9-a0cd-94aeb79fab43",
  "id": "123",
  "at": 2,
  "imp": [
      {
          "id": "39c22289-06e2-48e9-a0cd-94aeb79fab43-1=3",
          "instl": 0,
          "native": {
              "layout":2,
              "assets": [
                  {
                      "id": 1,
                      "required": 1,
                      "title": {
                          "len": 30
                      }
                  },
                  {
                      "id": 2,
                      "required": 0,
                      "data": {
                          "type": 3,
                          "len": 5
                      }
                  },
                  {
                      "id": 3,
                      "required": 1,
                      "img": {
                          "type": 1,
                          "w": 64,
                          "h": 64,
                          "mimes": [
                              "image/png"
                          ]
                      }
                  },
                  {
                      "id": 4,
                      "required": 0,
                      "data": {
                          "type": 2,
                          "len": 10
                      }
                  }
              ]
          },
          "ext": {
              "nex_screen": 0
          }
      }
  ],
  "site": {
      "id": "99201",
      "name": "Bidder Test Mobile WEB",
      "domain": "junk1.com",
      "cat": [
          "IAB1"
      ],
      "keywords": "radiation",
      "page": "http://www.nexage.com",
      "ref": "http://www.iab.net",
      "search": "radiation",
      "publisher": {
          "id": "98401",
          "name": "RTB Bidder Integration Test Publisher"
      },
      "ext": {
          "nex_coppa": 0
      }
  },
  "device": {
      "didsha1": "132079238ec783b0b89dff308e1f9bdd08576273",
      "dpidsha1": "f22711a823044bb9ce7ace097955de0286eb0182",
      "ip": "166.137.138.18",
      "carrier": "ATT",
      "ua": "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_2_1 like Mac OS X; el-gr) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8C148 Safari/6533.18.5",
      "make": "Apple",
      "model": "iPhone",
      "osv": "3.1.2",
      "connectiontype": 3,
      "devicetype": 1,
      "geo": {
          "lat": 42.378,
          "lon": -71.227,
          "country": "USA"
      }
  },
  "user": {
      "id": "ASDFJKL",
      "yob": 1961,
      "gender": "F",
      "keywords": "sports",
      "geo": {
          "country": "USA",
          "city": "Waltham",
          "zip": "02451",
          "region": "MA",
          "type": 3
      },
      "ext": {
          "nex_eth": "4",
          "nex_marital": "M",
          "nex_kids": "N",
          "nex_hhi": 75000,
          "nex_dma": "Boston"
      }
  }
};

export const Logo = () =>
  <div style={{ margin: '1rem auto', display: 'flex', flexWrap: 'wrap', alignItems: 'center', justifyContent: 'center'}}>
    For more information, visit {'RTB4FREE.COM'}
  <br />
    <a href="http://rtb4free.com" target="_blank">
      <img
        src="http://rtb4free.com/images/alien.png"
        alt=""
        style={{ width: `50px`, margin: ".5em auto .3em" }}
      />
    </a>
  </div>;

export const Tips = () =>
  <div style={{ textAlign: "center" }}>
    <em>Tip: Hold shift when sorting to multi-sort!</em>
  </div>;
