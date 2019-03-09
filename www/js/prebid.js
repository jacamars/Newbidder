/* prebid.js v0.29.0
Updated : 2017-09-28 */
!(function(e) {
  function n(t) {
    if (r[t]) return r[t].exports;
    var i = r[t] = {
      i: t,
      l: !1,
      exports: {}
    };
    return e[t].call(i.exports, i, i.exports, n), i.l = !0, i.exports
  }
  var t = window.pbjsChunk;
  window.pbjsChunk = function(r, o, a) {
    for (var s, d, u, c = 0, f = []; c < r.length; c++) d = r[c], i[d] && f.push(i[d][0]), i[d] = 0;
    for (s in o) Object.prototype.hasOwnProperty.call(o, s) && (e[s] = o[s]);
    for (t && t(r, o, a); f.length;) f.shift()();
    if (a) for (c = 0; c < a.length; c++) u = n(n.s = a[c]);
    return u
  };
  var r = {}, i = {
    104: 0
  };
  n.e = function(e) {
    if (0 === i[e]) return callback.call(null, n);
    console.error("webpack chunk not found and jsonp disabled")
  }, n.m = e, n.c = r, n.d = function(e, t, r) {
    n.o(e, t) || Object.defineProperty(e, t, {
      configurable: !1,
      enumerable: !0,
      get: r
    })
  }, n.n = function(e) {
    var t = e && e.__esModule ? function() {
        return e.
        default
      } : function() {
        return e
      };
    return n.d(t, "a", t), t
  }, n.o = function(e, n) {
    return Object.prototype.hasOwnProperty.call(e, n)
  }, n.p = "", n.oe = function(e) {
    throw console.error(e), e
  }, n(n.s = 257)
})([(function(e, n, t) {
  "use strict";

  function r(e, n, t) {
    return n in e ? Object.defineProperty(e, n, {
      value: t,
      enumerable: !0,
      configurable: !0,
      writable: !0
    }) : e[n] = t, e
  }
  function i() {
    return h() + Math.random().toString(16).substr(2)
  }
  function o(e) {
    if (n.isArray(e) && 2 === e.length && !isNaN(e[0]) && !isNaN(e[1])) return e[0] + "x" + e[1]
  }
  function a() {
    return window.console && window.console.log
  }
  function s(e, n, t) {
    return t.indexOf(e) === n
  }
  function d(e, n) {
    return e.concat(n)
  }
  function u(e) {
    return Object.keys(e)
  }
  function c(e, n) {
    return e[n]
  }
  Object.defineProperty(n, "__esModule", {
    value: !0
  });
  var f = Object.assign || function(e) {
      for (var n = 1; n < arguments.length; n++) {
        var t = arguments[n];
        for (var r in t) Object.prototype.hasOwnProperty.call(t, r) && (e[r] = t[r])
      }
      return e
    }, l = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function(e) {
      return typeof e
    } : function(e) {
      return e && "function" == typeof Symbol && e.constructor === Symbol && e !== Symbol.prototype ? "symbol" : typeof e
    };
  n.parseSizesInput = function(e) {
    var n = [];
    if ("string" == typeof e) {
      var t = e.split(","),
        r = /^(\d)+x(\d)+$/i;
      if (t) for (var i in t) T(t, i) && t[i].match(r) && n.push(t[i])
    } else if ("object" === (void 0 === e ? "undefined" : l(e))) {
      var a = e.length;
      if (a > 0) if (2 === a && "number" == typeof e[0] && "number" == typeof e[1]) n.push(o(e));
      else for (var s = 0; s < a; s++) n.push(o(e[s]))
    }
    return n
  }, n.parseGPTSingleSizeArray = o, n.uniques = s, n.flatten = d, n.getBidRequest = function(e) {
    return pbjs._bidsRequested.map((function(n) {
      return n.bids.find((function(n) {
        return n.bidId === e
      }))
    })).find((function(e) {
      return e
    }))
  }, n.getKeys = u, n.getValue = c, n.getBidderCodes = function() {
    return (arguments.length > 0 && void 0 !== arguments[0] ? arguments[0] : pbjs.adUnits).map((function(e) {
      return e.bids.map((function(e) {
        return e.bidder
      })).reduce(d, [])
    })).reduce(d).filter(s)
  }, n.isGptPubadsDefined = function() {
    if (window.googletag && n.isFn(window.googletag.pubads) && n.isFn(window.googletag.pubads().getSlots)) return !0
  }, n.getHighestCpm = function(e, n) {
    return e.cpm === n.cpm ? e.timeToRespond > n.timeToRespond ? n : e : e.cpm < n.cpm ? n : e
  }, n.shuffle = function(e) {
    for (var n = e.length; n > 0;) {
      var t = Math.floor(Math.random() * n),
        r = e[--n];
      e[n] = e[t], e[t] = r
    }
    return e
  }, n.adUnitsFilter = function(e, n) {
    return e.includes(n && n.placementCode || n && n.adUnitCode)
  }, n.isSrcdocSupported = function(e) {
    return e.defaultView && e.defaultView.frameElement && "srcdoc" in e.defaultView.frameElement && !/firefox/i.test(navigator.userAgent)
  }, n.cloneJson = function(e) {
    return JSON.parse(JSON.stringify(e))
  }, n.inIframe = function() {
    try {
      return window.self !== window.top
    } catch (e) {
      return !0
    }
  }, n.isSafariBrowser = function() {
    return /^((?!chrome|android).)*safari/i.test(navigator.userAgent)
  }, n.replaceAuctionPrice = function(e, n) {
    if (e) return e.replace(/\$\{AUCTION_PRICE\}/g, n)
  }, n.getBidderRequestAllAdUnits = function(e) {
    return pbjs._bidsRequested.find((function(n) {
      return n.bidderCode === e
    }))
  }, n.getBidderRequest = function(e, n) {
    return pbjs._bidsRequested.find((function(t) {
      return t.bids.filter((function(t) {
        return t.bidder === e && t.placementCode === n
      })).length > 0
    })) || {
      start: null,
      requestId: null
    }
  }, n.cookiesAreEnabled = function() {
    return !(!window.navigator.cookieEnabled && !document.cookie.length) || (window.document.cookie = "prebid.cookieTest", -1 != window.document.cookie.indexOf("prebid.cookieTest"))
  }, n.delayExecution = function(e, n) {
    if (n < 1) throw new Error("numRequiredCalls must be a positive number. Got " + n);
    var t = 0;
    return function() {
      ++t === n && e.apply(null, arguments)
    }
  }, n.groupBy = function(e, n) {
    return e.reduce((function(e, t) {
      return (e[t[n]] = e[t[n]] || []).push(t), e
    }), {})
  }, n.deepAccess = function(e, n) {
    n = String(n).split(".");
    for (var t = 0; t < n.length; t++) if (void 0 === (e = e[n[t]])) return;
    return e
  }, n.getDefinedParams = function(e, n) {
    return n.filter((function(n) {
      return e[n]
    })).reduce((function(n, t) {
      return f(n, r({}, t, e[t]))
    }), {})
  }, n.isValidMediaTypes = function(e) {
    var n = ["banner", "native", "video"],
      t = ["instream", "outstream"];
    return !!Object.keys(e).every((function(e) {
      return n.includes(e)
    })) && (!e.video || !e.video.context || t.includes(e.video.context))
  };
  var p = t(8),
    g = t(4),
    b = !1,
    v = Object.prototype.toString,
    m = null;
  try {
    m = console.info.bind(window.console)
  } catch (e) {}
  n.replaceTokenInString = function(e, n, t) {
    return this._each(n, (function(n, r) {
      n = void 0 === n ? "" : n;
      var i = t + r.toUpperCase() + t,
        o = new RegExp(i, "g");
      e = e.replace(o, n)
    })), e
  };
  var h = (function() {
    var e = 0;
    return function() {
      return ++e
    }
  })();
  n.getUniqueIdentifierStr = i, n.generateUUID = function e(n) {
    return n ? (n ^ 16 * Math.random() >> n / 4).toString(16) : ([1e7] + -1e3 + -4e3 + -8e3 + -1e11).replace(/[018]/g, e)
  }, n.getBidIdParameter = function(e, n) {
    return n && n[e] ? n[e] : ""
  }, n.tryAppendQueryString = function(e, n, t) {
    return t ? e += n + "=" + encodeURIComponent(t) + "&" : e
  }, n.parseQueryStringParameters = function(e) {
    var n = "";
    for (var t in e) e.hasOwnProperty(t) && (n += t + "=" + encodeURIComponent(e[t]) + "&");
    return n
  }, n.transformAdServerTargetingObj = function(e) {
    return e && Object.getOwnPropertyNames(e).length > 0 ? u(e).map((function(n) {
      return n + "=" + encodeURIComponent(c(e, n))
    })).join("&") : ""
  }, n.getTopWindowLocation = function() {
    var e = void 0;
    try {
      window.top.location.toString(), e = window.top.location
    } catch (n) {
      e = window.location
    }
    return e
  }, n.getTopWindowUrl = function() {
    var e = void 0;
    try {
      e = this.getTopWindowLocation().href
    } catch (n) {
      e = ""
    }
    return e
  }, n.logWarn = function(e) {
    E() && console.warn && console.warn("WARNING: " + e)
  }, n.logInfo = function(e, n) {
    E() && a() && m && (n && 0 !== n.length || (n = ""), m("INFO: " + e + ("" === n ? "" : " : params : "), n))
  }, n.logMessage = function(e) {
    E() && a() && console.log("MESSAGE: " + e)
  }, n.hasConsoleLogger = a;
  var y = a() ? window.console.error ? "error" : "log" : "",
    E = function() {
      if (!1 === p.config.getConfig("debug") && !1 === b) {
        var e = "TRUE" === S(g.DEBUG_MODE).toUpperCase();
        p.config.setConfig({
          debug: e
        }), b = !0
      }
      return !!p.config.getConfig("debug")
    };
  n.debugTurnedOn = E, n.logError = function(e, n, t) {
    var r = n || "ERROR";
    E() && a() && console[y](console, r + ": " + e, t || "")
  }, n.createInvisibleIframe = function() {
    var e = document.createElement("iframe");
    return e.id = i(), e.height = 0, e.width = 0, e.border = "0px", e.hspace = "0", e.vspace = "0", e.marginWidth = "0", e.marginHeight = "0", e.style.border = "0", e.scrolling = "no", e.frameBorder = "0", e.src = "about:blank", e.style.display = "none", e
  };
  var S = function(e) {
    var n = "[\\?&]" + e + "=([^&#]*)",
      t = new RegExp(n).exec(window.location.search);
    return null === t ? "" : decodeURIComponent(t[1].replace(/\+/g, " "))
  };
  n.getParameterByName = S, n.hasValidBidRequest = function(e, n, t) {
    for (var r = !1, i = 0; i < n.length; i++) if (r = !1, this._each(e, (function(e, t) {
      t === n[i] && (r = !0)
    })), !r) return this.logError("Params are missing for bid request. One of these required paramaters are missing: " + n, t), !1;
    return !0
  }, n.addEventHandler = function(e, n, t) {
    e.addEventListener ? e.addEventListener(n, t, !0) : e.attachEvent && e.attachEvent("on" + n, t)
  }, n.isA = function(e, n) {
    return v.call(e) === "[object " + n + "]"
  }, n.isFn = function(e) {
    return this.isA(e, "Function")
  }, n.isStr = function(e) {
    return this.isA(e, "String")
  }, n.isArray = function(e) {
    return this.isA(e, "Array")
  }, n.isNumber = function(e) {
    return this.isA(e, "Number")
  }, n.isEmpty = function(e) {
    if (!e) return !0;
    if (this.isArray(e) || this.isStr(e)) return !(e.length > 0);
    for (var n in e) if (hasOwnProperty.call(e, n)) return !1;
    return !0
  }, n.isEmptyStr = function(e) {
    return this.isStr(e) && (!e || 0 === e.length)
  }, n._each = function(e, n) {
    if (!this.isEmpty(e)) {
      if (this.isFn(e.forEach)) return e.forEach(n, this);
      var t = 0,
        r = e.length;
      if (r > 0) for (; t < r; t++) n(e[t], t, e);
      else for (t in e) hasOwnProperty.call(e, t) && n.call(this, e[t], t)
    }
  }, n.contains = function(e, n) {
    if (this.isEmpty(e)) return !1;
    if (this.isFn(e.indexOf)) return -1 !== e.indexOf(n);
    for (var t = e.length; t--;) if (e[t] === n) return !0;
    return !1
  }, n.indexOf = (function() {
    if (Array.prototype.indexOf) return Array.prototype.indexOf
  })(), n._map = function(e, n) {
    if (this.isEmpty(e)) return [];
    if (this.isFn(e.map)) return e.map(n);
    var t = [];
    return this._each(e, (function(r, i) {
      t.push(n(r, i, e))
    })), t
  };
  var T = function(e, n) {
    return e.hasOwnProperty ? e.hasOwnProperty(n) : void 0 !== e[n] && e.constructor.prototype[n] !== e[n]
  };
  n.insertElement = function(e, n, t) {
    n = n || document;
    var r = void 0;
    r = t ? n.getElementsByTagName(t) : n.getElementsByTagName("head");
    try {
      (r = r.length ? r : n.getElementsByTagName("body")).length && (r = r[0]).insertBefore(e, r.firstChild)
    } catch (e) {}
  }, n.triggerPixel = function(e) {
    (new Image).src = e
  }, n.insertUserSyncIframe = function(e) {
    var t = this.createTrackPixelIframeHtml(e, !1, "allow-scripts"),
      r = document.createElement("div");
    r.innerHTML = t;
    var i = r.firstChild;
    n.insertElement(i)
  }, n.createTrackPixelHtml = function(e) {
    if (!e) return "";
    var n = '<div style="position:absolute;left:0px;top:0px;visibility:hidden;">';
    return n += '<img src="' + encodeURI(e) + '"></div>'
  }, n.createTrackPixelIframeHtml = function(e) {
    var t = !(arguments.length > 1 && void 0 !== arguments[1]) || arguments[1],
      r = arguments.length > 2 && void 0 !== arguments[2] ? arguments[2] : "";
    return e ? (t && (e = encodeURI(e)), r && (r = 'sandbox="' + r + '"'), "<iframe " + r + ' id="' + n.getUniqueIdentifierStr() + '"\n      frameborder="0"\n      allowtransparency="true"\n      marginheight="0" marginwidth="0"\n      width="0" hspace="0" vspace="0" height="0"\n      style="height:0p;width:0p;display:none;"\n      scrolling="no"\n      src="' + e + '">\n    </iframe>') : ""
  }, n.getIframeDocument = function(e) {
    if (e) {
      var n = void 0;
      try {
        n = e.contentWindow ? e.contentWindow.document : e.contentDocument.document ? e.contentDocument.document : e.contentDocument
      } catch (e) {
        this.logError("Cannot get iframe document", e)
      }
      return n
    }
  }, n.getValueString = function(e, n, t) {
    return void 0 === n || null === n ? t : this.isStr(n) ? n : this.isNumber(n) ? n.toString() : void this.logWarn("Unsuported type for param: " + e + " required type: String")
  }
}), (function(e, n, t) {
  "use strict";

  function r(e) {
    var n = e.bidderCode,
      t = e.requestId,
      r = e.bidderRequestId;
    return e.adUnits.map((function(e) {
      return e.bids.filter((function(e) {
        return e.bidder === n
      })).map((function(n) {
        var i = e.sizes;
        if (e.sizeMapping) {
          var u = (0, s.mapSizes)(e);
          if ("" === u) return "";
          i = u
        }
        return e.nativeParams && (n = o({}, n, {
          nativeParams: (0, d.processNativeAdUnitParams)(e.nativeParams)
        })), e.mediaTypes && (c.isValidMediaTypes(e.mediaTypes) ? n = o({}, n, {
          mediaTypes: e.mediaTypes
        }) : c.logError("mediaTypes is not correctly configured for adunit " + e.code)), n = o({}, n, (0, a.getDefinedParams)(e, ["mediaType", "renderer"])), o({}, n, {
          placementCode: e.code,
          transactionId: e.transactionId,
          sizes: i,
          bidId: n.bid_id || c.getUniqueIdentifierStr(),
          bidderRequestId: r,
          requestId: t
        })
      }))
    })).reduce(a.flatten, []).filter((function(e) {
      return "" !== e
    }))
  }
  function i(e) {
    var n = [];
    return c.parseSizesInput(e.sizes).forEach((function(e) {
      var t = e.split("x"),
        r = {
          w: parseInt(t[0]),
          h: parseInt(t[1])
        };
      n.push(r)
    })), n
  }
  var o = Object.assign || function(e) {
      for (var n = 1; n < arguments.length; n++) {
        var t = arguments[n];
        for (var r in t) Object.prototype.hasOwnProperty.call(t, r) && (e[r] = t[r])
      }
      return e
    }, a = t(0),
    s = t(45),
    d = t(13),
    u = t(28),
    c = t(0),
    f = t(4),
    l = t(9),
    p = {};
  n.bidderRegistry = p;
  var g = {
    endpoint: f.S2S.DEFAULT_ENDPOINT,
    adapter: f.S2S.ADAPTER,
    syncEndpoint: f.S2S.SYNC_ENDPOINT
  }, b = {};
  b.random = !0, b.fixed = !0;
  var v = {}, m = "random";
  n.callBids = function(e) {
    var n = e.adUnits,
      t = e.cbTimeout,
      o = c.generateUUID(),
      d = Date.now(),
      b = {
        timestamp: d,
        requestId: o,
        timeout: t
      };
    l.emit(f.EVENTS.AUCTION_INIT, b);
    var v = (0, a.getBidderCodes)(n),
      h = u.StorageManager.get(u.pbjsSyncsKey);
    "random" === m && (v = (0, a.shuffle)(v));
    var y = p[g.adapter];
    if (y && (y.setConfig(g), y.queueSync({
      bidderCodes: v
    })), g.enabled) {
      var E = g.bidders.filter((function(e) {
        return h.includes(e)
      }));
      v = v.filter((function(e) {
        return !E.includes(e)
      }));
      var S = c.cloneJson(n);
      S.forEach((function(e) {
        e.sizeMapping && (e.sizes = (0, s.mapSizes)(e), delete e.sizeMapping), e.sizes = i(e), e.bids = e.bids.filter((function(e) {
          return E.includes(e.bidder)
        })).map((function(e) {
          return e.bid_id = c.getUniqueIdentifierStr(), e
        }))
      })), S = S.filter((function(e) {
        return 0 !== e.bids.length
      }));
      var T = c.generateUUID();
      E.forEach((function(e) {
        var n = c.getUniqueIdentifierStr(),
          t = {
            bidderCode: e,
            requestId: o,
            bidderRequestId: n,
            tid: T,
            bids: r({
              bidderCode: e,
              requestId: o,
              bidderRequestId: n,
              adUnits: S
            }),
            start: (new Date).getTime(),
            auctionStart: d,
            timeout: g.timeout,
            src: f.S2S.SRC
          };
        0 !== t.bids.length && pbjs._bidsRequested.push(t)
      }));
      var A = {
        tid: T,
        ad_units: S
      };
      c.logMessage("CALLING S2S HEADER BIDDERS ==== " + E.join(",")), A.ad_units.length && y.callBids(A)
    }
    v.forEach((function(e) {
      var i = p[e];
      if (i) {
        var a = c.getUniqueIdentifierStr(),
          s = {
            bidderCode: e,
            requestId: o,
            bidderRequestId: a,
            bids: r({
              bidderCode: e,
              requestId: o,
              bidderRequestId: a,
              adUnits: n
            }),
            start: (new Date).getTime(),
            auctionStart: d,
            timeout: t
          };
        s.bids && 0 !== s.bids.length && (c.logMessage("CALLING BIDDER ======= " + e), pbjs._bidsRequested.push(s), l.emit(f.EVENTS.BID_REQUESTED, s), i.callBids(s))
      } else c.logError("Adapter trying to be called which does not exist: " + e + " adaptermanager.callBids")
    }))
  }, n.videoAdapters = [], n.registerBidAdapter = function(e, t) {
    var r = (arguments.length > 2 && void 0 !== arguments[2] ? arguments[2] : {}).supportedMediaTypes,
      i = void 0 === r ? [] : r;
    e && t ? "function" == typeof e.callBids ? (p[t] = e, i.includes("video") && n.videoAdapters.push(t), i.includes("native") && d.nativeAdapters.push(t)) : c.logError("Bidder adaptor error for bidder code: " + t + "bidder must implement a callBids() function") : c.logError("bidAdaptor or bidderCode not specified")
  }, n.aliasBidAdapter = function(e, n) {
    if (void 0 === p[n]) {
      var t = p[e];
      if (void 0 === t) c.logError('bidderCode "' + e + '" is not an existing bidder.', "adaptermanager.aliasBidAdapter");
      else try {
        var r = new t.constructor;
        r.setBidderCode(n), this.registerBidAdapter(r, n)
      } catch (n) {
        c.logError(e + " bidder does not currently support aliasing.", "adaptermanager.aliasBidAdapter")
      }
    } else c.logMessage('alias name "' + n + '" has been already specified.')
  }, n.registerAnalyticsAdapter = function(e) {
    var n = e.adapter,
      t = e.code;
    n && t ? "function" == typeof n.enableAnalytics ? (n.code = t, v[t] = n) : c.logError('Prebid Error: Analytics adaptor error for analytics "' + t + '"\n        analytics adapter must implement an enableAnalytics() function') : c.logError("Prebid Error: analyticsAdapter or analyticsCode not specified")
  }, n.enableAnalytics = function(e) {
    c.isArray(e) || (e = [e]), c._each(e, (function(e) {
      var n = v[e.provider];
      n ? n.enableAnalytics(e) : c.logError("Prebid Error: no analytics adapter found in registry for\n        " + e.provider + ".")
    }))
  }, n.setBidderSequence = function(e) {
    b[e] ? m = e : c.logWarn("Invalid order: " + e + ". Bidder Sequence was not set.")
  }, n.setS2SConfig = function(e) {
    g = e
  }
}), (function(e, n, t) {
  "use strict";

  function r() {
    return (new Date).getTime()
  }
  function i(e) {
    return e.bidderCode
  }
  function o(e) {
    return e.bidder
  }
  function a(e) {
    var n = this;
    return pbjs._bidsRequested.map((function(t) {
      return t.bids.filter(h.adUnitsFilter.bind(n, pbjs._adUnitCodes)).filter((function(n) {
        return n.placementCode === e
      }))
    })).reduce(h.flatten, []).map((function(e) {
      return "indexExchange" === e.bidder ? e.sizes.length : 1
    })).reduce(s, 0) === pbjs._bidsReceived.filter((function(n) {
      return n.adUnitCode === e
    })).length
  }
  function s(e, n) {
    return e + n
  }
  function d() {
    return pbjs._bidsRequested.map((function(e) {
      return e.bids
    })).reduce(h.flatten, []).filter(h.adUnitsFilter.bind(this, pbjs._adUnitCodes)).map((function(e) {
      return "indexExchange" === e.bidder ? e.sizes.length : 1
    })).reduce((function(e, n) {
      return e + n
    }), 0) === pbjs._bidsReceived.filter(h.adUnitsFilter.bind(this, pbjs._adUnitCodes)).length
  }
  function u(e, n) {
    var t = {}, r = pbjs.bidderSettings;
    if (n && r) {
      var i = b();
      c(t, i, n)
    }
    return e && n && r && r[e] && r[e][_.JSON_MAPPING.ADSERVER_TARGETING] ? (c(t, r[e], n), n.alwaysUseBid = r[e].alwaysUseBid, n.sendStandardTargeting = r[e].sendStandardTargeting) : U[e] && (c(t, U[e], n), n.alwaysUseBid = U[e].alwaysUseBid, n.sendStandardTargeting = U[e].sendStandardTargeting), n.native && Object.keys(n.native).forEach((function(e) {
      var r = E.NATIVE_KEYS[e],
        i = n.native[e];
      r && (t[r] = i)
    })), t
  }
  function c(e, n, t) {
    var r = n[_.JSON_MAPPING.ADSERVER_TARGETING];
    return t.size = t.getSize(), j._each(r, (function(r) {
      var i = r.key,
        o = r.val;
      if (e[i] && j.logWarn("The key: " + i + " is getting ovewritten"), j.isFn(o)) try {
        o = o(t)
      } catch (e) {
        j.logError("bidmanager", "ERROR", e)
      }(void 0 === n.suppressEmptyKeys || !0 !== n.suppressEmptyKeys) && "hb_deal" !== i || !j.isEmptyStr(o) && null !== o && void 0 !== o ? e[i] = o : j.logInfo("suppressing empty key '" + i + "' from adserver targeting")
    })), e
  }
  function f(e) {
    var n = [e];
    l(O.byAdUnit, n)
  }
  function l(e, n) {
    var t = this;
    j.isArray(e) && e.forEach((function(e) {
      var r = n || pbjs._adUnitCodes,
        i = [pbjs._bidsReceived.filter(h.adUnitsFilter.bind(t, r)).reduce(p, {})];
      e.apply(pbjs, i)
    }))
  }
  function p(e, n) {
    return e[n.adUnitCode] || (e[n.adUnitCode] = {
      bids: []
    }), e[n.adUnitCode].bids.push(n), e
  }
  function g(e) {
    var n = e.bidderCode,
      t = e.cpm;
    if (n && pbjs.bidderSettings && pbjs.bidderSettings[n] && "function" == typeof pbjs.bidderSettings[n].bidCpmAdjustment) try {
      t = pbjs.bidderSettings[n].bidCpmAdjustment.call(null, e.cpm, v({}, e))
    } catch (e) {
      j.logError("Error during bid adjustment", "bidmanager.js", e)
    }
    t >= 0 && (e.cpm = t)
  }
  function b() {
    var e = I.config.getConfig("priceGranularity"),
      n = pbjs.bidderSettings;
    return n[_.JSON_MAPPING.BD_SETTING_STANDARD] || (n[_.JSON_MAPPING.BD_SETTING_STANDARD] = {
      adserverTargeting: [{
        key: "hb_bidder",
        val: function(e) {
          return e.bidderCode
        }
      }, {
        key: "hb_adid",
        val: function(e) {
          return e.adId
        }
      }, {
        key: "hb_pb",
        val: function(n) {
          return e === _.GRANULARITY_OPTIONS.AUTO ? n.pbAg : e === _.GRANULARITY_OPTIONS.DENSE ? n.pbDg : e === _.GRANULARITY_OPTIONS.LOW ? n.pbLg : e === _.GRANULARITY_OPTIONS.MEDIUM ? n.pbMg : e === _.GRANULARITY_OPTIONS.HIGH ? n.pbHg : e === _.GRANULARITY_OPTIONS.CUSTOM ? n.pbCg : void 0
        }
      }, {
        key: "hb_size",
        val: function(e) {
          return e.size
        }
      }, {
        key: "hb_deal",
        val: function(e) {
          return e.dealId
        }
      }]
    }), n[_.JSON_MAPPING.BD_SETTING_STANDARD]
  }
  var v = Object.assign || function(e) {
      for (var n = 1; n < arguments.length; n++) {
        var t = arguments[n];
        for (var r in t) Object.prototype.hasOwnProperty.call(t, r) && (e[r] = t[r])
      }
      return e
    }, m = (function() {
      function e(e, n) {
        var t = [],
          r = !0,
          i = !1,
          o = void 0;
        try {
          for (var a, s = e[Symbol.iterator](); !(r = (a = s.next()).done) && (t.push(a.value), !n || t.length !== n); r = !0);
        } catch (e) {
          i = !0, o = e
        } finally {
          try {
            !r && s.
            return &&s.
            return ()
          } finally {
            if (i) throw o
          }
        }
        return t
      }
      return function(n, t) {
        if (Array.isArray(n)) return n;
        if (Symbol.iterator in Object(n)) return e(n, t);
        throw new TypeError("Invalid attempt to destructure non-iterable instance")
      }
    })(),
    h = t(0),
    y = t(26),
    E = t(13),
    S = t(27),
    T = t(46),
    A = t(18),
    I = t(8),
    _ = t(4),
    w = _.EVENTS.AUCTION_END,
    j = t(0),
    C = t(9),
    O = {
      byAdUnit: [],
      all: [],
      oneTime: null,
      timer: !1
    }, U = {};
  n.getTimedOutBidders = function() {
    return pbjs._bidsRequested.map(i).filter(h.uniques).filter((function(e) {
      return pbjs._bidsReceived.map(o).filter(h.uniques).indexOf(e) < 0
    }))
  }, n.bidsBackAll = function() {
    return d()
  }, n.addBidResponse = function(e, t) {
    function i(n) {
      if ((n.width || 0 === n.width) && (n.height || 0 === n.height)) return !0;
      var t = (0, h.getBidderRequest)(n.bidderCode, e),
        r = t && t.bids && t.bids[0] && t.bids[0].sizes,
        i = j.parseSizesInput(r);
      if (1 === i.length) {
        var o = i[0].split("x"),
          a = m(o, 2),
          s = a[0],
          d = a[1];
        return n.width = s, n.height = d, !0
      }
      return !1
    }
    function o() {
      if (t.timeToRespond > pbjs.cbTimeout + pbjs.timeoutBuffer) {
        n.executeCallback(!0)
      }
    }
    function s() {
      C.emit(_.EVENTS.BID_RESPONSE, t), pbjs._bidsReceived.push(t), t.adUnitCode && a(t.adUnitCode) && f(t.adUnitCode), d() && n.executeCallback()
    }(function() {
      function n(e) {
        return "Invalid bid from " + t.bidderCode + ". Ignoring bid: " + e
      }
      return t ? e ? (0, h.getBidderRequest)(t.bidderCode, e).start ? "native" !== t.mediaType || (0, E.nativeBidIsValid)(t) ? "video" !== t.mediaType || (0, S.isValidVideoBid)(t) ? !("banner" === t.mediaType && !i(t) && (j.logError(n("Banner bids require a width and height")), 1)) : (j.logError(n("Video bid does not have required vastUrl or renderer property")), !1) : (j.logError(n("Native bid missing some required properties.")), !1) : (j.logError(n("Cannot find valid matching bid request.")), !1) : (j.logError(n("No adUnitCode was supplied to addBidResponse.")), !1) : (j.logError("Some adapter tried to add an undefined bid for " + e + "."), !1)
    })() && (function() {
      var n = (0, h.getBidderRequest)(t.bidderCode, e);
      v(t, {
        requestId: n.requestId,
        responseTimestamp: r(),
        requestTimestamp: n.start,
        cpm: parseFloat(t.cpm) || 0,
        bidder: t.bidderCode,
        adUnitCode: e
      }), t.timeToRespond = t.responseTimestamp - t.requestTimestamp, C.emit(_.EVENTS.BID_ADJUSTMENT, t);
      var i = n.bids && n.bids[0] && n.bids[0].renderer;
      i && (t.renderer = A.Renderer.install({
        url: i.url
      }), t.renderer.setRender(i.render));
      var o = (0, y.getPriceBucketString)(t.cpm, I.config.getConfig("customPriceBucket"), I.config.getConfig("currency.granularityMultiplier"));
      t.pbLg = o.low, t.pbMg = o.med, t.pbHg = o.high, t.pbAg = o.auto, t.pbDg = o.dense, t.pbCg = o.custom;
      var a;
      t.bidderCode && (t.cpm > 0 || t.dealId) && (a = u(t.bidderCode, t)), t.adserverTargeting = v(t.adserverTargeting || {}, a)
    }(), "video" === t.mediaType ? (function(e) {
      I.config.getConfig("usePrebidCache") ? (0, T.store)([e], (function(n, t) {
        n ? j.logWarn("Failed to save to the video cache: " + n + ". Video bid must be discarded.") : (e.videoCacheKey = t[0].uuid, e.vastUrl || (e.vastUrl = (0, T.getCacheUrl)(e.videoCacheKey)), s(e)), o()
      })) : (s(e), o())
    })(t) : (s(t), o()))
  }, n.getKeyValueTargetingPairs = function() {
    return u.apply(void 0, arguments)
  }, n.registerDefaultBidderSetting = function(e, n) {
    U[e] = n
  }, n.executeCallback = function(e) {
    if (!e && O.timer && clearTimeout(O.timer), !0 !== O.all.called && (l(O.all), O.all.called = !0, e)) {
      var t = n.getTimedOutBidders();
      t.length && C.emit(_.EVENTS.BID_TIMEOUT, t)
    }
    if (O.oneTime) {
      C.emit(w);
      try {
        l([O.oneTime])
      } catch (e) {
        j.logError("Error executing bidsBackHandler", null, e)
      } finally {
        O.oneTime = null, O.timer = !1, pbjs.clearAuction()
      }
    }
  }, n.externalCallbackReset = function() {
    O.all.called = !1
  }, n.addOneTimeCallback = function(e, n) {
    O.oneTime = e, O.timer = n
  }, n.addCallback = function(e, n, t) {
    n.id = e, _.CB.TYPE.ALL_BIDS_BACK === t ? O.all.push(n) : _.CB.TYPE.AD_UNIT_BIDS_BACK === t && O.byAdUnit.push(n)
  }, C.on(_.EVENTS.BID_ADJUSTMENT, (function(e) {
    g(e)
  })), n.adjustBids = function() {
    return g.apply(void 0, arguments)
  }, n.getStandardBidderAdServerTargeting = function() {
    return b()[_.JSON_MAPPING.ADSERVER_TARGETING]
  }
}), (function(e, n, t) {
  "use strict";

  function r(e, n) {
    var t = n && n.bidId || i.getUniqueIdentifierStr(),
      r = e || 0;
    this.bidderCode = n && n.bidder || "", this.width = 0, this.height = 0, this.statusMessage = (function() {
      switch (r) {
        case 0:
          return "Pending";
        case 1:
          return "Bid available";
        case 2:
          return "Bid returned empty or error response";
        case 3:
          return "Bid timed out"
      }
    })(), this.adId = t, this.mediaType = "banner", this.getStatusCode = function() {
      return r
    }, this.getSize = function() {
      return this.width + "x" + this.height
    }
  }
  var i = t(0);
  n.createBid = function(e, n) {
    return new r(e, n)
  }
}), (function(e, n) {
  e.exports = {
    JSON_MAPPING: {
      PL_CODE: "code",
      PL_SIZE: "sizes",
      PL_BIDS: "bids",
      BD_BIDDER: "bidder",
      BD_ID: "paramsd",
      BD_PL_ID: "placementId",
      ADSERVER_TARGETING: "adserverTargeting",
      BD_SETTING_STANDARD: "standard"
    },
    REPO_AND_VERSION: "prebid_prebid_0.29.0",
    DEBUG_MODE: "pbjs_debug",
    STATUS: {
      GOOD: 1,
      NO_BID: 2
    },
    CB: {
      TYPE: {
        ALL_BIDS_BACK: "allRequestedBidsBack",
        AD_UNIT_BIDS_BACK: "adUnitBidsBack",
        BID_WON: "bidWon",
        REQUEST_BIDS: "requestBids"
      }
    },
    EVENTS: {
      AUCTION_INIT: "auctionInit",
      AUCTION_END: "auctionEnd",
      BID_ADJUSTMENT: "bidAdjustment",
      BID_TIMEOUT: "bidTimeout",
      BID_REQUESTED: "bidRequested",
      BID_RESPONSE: "bidResponse",
      BID_WON: "bidWon",
      SET_TARGETING: "setTargeting",
      REQUEST_BIDS: "requestBids"
    },
    EVENT_ID_PATHS: {
      bidWon: "adUnitCode"
    },
    GRANULARITY_OPTIONS: {
      LOW: "low",
      MEDIUM: "medium",
      HIGH: "high",
      AUTO: "auto",
      DENSE: "dense",
      CUSTOM: "custom"
    },
    TARGETING_KEYS: ["hb_bidder", "hb_adid", "hb_pb", "hb_size", "hb_deal"],
    S2S: {
      DEFAULT_ENDPOINT: "http://localhost:8080/auction",
      SRC: "s2s",
      ADAPTER: "prebidServer",
      SYNC_ENDPOINT: "http://localhost:8080/cookie_sync",
      SYNCED_BIDDERS_KEY: "pbjsSyncs"
    }
  }
}), (function(e, n, t) {
  "use strict";

  function r(e, n) {
    var t = document.createElement("script");
    t.type = "text/javascript", t.async = !0, n && "function" == typeof n && (t.readyState ? t.onreadystatechange = function() {
      "loaded" !== t.readyState && "complete" !== t.readyState || (t.onreadystatechange = null, n())
    } : t.onload = function() {
      n()
    }), t.src = e;
    var r = document.getElementsByTagName("head");
    (r = r.length ? r : document.getElementsByTagName("body")).length && (r = r[0]).insertBefore(t, r.firstChild)
  }
  var i = t(0),
    o = {};
  n.loadScript = function(e, n, t) {
    e ? t ? o[e] ? n && "function" == typeof n && (o[e].loaded ? n() : o[e].callbacks.push(n)) : (o[e] = {
      loaded: !1,
      callbacks: []
    }, n && "function" == typeof n && o[e].callbacks.push(n), r(e, (function() {
      o[e].loaded = !0;
      try {
        for (var n = 0; n < o[e].callbacks.length; n++) o[e].callbacks[n]()
      } catch (e) {
        i.logError("Error executing callback", "adloader.js:loadScript", e)
      }
    }))) : r(e, n) : i.logError("Error attempting to request empty URL", "adloader.js:loadScript")
  }
}), (function(e, n, t) {
  "use strict";
  Object.defineProperty(n, "__esModule", {
    value: !0
  });
  var r = Object.assign || function(e) {
      for (var n = 1; n < arguments.length; n++) {
        var t = arguments[n];
        for (var r in t) Object.prototype.hasOwnProperty.call(t, r) && (e[r] = t[r])
      }
      return e
    }, i = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function(e) {
      return typeof e
    } : function(e) {
      return e && "function" == typeof Symbol && e.constructor === Symbol && e !== Symbol.prototype ? "symbol" : typeof e
    };
  n.setAjaxTimeout = function(e) {
    d = e
  }, n.ajax = function(e, n, t) {
    var u = arguments.length > 3 && void 0 !== arguments[3] ? arguments[3] : {};
    try {
      var c = void 0,
        f = !1,
        l = u.method || (t ? "POST" : "GET"),
        p = "object" === (void 0 === n ? "undefined" : i(n)) ? n : {
          success: function() {
            a.logMessage("xhr success")
          },
          error: function(e) {
            a.logError("xhr error", null, e)
          }
        };
      if ("function" == typeof n && (p.success = n), window.XMLHttpRequest ? void 0 === (c = new window.XMLHttpRequest).responseType && (f = !0) : f = !0, f ? ((c = new window.XDomainRequest).onload = function() {
        p.success(c.responseText, c)
      }, c.onerror = function() {
        p.error("error", c)
      }, c.ontimeout = function() {
        p.error("timeout", c)
      }, c.onprogress = function() {
        a.logMessage("xhr onprogress")
      }) : c.onreadystatechange = function() {
        if (c.readyState === s) {
          var e = c.status;
          e >= 200 && e < 300 || 304 === e ? p.success(c.responseText, c) : p.error(c.statusText, c)
        }
      }, "GET" === l && t) {
        var g = (0, o.parse)(e, u);
        r(g.search, t), e = (0, o.format)(g)
      }
      c.open(l, e), c.timeout = d, f || (u.withCredentials && (c.withCredentials = !0), a._each(u.customHeaders, (function(e, n) {
        c.setRequestHeader(n, e)
      })), u.preflight && c.setRequestHeader("X-Requested-With", "XMLHttpRequest"), c.setRequestHeader("Content-Type", u.contentType || "text/plain")), c.send("POST" === l && t)
    } catch (e) {
      a.logError("xhr construction", e)
    }
  };
  var o = t(11),
    a = t(0),
    s = 4,
    d = 3e3
}), (function(e, n, t) {
  "use strict";
  Object.defineProperty(n, "__esModule", {
    value: !0
  }), n.
  default = function(e) {
    var n = e;
    return {
      callBids: function() {},
      setBidderCode: function(e) {
        n = e
      },
      getBidderCode: function() {
        return n
      }
    }
  }
}), (function(e, n, t) {
  "use strict";

  function r(e, n, t) {
    return n in e ? Object.defineProperty(e, n, {
      value: t,
      enumerable: !0,
      configurable: !0,
      writable: !0
    }) : e[n] = t, e
  }
  function i() {
    function e(e) {
      return Object.keys(b).find((function(n) {
        return e === b[n]
      }))
    }
    function n(n) {
      if (!n) return d.logError("Prebid Error: no value passed to `setPriceGranularity()`"), !1;
      if ("string" == typeof n) e(n) || d.logWarn("Prebid Warning: setPriceGranularity was called with invalid setting, using `medium` as default.");
      else if ("object" === (void 0 === n ? "undefined" : a(n)) && !(0, s.isValidPriceConfig)(n)) return d.logError("Invalid custom price value passed to `setPriceGranularity()`"), !1;
      return !0
    }
    function t(e, n) {
      var t = n;
      "string" != typeof e && (t = e, e = v); {
        if ("function" == typeof t) return m.push({
          topic: e,
          callback: t
        }),
        function() {
          m.splice(m.indexOf(n), 1)
        };
        d.logError("listener must be a function")
      }
    }
    function i(e) {
      var n = Object.keys(e);
      m.filter((function(e) {
        return n.includes(e.topic)
      })).forEach((function(n) {
        n.callback(r({}, n.topic, e[n.topic]))
      })), m.filter((function(e) {
        return e.topic === v
      })).forEach((function(n) {
        return n.callback(e)
      }))
    }
    var m = [],
      h = {
        _debug: u,
        get debug() {
          return pbjs.logging || !1 === pbjs.logging ? pbjs.logging : this._debug
        },
        set debug(e) {
          this._debug = e
        },
        _bidderTimeout: c,
        get bidderTimeout() {
          return pbjs.bidderTimeout || this._bidderTimeout
        },
        set bidderTimeout(e) {
          this._bidderTimeout = e
        },
        _publisherDomain: f,
        get publisherDomain() {
          return pbjs.publisherDomain || this._publisherDomain
        },
        set publisherDomain(e) {
          this._publisherDomain = e
        },
        _cookieSyncDelay: l,
        get cookieSyncDelay() {
          return pbjs.cookieSyncDelay || this._cookieSyncDelay
        },
        set cookieSyncDelay(e) {
          this._cookieSyncDelay = e
        },
        _priceGranularity: b.MEDIUM,
        set priceGranularity(t) {
          n(t) && ("string" == typeof t ? this._priceGranularity = e(t) ? t : b.MEDIUM : "object" === (void 0 === t ? "undefined" : a(t)) && (this._customPriceBucket = t, this._priceGranularity = b.CUSTOM, d.logMessage("Using custom price granularity")))
        },
        get priceGranularity() {
          return this._priceGranularity
        },
        _customPriceBucket: {},
        get customPriceBucket() {
          return this._customPriceBucket
        },
        _sendAllBids: p,
        get enableSendAllBids() {
          return this._sendAllBids
        },
        set enableSendAllBids(e) {
          this._sendAllBids = e
        },
        set bidderSequence(e) {
          pbjs.setBidderSequence(e)
        },
        set s2sConfig(e) {
          pbjs.setS2SConfig(e)
        },
        userSync: g
      };
    return {
      getConfig: function() {
        if (arguments.length <= 1 && "function" != typeof(arguments.length <= 0 ? void 0 : arguments[0])) {
          var e = arguments.length <= 0 ? void 0 : arguments[0];
          return e ? d.deepAccess(h, e) : h
        }
        return t.apply(void 0, arguments)
      },
      setConfig: function(e) {
        "object" !== (void 0 === e ? "undefined" : a(e)) && d.logError("setConfig options must be an object"), o(h, e), i(e)
      }
    }
  }
  Object.defineProperty(n, "__esModule", {
    value: !0
  }), n.config = void 0;
  var o = Object.assign || function(e) {
      for (var n = 1; n < arguments.length; n++) {
        var t = arguments[n];
        for (var r in t) Object.prototype.hasOwnProperty.call(t, r) && (e[r] = t[r])
      }
      return e
    }, a = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function(e) {
      return typeof e
    } : function(e) {
      return e && "function" == typeof Symbol && e.constructor === Symbol && e !== Symbol.prototype ? "symbol" : typeof e
    };
  n.newConfig = i;
  var s = t(26),
    d = t(0),
    u = !1,
    c = 3e3,
    f = window.location.origin,
    l = 100,
    p = !1,
    g = {
      syncEnabled: !0,
      pixelEnabled: !0,
      syncsPerBidder: 5,
      syncDelay: 3e3
    }, b = {
      LOW: "low",
      MEDIUM: "medium",
      HIGH: "high",
      AUTO: "auto",
      DENSE: "dense",
      CUSTOM: "custom"
    }, v = "*";
  n.config = i()
}), (function(e, n, t) {
  "use strict";
  var r = Object.assign || function(e) {
      for (var n = 1; n < arguments.length; n++) {
        var t = arguments[n];
        for (var r in t) Object.prototype.hasOwnProperty.call(t, r) && (e[r] = t[r])
      }
      return e
    }, i = t(0),
    o = t(4),
    a = Array.prototype.slice,
    s = Array.prototype.push,
    d = i._map(o.EVENTS, (function(e) {
      return e
    })),
    u = o.EVENT_ID_PATHS,
    c = [];
  e.exports = (function() {
    function e(e, n) {
      i.logMessage("Emitting event for: " + e);
      var r = n[0] || {}, o = r[u[e]],
        a = t[e] || {
          que: []
        }, d = i._map(a, (function(e, n) {
          return n
        })),
        f = [];
      c.push({
        eventType: e,
        args: r,
        id: o
      }), o && i.contains(d, o) && s.apply(f, a[o].que), s.apply(f, a.que), i._each(f, (function(e) {
        if (e) try {
          e.apply(null, n)
        } catch (e) {
          i.logError("Error executing handler:", "events.js", e)
        }
      }))
    }
    function n(e) {
      return i.contains(d, e)
    }
    var t = {}, o = {};
    return o.on = function(e, r, o) {
      if (n(e)) {
        var a = t[e] || {
          que: []
        };
        o ? (a[o] = a[o] || {
          que: []
        }, a[o].que.push(r)) : a.que.push(r), t[e] = a
      } else i.logError("Wrong event name : " + e + " Valid event names :" + d)
    }, o.emit = function(n) {
      e(n, a.call(arguments, 1))
    }, o.off = function(e, n, r) {
      var o = t[e];
      i.isEmpty(o) || i.isEmpty(o.que) && i.isEmpty(o[r]) || r && (i.isEmpty(o[r]) || i.isEmpty(o[r].que)) || (r ? i._each(o[r].que, (function(e) {
        var t = o[r].que;
        e === n && t.splice(i.indexOf.call(t, e), 1)
      })) : i._each(o.que, (function(e) {
        var t = o.que;
        e === n && t.splice(i.indexOf.call(t, e), 1)
      })), t[e] = o)
    }, o.get = function() {
      return t
    }, o.getEvents = function() {
      var e = [];
      return i._each(c, (function(n) {
        var t = r({}, n);
        e.push(t)
      })), e
    }, o
  })()
}), , (function(e, n, t) {
  "use strict";

  function r(e) {
    return e ? e.replace(/^\?/, "").split("&").reduce((function(e, n) {
      var t = n.split("="),
        r = o(t, 2),
        i = r[0],
        a = r[1];
      return /\[\]$/.test(i) ? (e[i = i.replace("[]", "")] = e[i] || [], e[i].push(a)) : e[i] = a || "", e
    }), {}) : {}
  }
  function i(e) {
    return Object.keys(e).map((function(n) {
      return Array.isArray(e[n]) ? e[n].map((function(e) {
        return n + "[]=" + e
      })).join("&") : n + "=" + e[n]
    })).join("&")
  }
  Object.defineProperty(n, "__esModule", {
    value: !0
  });
  var o = (function() {
    function e(e, n) {
      var t = [],
        r = !0,
        i = !1,
        o = void 0;
      try {
        for (var a, s = e[Symbol.iterator](); !(r = (a = s.next()).done) && (t.push(a.value), !n || t.length !== n); r = !0);
      } catch (e) {
        i = !0, o = e
      } finally {
        try {
          !r && s.
          return &&s.
          return ()
        } finally {
          if (i) throw o
        }
      }
      return t
    }
    return function(n, t) {
      if (Array.isArray(n)) return n;
      if (Symbol.iterator in Object(n)) return e(n, t);
      throw new TypeError("Invalid attempt to destructure non-iterable instance")
    }
  })();
  n.parseQS = r, n.formatQS = i, n.parse = function(e, n) {
    var t = document.createElement("a");
    return n && "noDecodeWholeURL" in n && n.noDecodeWholeURL ? t.href = e : t.href = decodeURIComponent(e), {
      protocol: (t.protocol || "").replace(/:$/, ""),
      hostname: t.hostname,
      port: +t.port,
      pathname: t.pathname.replace(/^(?!\/)/, "/"),
      search: r(t.search || ""),
      hash: (t.hash || "").replace(/^#/, ""),
      host: t.host
    }
  }, n.format = function(e) {
    return (e.protocol || "http") + "://" + (e.host || e.hostname + (e.port ? ":" + e.port : "")) + (e.pathname || "") + (e.search ? "?" + i(e.search || "") : "") + (e.hash ? "#" + e.hash : "")
  }
}), (function(e, n) {
  var t = e.exports = {
    version: "2.4.0"
  };
  "number" == typeof __e && (__e = t)
}), (function(e, n, t) {
  "use strict";

  function r(e) {
    return !(!e || !Object.keys(s).includes(e)) || ((0, i.logError)(e + " nativeParam is not supported"), !1)
  }
  Object.defineProperty(n, "__esModule", {
    value: !0
  }), n.hasNonNativeBidder = n.nativeBidder = n.nativeAdUnit = n.NATIVE_TARGETING_KEYS = n.NATIVE_KEYS = n.nativeAdapters = void 0, n.processNativeAdUnitParams = function(e) {
    return e && e.type && r(e.type) ? s[e.type] : e
  }, n.nativeBidIsValid = function(e) {
    var n = (0, i.getBidRequest)(e.adId);
    if (!n) return !1;
    var t = n.nativeParams;
    if (!t) return !0;
    var r = Object.keys(t).filter((function(e) {
      return t[e].required
    })),
      o = Object.keys(e.native).filter((function(n) {
        return e.native[n]
      }));
    return r.every((function(e) {
      return o.includes(e)
    }))
  }, n.fireNativeImpressions = function(e) {
    (e.native && e.native.impressionTrackers || []).forEach((function(e) {
      (0, i.triggerPixel)(e)
    }))
  };
  var i = t(0),
    o = n.nativeAdapters = [],
    a = n.NATIVE_KEYS = {
      title: "hb_native_title",
      body: "hb_native_body",
      sponsoredBy: "hb_native_brand",
      image: "hb_native_image",
      icon: "hb_native_icon",
      clickUrl: "hb_native_linkurl",
      cta: "hb_native_cta"
    }, s = (n.NATIVE_TARGETING_KEYS = Object.keys(a).map((function(e) {
      return a[e]
    })), {
      image: {
        image: {
          required: !0
        },
        title: {
          required: !0
        },
        sponsoredBy: {
          required: !0
        },
        clickUrl: {
          required: !0
        },
        body: {
          required: !1
        },
        icon: {
          required: !1
        }
      }
    }),
    d = (n.nativeAdUnit = function(e) {
      return "native" === e.mediaType
    }, n.nativeBidder = function(e) {
      return o.includes(e.bidder)
    });
  n.hasNonNativeBidder = function(e) {
    return e.bids.filter((function(e) {
      return !d(e)
    })).length
  }
}), (function(e, n) {
  var t = e.exports = "undefined" != typeof window && window.Math == Math ? window : "undefined" != typeof self && self.Math == Math ? self : Function("return this")();
  "number" == typeof __g && (__g = t)
}), (function(e, n, t) {
  "use strict";

  function r(e) {
    function n() {
      return {
        image: [],
        iframe: []
      }
    }
    function t() {
      if (l.syncEnabled && e.browserSupportsCookies && !c) {
        try {
          r(), a()
        } catch (e) {
          return o.logError("Error firing user syncs", e)
        }
        u = n(), c = !0
      }
    }
    function r() {
      l.pixelEnabled && o.shuffle(u.image).forEach((function(e) {
        var n = i(e, 2),
          t = n[0],
          r = n[1];
        o.logMessage("Invoking image pixel user sync for bidder: " + t), o.triggerPixel(r)
      }))
    }
    function a() {
      l.iframeEnabled && o.shuffle(u.iframe).forEach((function(e) {
        var n = i(e, 2),
          t = n[0],
          r = n[1];
        o.logMessage("Invoking iframe user sync for bidder: " + t), o.insertUserSyncIframe(r)
      }))
    }
    function s(e, n) {
      return e[n] ? e[n] += 1 : e[n] = 1, e
    }
    var d = {}, u = n(),
      c = !1,
      f = {}, l = e.config;
    return d.registerSync = function(e, n, t) {
      return l.syncEnabled && o.isArray(u[e]) ? n ? Number(f[n]) >= l.syncsPerBidder ? o.logWarn('Number of user syncs exceeded for "{$bidder}"') : l.enabledBidders && l.enabledBidders.length && l.enabledBidders.indexOf(n) < 0 ? o.logWarn('Bidder "' + n + '" not supported') : (u[e].push([n, t]), void(f = s(f, n))) : o.logWarn("Bidder is required for registering sync") : o.logWarn('User sync type "{$type}" not supported')
    }, d.syncUsers = function() {
      var e = arguments.length > 0 && void 0 !== arguments[0] ? arguments[0] : 0;
      if (e) return window.setTimeout(t, Number(e));
      t()
    }, d.triggerUserSyncs = function() {
      l.enableOverride && d.syncUsers()
    }, d
  }
  Object.defineProperty(n, "__esModule", {
    value: !0
  }), n.userSync = void 0;
  var i = (function() {
    function e(e, n) {
      var t = [],
        r = !0,
        i = !1,
        o = void 0;
      try {
        for (var a, s = e[Symbol.iterator](); !(r = (a = s.next()).done) && (t.push(a.value), !n || t.length !== n); r = !0);
      } catch (e) {
        i = !0, o = e
      } finally {
        try {
          !r && s.
          return &&s.
          return ()
        } finally {
          if (i) throw o
        }
      }
      return t
    }
    return function(n, t) {
      if (Array.isArray(n)) return n;
      if (Symbol.iterator in Object(n)) return e(n, t);
      throw new TypeError("Invalid attempt to destructure non-iterable instance")
    }
  })();
  n.newUserSync = r;
  var o = (function(e) {
    if (e && e.__esModule) return e;
    var n = {};
    if (null != e) for (var t in e) Object.prototype.hasOwnProperty.call(e, t) && (n[t] = e[t]);
    return n.
    default = e, n
  })(t(0)),
    a = t(8),
    s = !o.isSafariBrowser() && o.cookiesAreEnabled();
  n.userSync = r({
    config: a.config.getConfig("userSync"),
    browserSupportsCookies: s
  })
}), (function(e, n, t) {
  var r = t(14),
    i = t(12),
    o = t(20),
    a = t(268),
    s = t(32),
    d = function(e, n, t) {
      var u, c, f, l, p = e & d.F,
        g = e & d.G,
        b = e & d.S,
        v = e & d.P,
        m = e & d.B,
        h = g ? r : b ? r[n] || (r[n] = {}) : (r[n] || {}).prototype,
        y = g ? i : i[n] || (i[n] = {}),
        E = y.prototype || (y.prototype = {});
      g && (t = n);
      for (u in t) f = ((c = !p && h && void 0 !== h[u]) ? h : t)[u], l = m && c ? s(f, r) : v && "function" == typeof f ? s(Function.call, f) : f, h && a(h, u, f, e & d.U), y[u] != f && o(y, u, l), v && E[u] != f && (E[u] = f)
    };
  r.core = i, d.F = 1, d.G = 2, d.S = 4, d.P = 8, d.B = 16, d.W = 32, d.U = 64, d.R = 128, e.exports = d
}), (function(e, n) {
  e.exports = function(e) {
    return "object" == typeof e ? null !== e : "function" == typeof e
  }
}), (function(e, n, t) {
  "use strict";

  function r(e) {
    var n = this,
      t = e.url,
      r = e.config,
      a = e.id,
      s = e.callback,
      d = e.loaded;
    this.url = t, this.config = r, this.handlers = {}, this.id = a, this.loaded = d, this.cmd = [], this.push = function(e) {
      "function" == typeof e ? n.loaded ? e.call() : n.cmd.push(e) : o.logError("Commands given to Renderer.push must be wrapped in a function")
    }, this.callback = s || function() {
      n.loaded = !0, n.process()
    }, (0, i.loadScript)(t, this.callback, !0)
  }
  Object.defineProperty(n, "__esModule", {
    value: !0
  }), n.Renderer = r;
  var i = t(5),
    o = (function(e) {
      if (e && e.__esModule) return e;
      var n = {};
      if (null != e) for (var t in e) Object.prototype.hasOwnProperty.call(e, t) && (n[t] = e[t]);
      return n.
      default = e, n
    })(t(0));
  r.install = function(e) {
    return new r({
      url: e.url,
      config: e.config,
      id: e.id,
      callback: e.callback,
      loaded: e.loaded
    })
  }, r.prototype.getConfig = function() {
    return this.config
  }, r.prototype.setRender = function(e) {
    this.render = e
  }, r.prototype.setEventHandlers = function(e) {
    this.handlers = e
  }, r.prototype.handleVideoEvent = function(e) {
    var n = e.id,
      t = e.eventName;
    "function" == typeof this.handlers[t] && this.handlers[t](), o.logMessage("Prebid Renderer event for id " + n + " type " + t)
  }, r.prototype.process = function() {
    for (; this.cmd.length > 0;) try {
      this.cmd.shift().call()
    } catch (e) {
      o.logError("Error processing Renderer command: ", e)
    }
  }
}), (function(e, n, t) {
  "use strict";

  function r(e, n, t) {
    return n in e ? Object.defineProperty(e, n, {
      value: t,
      enumerable: !0,
      configurable: !0,
      writable: !0
    }) : e[n] = t, e
  }
  function i(e) {
    return "string" == typeof e ? [e] : b.isArray(e) ? e : pbjs._adUnitCodes || []
  }
  function o(e) {
    var n = m.getWinningBids(e),
      t = a();
    return n = n.map((function(e) {
      return r({}, e.adUnitCode, Object.keys(e.adserverTargeting).filter((function(n) {
        return void 0 === e.sendStandardTargeting || e.sendStandardTargeting || -1 === t.indexOf(n)
      })).map((function(n) {
        return r({}, n.substring(0, 20), [e.adserverTargeting[n]])
      })))
    }))
  }
  function a() {
    return g.getStandardBidderAdServerTargeting().map((function(e) {
      return e.key
    })).concat(v.TARGETING_KEYS).filter(f.uniques)
  }
  function s(e) {
    var n = a();
    return pbjs._bidsReceived.filter(f.adUnitsFilter.bind(this, e)).map((function(e) {
      if (e.alwaysUseBid) return r({}, e.adUnitCode, Object.keys(e.adserverTargeting).map((function(t) {
        if (!(n.indexOf(t) > -1)) return r({}, t.substring(0, 20), [e.adserverTargeting[t]])
      })).filter((function(e) {
        return e
      })))
    })).filter((function(e) {
      return e
    }))
  }
  function d(e) {
    var n = v.TARGETING_KEYS.concat(p.NATIVE_TARGETING_KEYS),
      t = [],
      i = (0, f.groupBy)(pbjs._bidsReceived, "adUnitCode");
    return Object.keys(i).forEach((function(e) {
      var n = (0, f.groupBy)(i[e], "bidderCode");
      Object.keys(n).forEach((function(e) {
        return t.push(n[e].reduce(f.getHighestCpm, c()))
      }))
    })), t.map((function(e) {
      if (e.adserverTargeting) return r({}, e.adUnitCode, u(e, n.filter((function(n) {
        return void 0 !== e.adserverTargeting[n]
      }))))
    })).filter((function(e) {
      return e
    }))
  }
  function u(e, n) {
    return n.map((function(n) {
      return r({}, (n + "_" + e.bidderCode).substring(0, 20), [e.adserverTargeting[n]])
    }))
  }
  function c(e) {
    return {
      adUnitCode: e,
      cpm: 0,
      adserverTargeting: {},
      timeToRespond: 0
    }
  }
  var f = t(0),
    l = t(8),
    p = t(13),
    g = t(2),
    b = t(0),
    v = t(4),
    m = n,
    h = [];
  m.resetPresetTargeting = function(e) {
    if ((0, f.isGptPubadsDefined)()) {
      var n = i(e),
        t = pbjs.adUnits.filter((function(e) {
          return n.includes(e.code)
        }));
      window.googletag.pubads().getSlots().forEach((function(e) {
        h.forEach((function(n) {
          t.forEach((function(t) {
            t.code !== e.getAdUnitPath() && t.code !== e.getSlotElementId() || e.setTargeting(n, null)
          }))
        }))
      }))
    }
  }, m.getAllTargeting = function(e) {
    var n = i(e),
      t = o(n).concat(s(n)).concat(l.config.getConfig("enableSendAllBids") ? d() : []);
    return t.map((function(e) {
      Object.keys(e).map((function(n) {
        e[n].map((function(e) {
          -1 === h.indexOf(Object.keys(e)[0]) && (h = Object.keys(e).concat(h))
        }))
      }))
    })), t
  }, m.setTargeting = function(e) {
    window.googletag.pubads().getSlots().forEach((function(n) {
      e.filter((function(e) {
        return Object.keys(e)[0] === n.getAdUnitPath() || Object.keys(e)[0] === n.getSlotElementId()
      })).forEach((function(e) {
        return e[Object.keys(e)[0]].forEach((function(e) {
          e[Object.keys(e)[0]].map((function(t) {
            return b.logMessage("Attempting to set key value for slot: " + n.getSlotElementId() + " key: " + Object.keys(e)[0] + " value: " + t), t
          })).forEach((function(t) {
            n.setTargeting(Object.keys(e)[0], t)
          }))
        }))
      }))
    }))
  }, m.getWinningBids = function(e) {
    var n = i(e);
    return pbjs._bidsReceived.filter((function(e) {
      return n.includes(e.adUnitCode)
    })).filter((function(e) {
      return e.cpm > 0
    })).map((function(e) {
      return e.adUnitCode
    })).filter(f.uniques).map((function(e) {
      return pbjs._bidsReceived.filter((function(n) {
        return n.adUnitCode === e ? n : null
      })).reduce(f.getHighestCpm, c(e))
    }))
  }, m.setTargetingForAst = function() {
    var e = pbjs.getAdserverTargeting();
    Object.keys(e).forEach((function(n) {
      return Object.keys(e[n]).forEach((function(t) {
        if (b.logMessage("Attempting to set targeting for targetId: " + n + " key: " + t + " value: " + e[n][t]), b.isStr(e[n][t]) || b.isArray(e[n][t])) {
          var r = {};
          r["hb_adid" === t.substring(0, "hb_adid".length) ? t.toUpperCase() : t] = e[n][t], window.apntag.setKeywords(n, r)
        }
      }))
    }))
  }, m.isApntagDefined = function() {
    if (window.apntag && b.isFn(window.apntag.setKeywords)) return !0
  }
}), (function(e, n, t) {
  var r = t(262),
    i = t(267);
  e.exports = t(21) ? function(e, n, t) {
    return r.f(e, n, i(1, t))
  } : function(e, n, t) {
    return e[n] = t, e
  }
}), (function(e, n, t) {
  e.exports = !t(22)((function() {
    return 7 != Object.defineProperty({}, "a", {
      get: function() {
        return 7
      }
    }).a
  }))
}), (function(e, n) {
  e.exports = function(e) {
    try {
      return !!e()
    } catch (e) {
      return !0
    }
  }
}), (function(e, n) {
  var t = 0,
    r = Math.random();
  e.exports = function(e) {
    return "Symbol(".concat(void 0 === e ? "" : e, ")_", (++t + r).toString(36))
  }
}), (function(e, n, t) {
  var r = t(34);
  e.exports = Object("z").propertyIsEnumerable(0) ? Object : function(e) {
    return "String" == r(e) ? e.split("") : Object(e)
  }
}), (function(e, n, t) {
  var r = t(39)("unscopables"),
    i = Array.prototype;
  void 0 == i[r] && t(20)(i, r, {}), e.exports = function(e) {
    i[r][e] = !0
  }
}), (function(e, n, t) {
  "use strict";

  function r(e, n, t) {
    var r = "";
    if (!i(n)) return r;
    var a = n.buckets.reduce((function(e, n) {
      return e.max > n.max ? e : n
    }), {
      max: 0
    }),
      d = n.buckets.find((function(n) {
        if (e > a.max * t) {
          var i = n.precision;
          void 0 === i && (i = s), r = (n.max * t).toFixed(i)
        } else if (e <= n.max * t && e >= n.min * t) return n
      }));
    return d && (r = o(e, d.increment, d.precision, t)), r
  }
  function i(e) {
    if (a.isEmpty(e) || !e.buckets || !Array.isArray(e.buckets)) return !1;
    var n = !0;
    return e.buckets.forEach((function(e) {
      void 0 !== e.min && e.max && e.increment || (n = !1)
    })), n
  }
  function o(e, n, t, r) {
    void 0 === t && (t = s);
    var i = 1 / (n * r);
    return (Math.floor(e * i) / i).toFixed(t)
  }
  Object.defineProperty(n, "__esModule", {
    value: !0
  });
  var a = t(0),
    s = 2,
    d = {
      buckets: [{
        min: 0,
        max: 5,
        increment: .5
      }]
    }, u = {
      buckets: [{
        min: 0,
        max: 20,
        increment: .1
      }]
    }, c = {
      buckets: [{
        min: 0,
        max: 20,
        increment: .01
      }]
    }, f = {
      buckets: [{
        min: 0,
        max: 3,
        increment: .01
      }, {
        min: 3,
        max: 8,
        increment: .05
      }, {
        min: 8,
        max: 20,
        increment: .5
      }]
    }, l = {
      buckets: [{
        min: 0,
        max: 5,
        increment: .05
      }, {
        min: 5,
        max: 10,
        increment: .1
      }, {
        min: 10,
        max: 20,
        increment: .5
      }]
    };
  n.getPriceBucketString = function(e, n) {
    var t = arguments.length > 2 && void 0 !== arguments[2] ? arguments[2] : 1,
      i = parseFloat(e);
    return isNaN(i) && (i = ""), {
      low: "" === i ? "" : r(e, d, t),
      med: "" === i ? "" : r(e, u, t),
      high: "" === i ? "" : r(e, c, t),
      auto: "" === i ? "" : r(e, l, t),
      dense: "" === i ? "" : r(e, f, t),
      custom: "" === i ? "" : r(e, n, t)
    }
  }, n.isValidPriceConfig = i
}), (function(e, n, t) {
  "use strict";
  Object.defineProperty(n, "__esModule", {
    value: !0
  }), n.hasNonVideoBidder = n.videoAdUnit = void 0, n.isValidVideoBid = function(e) {
    var n = (0, i.getBidRequest)(e.adId),
      t = n && (0, i.deepAccess)(n, "mediaTypes.video"),
      r = t && (0, i.deepAccess)(t, "context");
    return !n || t && r !== o ? !(!e.vastUrl && !e.vastXml) : r !== o || !(!e.renderer && !n.renderer)
  };
  var r = t(1),
    i = t(0),
    o = "outstream",
    a = (n.videoAdUnit = function(e) {
      return "video" === e.mediaType
    }, function(e) {
      return !r.videoAdapters.includes(e.bidder)
    });
  n.hasNonVideoBidder = function(e) {
    return e.bids.filter(a).length
  }
}), (function(e, n, t) {
  "use strict";

  function r() {
    function e(e, n) {
      try {
        localStorage.setItem(e, JSON.stringify(n))
      } catch (e) {
        (0, i.logWarn)("could not set storage item: ", e)
      }
    }
    function n(e) {
      try {
        var n = JSON.parse(localStorage.getItem(e));
        return n && n.length ? n : []
      } catch (e) {
        return (0, i.logWarn)("could not get storage item: ", e), []
      }
    }
    return {
      get: n,
      set: e,
      add: function(t, r) {
        var i = arguments.length > 2 && void 0 !== arguments[2] && arguments[2];
        e(t, n(t).concat([r]).filter((function(e, n, t) {
          return !i || t.indexOf(e) === n
        })))
      },
      remove: function(t, r) {
        e(t, n(t).filter((function(e) {
          return e !== r
        })))
      }
    }
  }
  Object.defineProperty(n, "__esModule", {
    value: !0
  }), n.StorageManager = n.pbjsSyncsKey = void 0, n.newStorageManager = r;
  var i = t(0);
  n.pbjsSyncsKey = "pbjsSyncs", n.StorageManager = r()
}), (function(e, n) {
  var t;
  t = (function() {
    return this
  })();
  try {
    t = t || Function("return this")() || (0, eval)("this")
  } catch (e) {
    "object" == typeof window && (t = window)
  }
  e.exports = t
}), (function(e, n, t) {
  "use strict";
  Object.defineProperty(n, "__esModule", {
    value: !0
  }), n.getGlobal = function() {
    return window.pbjs
  }, window.pbjs = window.pbjs || {}, window.pbjs.cmd = window.pbjs.cmd || [], window.pbjs.que = window.pbjs.que || []
}), (function(e, n) {
  var t = {}.hasOwnProperty;
  e.exports = function(e, n) {
    return t.call(e, n)
  }
}), (function(e, n, t) {
  var r = t(269);
  e.exports = function(e, n, t) {
    if (r(e), void 0 === n) return e;
    switch (t) {
      case 1:
        return function(t) {
          return e.call(n, t)
        };
      case 2:
        return function(t, r) {
          return e.call(n, t, r)
        };
      case 3:
        return function(t, r, i) {
          return e.call(n, t, r, i)
        }
    }
    return function() {
      return e.apply(n, arguments)
    }
  }
}), (function(e, n, t) {
  var r = t(32),
    i = t(24),
    o = t(35),
    a = t(37),
    s = t(270);
  e.exports = function(e, n) {
    var t = 1 == e,
      d = 2 == e,
      u = 3 == e,
      c = 4 == e,
      f = 6 == e,
      l = 5 == e || f,
      p = n || s;
    return function(n, s, g) {
      for (var b, v, m = o(n), h = i(m), y = r(s, g, 3), E = a(h.length), S = 0, T = t ? p(n, E) : d ? p(n, 0) : void 0; E > S; S++) if ((l || S in h) && (b = h[S], v = y(b, S, m), e)) if (t) T[S] = v;
      else if (v) switch (e) {
        case 3:
          return !0;
        case 5:
          return b;
        case 6:
          return S;
        case 2:
          T.push(b)
      } else if (c) return !1;
      return f ? -1 : u || c ? c : T
    }
  }
}), (function(e, n) {
  var t = {}.toString;
  e.exports = function(e) {
    return t.call(e).slice(8, -1)
  }
}), (function(e, n, t) {
  var r = t(36);
  e.exports = function(e) {
    return Object(r(e))
  }
}), (function(e, n) {
  e.exports = function(e) {
    if (void 0 == e) throw TypeError("Can't call method on  " + e);
    return e
  }
}), (function(e, n, t) {
  var r = t(38),
    i = Math.min;
  e.exports = function(e) {
    return e > 0 ? i(r(e), 9007199254740991) : 0
  }
}), (function(e, n) {
  var t = Math.ceil,
    r = Math.floor;
  e.exports = function(e) {
    return isNaN(e = +e) ? 0 : (e > 0 ? r : t)(e)
  }
}), (function(e, n, t) {
  var r = t(40)("wks"),
    i = t(23),
    o = t(14).Symbol,
    a = "function" == typeof o;
  (e.exports = function(e) {
    return r[e] || (r[e] = a && o[e] || (a ? o : i)("Symbol." + e))
  }).store = r
}), (function(e, n, t) {
  var r = t(14),
    i = r["__core-js_shared__"] || (r["__core-js_shared__"] = {});
  e.exports = function(e) {
    return i[e] || (i[e] = {})
  }
}), (function(e, n, t) {
  var r = t(42),
    i = t(37),
    o = t(277);
  e.exports = function(e) {
    return function(n, t, a) {
      var s, d = r(n),
        u = i(d.length),
        c = o(a, u);
      if (e && t != t) {
        for (; u > c;) if ((s = d[c++]) != s) return !0
      } else for (; u > c; c++) if ((e || c in d) && d[c] === t) return e || c || 0;
      return !e && -1
    }
  }
}), (function(e, n, t) {
  var r = t(24),
    i = t(36);
  e.exports = function(e) {
    return r(i(e))
  }
}), , , (function(e, n, t) {
  "use strict";

  function r(e) {
    return !!(o.isArray(e) && e.length > 0) || (o.logInfo("No size mapping defined"), !1)
  }
  function i(e) {
    var n = e || a || window,
      t = n.document;
    return n.innerWidth ? n.innerWidth : t.body.clientWidth ? t.body.clientWidth : t.documentElement.clientWidth ? t.documentElement.clientWidth : 0
  }
  Object.defineProperty(n, "__esModule", {
    value: !0
  }), n.setWindow = n.getScreenWidth = n.mapSizes = void 0;
  var o = (function(e) {
    if (e && e.__esModule) return e;
    var n = {};
    if (null != e) for (var t in e) Object.prototype.hasOwnProperty.call(e, t) && (n[t] = e[t]);
    return n.
    default = e, n
  })(t(0)),
    a = void 0;
  n.mapSizes = function(e) {
    if (!r(e.sizeMapping)) return e.sizes;
    var n = i();
    if (!n) {
      var t = e.sizeMapping.reduce((function(e, n) {
        return e.minWidth < n.minWidth ? n : e
      }));
      return t.sizes && t.sizes.length ? t.sizes : e.sizes
    }
    var a = "",
      s = e.sizeMapping.find((function(e) {
        return n >= e.minWidth
      }));
    return s && s.sizes && s.sizes.length ? (a = s.sizes, o.logMessage("AdUnit : " + e.code + " resized based on device width to : " + a)) : o.logMessage("AdUnit : " + e.code + " not mapped to any sizes for device width. This request will be suppressed."), a
  }, n.getScreenWidth = i, n.setWindow = function(e) {
    a = e
  }
}), (function(e, n, t) {
  "use strict";

  function r(e) {
    return '<VAST version="3.0">\n    <Ad>\n      <Wrapper>\n        <AdSystem>prebid.org wrapper</AdSystem>\n        <VASTAdTagURI><![CDATA[' + e + "]]></VASTAdTagURI>\n        <Impression></Impression>\n        <Creatives></Creatives>\n      </Wrapper>\n    </Ad>\n  </VAST>"
  }
  function i(e) {
    return {
      type: "xml",
      value: e.vastXml ? e.vastXml : r(e.vastUrl)
    }
  }
  function o(e) {
    return {
      success: function(n) {
        var t = void 0;
        try {
          t = JSON.parse(n).responses
        } catch (n) {
          return void e(n, [])
        }
        t ? e(null, t) : e(new Error("The cache server didn't respond with a responses property."), [])
      },
      error: function(n, t) {
        e(new Error("Error storing video ad in the cache: " + n + ": " + JSON.stringify(t)), [])
      }
    }
  }
  Object.defineProperty(n, "__esModule", {
    value: !0
  }), n.store = function(e, n) {
    var t = {
      puts: e.map(i)
    };
    (0, a.ajax)(s, o(n), JSON.stringify(t), {
      contentType: "text/plain",
      withCredentials: !0
    })
  }, n.getCacheUrl = function(e) {
    return s + "?uuid=" + e
  };
  var a = t(6),
    s = "https://prebid.adnxs.com/pbc/v1/cache"
}), , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , (function(e, n, t) {
  e.exports = t(258)
}), (function(e, n, t) {
  "use strict";

  function r(e, n, t) {
    return n in e ? Object.defineProperty(e, n, {
      value: t,
      enumerable: !0,
      configurable: !0,
      writable: !0
    }) : e[n] = t, e
  }
  function i() {
    y._bidsRequested = [], y._bidsReceived = y._bidsReceived.filter((function(e) {
      return !y._adUnitCodes.includes(e.adUnitCode)
    }))
  }
  function o(e, n, t) {
    e.defaultView && e.defaultView.frameElement && (e.defaultView.frameElement.width = n, e.defaultView.frameElement.height = t)
  }
  function a(e) {
    e.forEach((function(e) {
      if (void 0 === e.called) try {
        e.call(), e.called = !0
      } catch (e) {
        S.logError("Error processing command :", "prebid.js", e)
      }
    }))
  }
  var s = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function(e) {
      return typeof e
    } : function(e) {
      return e && "function" == typeof Symbol && e.constructor === Symbol && e !== Symbol.prototype ? "symbol" : typeof e
    }, d = Object.assign || function(e) {
      for (var n = 1; n < arguments.length; n++) {
        var t = arguments[n];
        for (var r in t) Object.prototype.hasOwnProperty.call(t, r) && (e[r] = t[r])
      }
      return e
    }, u = t(30),
    c = t(0),
    f = t(27),
    l = t(13);
  t(259);
  var p = t(11),
    g = t(287),
    b = t(15),
    v = t(5),
    m = t(6),
    h = t(8),
    y = (0, u.getGlobal)(),
    E = t(4),
    S = t(0),
    T = t(2),
    A = t(1),
    I = t(3),
    _ = t(9),
    w = t(288),
    j = t(19),
    C = b.userSync.syncUsers,
    O = b.userSync.triggerUserSyncs,
    U = E.EVENTS.BID_WON,
    B = E.EVENTS.SET_TARGETING,
    P = !1,
    N = [],
    R = {
      bidWon: function(e) {
        var n = y._bidsRequested.map((function(e) {
          return e.bids.map((function(e) {
            return e.placementCode
          }))
        })).reduce(c.flatten).filter(c.uniques); {
          if (S.contains(n, e)) return !0;
          S.logError('The "' + e + '" placement is not defined.')
        }
      }
    };
  y._bidsRequested = [], y._bidsReceived = [], y._adUnitCodes = [], y._winningBids = [], y._adsReceived = [], y.bidderSettings = y.bidderSettings || {}, y.bidderTimeout = y.bidderTimeout, y.cbTimeout = y.cbTimeout || 200, y.timeoutBuffer = 200, y.logging = y.logging, y.publisherDomain = y.publisherDomain, y.libLoaded = !0, y.version = "v0.29.0", S.logInfo("Prebid.js v0.29.0 loaded"), y.adUnits = y.adUnits || [], y.triggerUserSyncs = O, y.getAdserverTargetingForAdUnitCodeStr = function(e) {
    if (S.logInfo("Invoking pbjs.getAdserverTargetingForAdUnitCodeStr", arguments), e) {
      var n = y.getAdserverTargetingForAdUnitCode(e);
      return S.transformAdServerTargetingObj(n)
    }
    S.logMessage("Need to call getAdserverTargetingForAdUnitCodeStr with adunitCode")
  }, y.getAdserverTargetingForAdUnitCode = function(e) {
    return y.getAdserverTargeting(e)[e]
  }, y.getAdserverTargeting = function(e) {
    return S.logInfo("Invoking pbjs.getAdserverTargeting", arguments), j.getAllTargeting(e).map((function(e) {
      return r({}, Object.keys(e)[0], e[Object.keys(e)[0]].map((function(e) {
        return r({}, Object.keys(e)[0], e[Object.keys(e)[0]].join(", "))
      })).reduce((function(e, n) {
        return d(n, e)
      }), {}))
    })).reduce((function(e, n) {
      var t = Object.keys(n)[0];
      return e[t] = d({}, e[t], n[t]), e
    }), {})
  }, y.getBidResponses = function() {
    S.logInfo("Invoking pbjs.getBidResponses", arguments);
    var e = y._bidsReceived.filter(c.adUnitsFilter.bind(this, y._adUnitCodes)),
      n = e && e.length && e[e.length - 1].requestId;
    return e.map((function(e) {
      return e.adUnitCode
    })).filter(c.uniques).map((function(t) {
      return e.filter((function(e) {
        return e.requestId === n && e.adUnitCode === t
      }))
    })).filter((function(e) {
      return e && e[0] && e[0].adUnitCode
    })).map((function(e) {
      return r({}, e[0].adUnitCode, {
        bids: e
      })
    })).reduce((function(e, n) {
      return d(e, n)
    }), {})
  }, y.getBidResponsesForAdUnitCode = function(e) {
    return {
      bids: y._bidsReceived.filter((function(n) {
        return n.adUnitCode === e
      }))
    }
  }, y.setTargetingForGPTAsync = function(e) {
    if (S.logInfo("Invoking pbjs.setTargetingForGPTAsync", arguments), (0, c.isGptPubadsDefined)()) {
      var n = j.getAllTargeting(e);
      j.resetPresetTargeting(e), j.setTargeting(n), _.emit(B)
    } else S.logError("window.googletag is not defined on the page")
  }, y.setTargetingForAst = function() {
    S.logInfo("Invoking pbjs.setTargetingForAn", arguments), j.isApntagDefined() ? (j.setTargetingForAst(), _.emit(B)) : S.logError("window.apntag is not defined on the page")
  }, y.allBidsAvailable = function() {
    return S.logWarn("pbjs.allBidsAvailable will be removed in Prebid 1.0. Alternative solution is in progress. See https://github.com/prebid/Prebid.js/issues/1087 for more details."), S.logInfo("Invoking pbjs.allBidsAvailable", arguments), T.bidsBackAll()
  }, y.renderAd = function(e, n) {
    if (S.logInfo("Invoking pbjs.renderAd", arguments), S.logMessage("Calling renderAd with adId :" + n), e && n) try {
      var t = y._bidsReceived.find((function(e) {
        return e.adId === n
      }));
      if (t) {
        t.ad = S.replaceAuctionPrice(t.ad, t.cpm), t.url = S.replaceAuctionPrice(t.url, t.cpm), y._winningBids.push(t), _.emit(U, t);
        var r = t.height,
          i = t.width,
          a = t.ad,
          s = t.mediaType,
          d = t.adUrl,
          u = t.renderer;
        if (u && u.url) u.render(t);
        else if (e === document && !S.inIframe() || "video" === s) S.logError("Error trying to write ad. Ad render call ad id " + n + " was prevented from writing to the main document.");
        else if (a) e.write(a), e.close(), o(e, i, r);
        else if (d) {
          var c = S.createInvisibleIframe();
          c.height = r, c.width = i, c.style.display = "inline", c.style.overflow = "hidden", c.src = d, S.insertElement(c, e, "body"), o(e, i, r)
        } else S.logError("Error trying to write ad. No ad for bid response id: " + n)
      } else S.logError("Error trying to write ad. Cannot find ad by given id : " + n)
    } catch (e) {
      S.logError("Error trying to write ad Id :" + n + " to the page:" + e.message)
    } else S.logError("Error trying to write ad Id :" + n + " to the page. Missing document or adId")
  }, y.removeAdUnit = function(e) {
    if (S.logInfo("Invoking pbjs.removeAdUnit", arguments), e) for (var n = 0; n < y.adUnits.length; n++) y.adUnits[n].code === e && y.adUnits.splice(n, 1)
  }, y.clearAuction = function() {
    P = !1;
    var e = h.config.getConfig("userSync") || {};
    e.enableOverride || C(e.syncDelay), S.logMessage("Prebid auction cleared"), N.length && N.shift()()
  }, y.requestBids = function() {
    var e = arguments.length > 0 && void 0 !== arguments[0] ? arguments[0] : {}, n = e.bidsBackHandler,
      t = e.timeout,
      r = e.adUnits,
      o = e.adUnitCodes;
    _.emit("requestBids");
    var a = y.cbTimeout = t || h.config.getConfig("bidderTimeout");
    if (r = r || y.adUnits, S.logInfo("Invoking pbjs.requestBids", arguments), o && o.length ? r = r.filter((function(e) {
      return o.includes(e.code)
    })) : o = r && r.map((function(e) {
      return e.code
    })), r.filter(f.videoAdUnit).filter(f.hasNonVideoBidder).forEach((function(e) {
      S.logError("adUnit " + e.code + " has 'mediaType' set to 'video' but contains a bidder that doesn't support video. No Prebid demand requests will be triggered for this adUnit.");
      for (var n = 0; n < r.length; n++) r[n].code === e.code && r.splice(n, 1)
    })), r.filter(l.nativeAdUnit).filter(l.hasNonNativeBidder).forEach((function(e) {
      var n = e.bids.filter((function(e) {
        return !(0, l.nativeBidder)(e)
      })).map((function(e) {
        return e.bidder
      })).join(", ");
      S.logError("adUnit " + e.code + " has 'mediaType' set to 'native' but contains non-native bidder(s) " + n + ". No Prebid demand requests will be triggered for those bidders."), e.bids = e.bids.filter(l.nativeBidder)
    })), P) N.push((function() {
      y.requestBids({
        bidsBackHandler: n,
        timeout: a,
        adUnits: r,
        adUnitCodes: o
      })
    }));
    else {
      if (P = !0, y._adUnitCodes = o, T.externalCallbackReset(), i(), !r || 0 === r.length) return S.logMessage("No adUnits configured. No bids requested."), "function" == typeof n && T.addOneTimeCallback(n, !1), void T.executeCallback();
      var s = T.executeCallback.bind(T, !0),
        d = setTimeout(s, a);
      (0, m.setAjaxTimeout)(a), "function" == typeof n && T.addOneTimeCallback(n, d), A.callBids({
        adUnits: r,
        adUnitCodes: o,
        cbTimeout: a
      }), 0 === y._bidsRequested.length && T.executeCallback()
    }
  }, y.addAdUnits = function(e) {
    S.logInfo("Invoking pbjs.addAdUnits", arguments), S.isArray(e) ? (e.forEach((function(e) {
      return e.transactionId = S.generateUUID()
    })), y.adUnits.push.apply(y.adUnits, e)) : "object" === (void 0 === e ? "undefined" : s(e)) && (e.transactionId = S.generateUUID(), y.adUnits.push(e))
  }, y.onEvent = function(e, n, t) {
    S.logInfo("Invoking pbjs.onEvent", arguments), S.isFn(n) ? !t || R[e].call(null, t) ? _.on(e, n, t) : S.logError('The id provided is not valid for event "' + e + '" and no handler was set.') : S.logError('The event handler provided is not a function and was not set on event "' + e + '".')
  }, y.offEvent = function(e, n, t) {
    S.logInfo("Invoking pbjs.offEvent", arguments), t && !R[e].call(null, t) || _.off(e, n, t)
  }, y.addCallback = function(e, n) {
    S.logWarn("pbjs.addCallback will be removed in Prebid 1.0. Please use onEvent instead"), S.logInfo("Invoking pbjs.addCallback", arguments);
    var t = null;
    return e && n && "function" == typeof n ? (t = S.getUniqueIdentifierStr, T.addCallback(t, n, e), t) : (S.logError("error registering callback. Check method signature"), t)
  }, y.removeCallback = function() {
    return S.logWarn("pbjs.removeCallback will be removed in Prebid 1.0. Please use offEvent instead."), null
  }, y.registerBidAdapter = function(e, n) {
    S.logInfo("Invoking pbjs.registerBidAdapter", arguments);
    try {
      A.registerBidAdapter(e(), n)
    } catch (e) {
      S.logError("Error registering bidder adapter : " + e.message)
    }
  }, y.registerAnalyticsAdapter = function(e) {
    S.logInfo("Invoking pbjs.registerAnalyticsAdapter", arguments);
    try {
      A.registerAnalyticsAdapter(e)
    } catch (e) {
      S.logError("Error registering analytics adapter : " + e.message)
    }
  }, y.bidsAvailableForAdapter = function(e) {
    S.logInfo("Invoking pbjs.bidsAvailableForAdapter", arguments), y._bidsRequested.find((function(n) {
      return n.bidderCode === e
    })).bids.map((function(n) {
      return d(n, I.createBid(1), {
        bidderCode: e,
        adUnitCode: n.placementCode
      })
    })).map((function(e) {
      return y._bidsReceived.push(e)
    }))
  }, y.createBid = function(e) {
    return S.logInfo("Invoking pbjs.createBid", arguments), I.createBid(e)
  }, y.addBidResponse = function(e, n) {
    S.logWarn("pbjs.addBidResponse will be removed in Prebid 1.0. Each bidder will be passed a reference to addBidResponse function in callBids as an argument. See https://github.com/prebid/Prebid.js/issues/1087 for more details."), S.logInfo("Invoking pbjs.addBidResponse", arguments), T.addBidResponse(e, n)
  }, y.loadScript = function(e, n, t) {
    S.logInfo("Invoking pbjs.loadScript", arguments), (0, v.loadScript)(e, n, t)
  }, y.enableAnalytics = function(e) {
    e && !S.isEmpty(e) ? (S.logInfo("Invoking pbjs.enableAnalytics for: ", e), A.enableAnalytics(e)) : S.logError("pbjs.enableAnalytics should be called with option {}")
  }, y.aliasBidder = function(e, n) {
    S.logInfo("Invoking pbjs.aliasBidder", arguments), e && n ? A.aliasBidAdapter(e, n) : S.logError("bidderCode and alias must be passed as arguments", "pbjs.aliasBidder")
  }, y.setPriceGranularity = function(e) {
    S.logWarn("pbjs.setPriceGranularity will be removed in Prebid 1.0. Use pbjs.setConfig({ priceGranularity: <granularity> }) instead."), S.logInfo("Invoking pbjs.setPriceGranularity", arguments), h.config.setConfig({
      priceGranularity: e
    })
  }, y.enableSendAllBids = function() {
    h.config.setConfig({
      enableSendAllBids: !0
    })
  }, y.getAllWinningBids = function() {
    return y._winningBids
  }, y.buildMasterVideoTagFromAdserverTag = function(e, n) {
    S.logWarn("pbjs.buildMasterVideoTagFromAdserverTag will be removed in Prebid 1.0. Include the dfpVideoSupport module in your build, and use the pbjs.adservers.dfp.buildVideoAdUrl function instead"), S.logInfo("Invoking pbjs.buildMasterVideoTagFromAdserverTag", arguments);
    var t = (0, p.parse)(e);
    if (0 === y._bidsReceived.length) return e;
    if ("dfp" === n.adserver.toLowerCase()) {
      var r = w.dfpAdserver(n, t);
      return r.verifyAdserverTag() || S.logError("Invalid adserverTag, required google params are missing in query string"), r.appendQueryParams(), (0, p.format)(r.urlComponents)
    }
    S.logError("Only DFP adserver is supported")
  }, y.setBidderSequence = A.setBidderSequence, y.getHighestCpmBids = function(e) {
    return j.getWinningBids(e)
  }, y.setS2SConfig = function(e) {
    if (S.contains(Object.keys(e), "accountId")) if (S.contains(Object.keys(e), "bidders")) {
      var n = d({
        enabled: !1,
        endpoint: E.S2S.DEFAULT_ENDPOINT,
        timeout: 1e3,
        maxBids: 1,
        adapter: E.S2S.ADAPTER,
        syncEndpoint: E.S2S.SYNC_ENDPOINT,
        cookieSet: !0,
        bidders: []
      }, e);
      A.setS2SConfig(n)
    } else S.logError("bidders missing in Server to Server config");
    else S.logError("accountId missing in Server to Server config")
  }, y.getConfig = h.config.getConfig, y.setConfig = h.config.setConfig, y.que.push((function() {
    return (0, g.listenMessagesFromCreative)()
  })), y.cmd.push = function(e) {
    if ("function" == typeof e) try {
      e.call()
    } catch (e) {
      S.logError("Error processing command :" + e.message)
    } else S.logError("Commands written into pbjs.cmd.push must be wrapped in a function")
  }, y.que.push = y.cmd.push, y.processQueue = function() {
    a(y.que), a(y.cmd)
  }
}), (function(e, n, t) {
  "use strict";
  t(260), t(273), t(275), t(278), Number.isInteger = Number.isInteger || function(e) {
    return "number" == typeof e && isFinite(e) && Math.floor(e) === e
  }
}), (function(e, n, t) {
  t(261), e.exports = t(12).Array.find
}), (function(e, n, t) {
  "use strict";
  var r = t(16),
    i = t(33)(5),
    o = !0;
  "find" in [] && Array(1).find((function() {
    o = !1
  })), r(r.P + r.F * o, "Array", {
    find: function(e) {
      return i(this, e, arguments.length > 1 ? arguments[1] : void 0)
    }
  }), t(25)("find")
}), (function(e, n, t) {
  var r = t(263),
    i = t(264),
    o = t(266),
    a = Object.defineProperty;
  n.f = t(21) ? Object.defineProperty : function(e, n, t) {
    if (r(e), n = o(n, !0), r(t), i) try {
      return a(e, n, t)
    } catch (e) {}
    if ("get" in t || "set" in t) throw TypeError("Accessors not supported!");
    return "value" in t && (e[n] = t.value), e
  }
}), (function(e, n, t) {
  var r = t(17);
  e.exports = function(e) {
    if (!r(e)) throw TypeError(e + " is not an object!");
    return e
  }
}), (function(e, n, t) {
  e.exports = !t(21) && !t(22)((function() {
    return 7 != Object.defineProperty(t(265)("div"), "a", {
      get: function() {
        return 7
      }
    }).a
  }))
}), (function(e, n, t) {
  var r = t(17),
    i = t(14).document,
    o = r(i) && r(i.createElement);
  e.exports = function(e) {
    return o ? i.createElement(e) : {}
  }
}), (function(e, n, t) {
  var r = t(17);
  e.exports = function(e, n) {
    if (!r(e)) return e;
    var t, i;
    if (n && "function" == typeof(t = e.toString) && !r(i = t.call(e))) return i;
    if ("function" == typeof(t = e.valueOf) && !r(i = t.call(e))) return i;
    if (!n && "function" == typeof(t = e.toString) && !r(i = t.call(e))) return i;
    throw TypeError("Can't convert object to primitive value")
  }
}), (function(e, n) {
  e.exports = function(e, n) {
    return {
      enumerable: !(1 & e),
      configurable: !(2 & e),
      writable: !(4 & e),
      value: n
    }
  }
}), (function(e, n, t) {
  var r = t(14),
    i = t(20),
    o = t(31),
    a = t(23)("src"),
    s = Function.toString,
    d = ("" + s).split("toString");
  t(12).inspectSource = function(e) {
    return s.call(e)
  }, (e.exports = function(e, n, t, s) {
    var u = "function" == typeof t;
    u && (o(t, "name") || i(t, "name", n)), e[n] !== t && (u && (o(t, a) || i(t, a, e[n] ? "" + e[n] : d.join(String(n)))), e === r ? e[n] = t : s ? e[n] ? e[n] = t : i(e, n, t) : (delete e[n], i(e, n, t)))
  })(Function.prototype, "toString", (function() {
    return "function" == typeof this && this[a] || s.call(this)
  }))
}), (function(e, n) {
  e.exports = function(e) {
    if ("function" != typeof e) throw TypeError(e + " is not a function!");
    return e
  }
}), (function(e, n, t) {
  var r = t(271);
  e.exports = function(e, n) {
    return new(r(e))(n)
  }
}), (function(e, n, t) {
  var r = t(17),
    i = t(272),
    o = t(39)("species");
  e.exports = function(e) {
    var n;
    return i(e) && ("function" != typeof(n = e.constructor) || n !== Array && !i(n.prototype) || (n = void 0), r(n) && null === (n = n[o]) && (n = void 0)), void 0 === n ? Array : n
  }
}), (function(e, n, t) {
  var r = t(34);
  e.exports = Array.isArray || function(e) {
    return "Array" == r(e)
  }
}), (function(e, n, t) {
  t(274), e.exports = t(12).Array.findIndex
}), (function(e, n, t) {
  "use strict";
  var r = t(16),
    i = t(33)(6),
    o = "findIndex",
    a = !0;
  o in [] && Array(1)[o]((function() {
    a = !1
  })), r(r.P + r.F * a, "Array", {
    findIndex: function(e) {
      return i(this, e, arguments.length > 1 ? arguments[1] : void 0)
    }
  }), t(25)(o)
}), (function(e, n, t) {
  t(276), e.exports = t(12).Array.includes
}), (function(e, n, t) {
  "use strict";
  var r = t(16),
    i = t(41)(!0);
  r(r.P, "Array", {
    includes: function(e) {
      return i(this, e, arguments.length > 1 ? arguments[1] : void 0)
    }
  }), t(25)("includes")
}), (function(e, n, t) {
  var r = t(38),
    i = Math.max,
    o = Math.min;
  e.exports = function(e, n) {
    return (e = r(e)) < 0 ? i(e + n, 0) : o(e, n)
  }
}), (function(e, n, t) {
  t(279), e.exports = t(12).Object.assign
}), (function(e, n, t) {
  var r = t(16);
  r(r.S + r.F, "Object", {
    assign: t(280)
  })
}), (function(e, n, t) {
  "use strict";
  var r = t(281),
    i = t(285),
    o = t(286),
    a = t(35),
    s = t(24),
    d = Object.assign;
  e.exports = !d || t(22)((function() {
    var e = {}, n = {}, t = Symbol(),
      r = "abcdefghijklmnopqrst";
    return e[t] = 7, r.split("").forEach((function(e) {
      n[e] = e
    })), 7 != d({}, e)[t] || Object.keys(d({}, n)).join("") != r
  })) ? function(e, n) {
    for (var t = a(e), d = arguments.length, u = 1, c = i.f, f = o.f; d > u;) for (var l, p = s(arguments[u++]), g = c ? r(p).concat(c(p)) : r(p), b = g.length, v = 0; b > v;) f.call(p, l = g[v++]) && (t[l] = p[l]);
    return t
  } : d
}), (function(e, n, t) {
  var r = t(282),
    i = t(284);
  e.exports = Object.keys || function(e) {
    return r(e, i)
  }
}), (function(e, n, t) {
  var r = t(31),
    i = t(42),
    o = t(41)(!1),
    a = t(283)("IE_PROTO");
  e.exports = function(e, n) {
    var t, s = i(e),
      d = 0,
      u = [];
    for (t in s) t != a && r(s, t) && u.push(t);
    for (; n.length > d;) r(s, t = n[d++]) && (~o(u, t) || u.push(t));
    return u
  }
}), (function(e, n, t) {
  var r = t(40)("keys"),
    i = t(23);
  e.exports = function(e) {
    return r[e] || (r[e] = i(e))
  }
}), (function(e, n) {
  e.exports = "constructor,hasOwnProperty,isPrototypeOf,propertyIsEnumerable,toLocaleString,toString,valueOf".split(",")
}), (function(e, n) {
  n.f = Object.getOwnPropertySymbols
}), (function(e, n) {
  n.f = {}.propertyIsEnumerable
}), (function(e, n, t) {
  "use strict";

  function r(e) {
    var n = e.message ? "message" : "data",
      t = {};
    try {
      t = JSON.parse(e[n])
    } catch (e) {
      return
    }
    if (t.adId) {
      var r = pbjs._bidsReceived.find((function(e) {
        return e.adId === t.adId
      }));
      "Prebid Request" === t.message && (i(r, t.adServerDomain, e.source), pbjs._winningBids.push(r), a.
      default.emit(d, r)), "Prebid Native" === t.message && ((0, s.fireNativeImpressions)(r), pbjs._winningBids.push(r), a.
      default.emit(d, r))
    }
  }
  function i(e, n, t) {
    var r = e.adId,
      i = e.ad,
      a = e.adUrl,
      s = e.width,
      d = e.height;
    r && (o(e), t.postMessage(JSON.stringify({
      message: "Prebid Response",
      ad: i,
      adUrl: a,
      adId: r,
      width: s,
      height: d
    }), n))
  }
  function o(e) {
    var n = e.adUnitCode,
      t = e.width,
      r = e.height,
      i = document.getElementById(window.googletag.pubads().getSlots().find((function(e) {
        return e.getAdUnitPath() === n || e.getSlotElementId() === n
      })).getSlotElementId()).querySelector("iframe");
    i.width = "" + t, i.height = "" + r
  }
  Object.defineProperty(n, "__esModule", {
    value: !0
  }), n.listenMessagesFromCreative = function() {
    addEventListener("message", r, !1)
  };
  var a = (function(e) {
    return e && e.__esModule ? e : {
      default: e
    }
  })(t(9)),
    s = t(13),
    d = t(4).EVENTS.BID_WON
}), (function(e, n, t) {
  "use strict";
  var r = t(11),
    i = t(19),
    o = function(e) {
      this.name = e.adserver, this.code = e.code, this.getWinningBidByCode = function() {
        return (0, i.getWinningBids)(this.code)[0]
      }
    };
  n.dfpAdserver = function(e, n) {
    var t = new o(e);
    t.urlComponents = n;
    var i = {
      env: "vp",
      gdfp_req: "1",
      impl: "s",
      unviewed_position_start: "1"
    }, a = ["output", "iu", "sz", "url", "correlator", "description_url", "hl"],
      s = function(e) {
        return encodeURIComponent((0, r.formatQS)(e))
      };
    return t.appendQueryParams = function() {
      var e = t.getWinningBidByCode();
      e && (this.urlComponents.search.description_url = encodeURIComponent(e.descriptionUrl), this.urlComponents.search.cust_params = s(e.adserverTargeting), this.urlComponents.search.correlator = Date.now())
    }, t.verifyAdserverTag = function() {
      for (var e in i) if (!this.urlComponents.search.hasOwnProperty(e) || this.urlComponents.search[e] !== i[e]) return !1;
      for (var n in a) if (!this.urlComponents.search.hasOwnProperty(a[n])) return !1;
      return !0
    }, t
  }
})]);
pbjsChunk([83], {
  91: function(i, e, n) {
    i.exports = n(92)
  },
  92: function(i, e, n) {
    "use strict";

    function d() {
      function i() {
        var i = r.createBid(t.STATUS.NO_BID);
        return i.bidderCode = u, i
      }
      function e(i) {
        var e = i;
        window.setTimeout((function() {
          var i = document.createElement("img");
          i.width = 1, i.height = 1, i.style = "display:none;";
          var n = document.location.protocol;
          i.src = (n ? "https:" : "http:") + d + e, document.body.insertBefore(i, null)
        }), c)
      }
      var n = "http://localhost:8080/ht",
        d = "//px.c1exchange.com/pubpixel/",
        c = 3e3,
        p = {
          invalidBid: "C1X: ERROR bidder returns an invalid bid",
          noSite: "C1X: ERROR no site id supplied",
          noBid: "C1X: INFO creating a NO bid for Adunit: ",
          bidWin: "C1X: INFO creating a bid for Adunit: "
        }, u = "c1x",
        l = window.pbjs;
      return l._c1xResponse = function(e) {
        var n = e;
        if ("string" == typeof n) try {
          n = JSON.parse(e)
        } catch (i) {
          o.logError(i)
        }
        if (n && !n.error) for (var d = 0; d < n.length; d++) {
          var s = n[d],
            c = null;
          s.bid ? ((c = r.createBid(t.STATUS.GOOD)).bidderCode = u, c.cpm = s.cpm, c.ad = s.ad, c.width = s.width, c.height = s.height, o.logInfo(p.bidWin + s.adId + " size: " + s.width + "x" + s.height), a.addBidResponse(s.adId, c)) : (o.logInfo(p.noBid + s.adId), a.addBidResponse(s.adId, i()))
        } else {
          var h = l.adUnits;
          for (o.logWarn(p.invalidBid), d = 0; d < h.length; d++) a.addBidResponse(h[d].code, i())
        }
      }, {
        callBids: function(i) {
          var d = i.bids,
            t = d[0].params;
          t.pixelId && e(t.pixelId);
          var r = t.siteId;
          if (r) {
            var a = ["adunits=" + d.length];
            a.push("site=" + r);
            for (var c = 0; c < d.length; c++) {
              a.push("a" + (c + 1) + "=" + d[c].placementCode);
              var u = d[c].sizes,
                h = u.reduce((function(i, e) {
                  return i + ("" === i ? "" : ",") + e.join("x")
                }), ""),
                f = t.floorPriceMap;
              if (f) {
                var g = u[0].join("x");
                g in f && a.push("a" + (c + 1) + "p=" + f[g])
              }
              a.push("a" + (c + 1) + "s=[" + h + "]")
            }
            a.push("rid=" + (new Date).getTime());
            var v = n;
            t.endpoint && (v = t.endpoint);
            var x = t.dspid;
            x && a.push("dspid=" + x);
            var b = v + "?" + a.join("&");
            window._c1xResponse = function(i) {
              l._c1xResponse(i)
            }, s.loadScript(b)
          } else o.logWarn(p.noSite)
        }
      }
    }
    var t = n(4),
      o = n(0),
      r = n(3),
      a = n(2),
      s = n(5);
    n(1).registerBidAdapter(new d, "c1x"), i.exports = d
  }
}, [91]);
pbjs.processQueue();