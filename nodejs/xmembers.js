var Client = require('hazelcast-client').Client;
var Config = require('hazelcast-client').Config;
var Long = require('long');
var CircularJSON = require('circular-json');

function Member(campaigns, name, percentage, stopped, request, bid, nobid, win,
		error, handled, unknown, loglevel, request, pixel, adspend, qps, avgx,
		fraud, threads, memory, freeDisk, cpu, ipaddress, leader, lastupdate,
		total, cores, ncampaigns, ecampaigns, nobidreason) {
	this.campaigns = campaigns;
	this.name = name;
	this.precentage = percentage;
	this.stopped = stopped;
	this.request = request;
	this.bid = bid;
	this.nobid = nobid;
	this.win = win;
	this.error = error;
	this.handled = handled;
	this.unknown = unknown;
	this.loglevel = loglevel;
	this.request = request;
	this.pixel = pixel;
	this.adspend = adspend;
	this.qps = qps;
	this.avgx = avgx;
	this.fraud = fraud;
	this.threads = threads;
	this.memory = memory;
	this.freeDisk = freeDisk;
	this.cpu = cpu;
	this.ipaddres = ipaddress;
	this.leader = leader;
	this.lastupdate = lastupdate;
	this.total = total;
	this.cores = cores;
	this.ncampaigns = ncampaigns;
	this.ecampaigns = ecampaigns;
	this.nobidreason = nobidreason;
}

Member.prototype.readPortable = function(reader) {
	if (reader.readBoolean("_has__campaigns")) {
		this.campaigns = reader.readUTFArray("campaigns")
	}

	this.name = reader.readUTF("name");
	this.percentage = reader.readInt("percentage");
	this.stopped = reader.readBoolean("stopped");
	this.request = reader.readLong("request").toNumber();
	this.bid = reader.readLong("bid").toNumber();
	this.nobid = reader.readLong("nobid").toNumber();
	this.win = reader.readLong("win").toNumber();
	this.error = reader.readLong("error").toNumber();
	this.handled = reader.readLong("handled").toNumber();
	this.unknown = reader.readLong("unknown").toNumber();
	this.loglevel = reader.readInt("loglevel");
	this.request = reader.readLong("request").toNumber();
	this.clicks = reader.readLong("clicks").toNumber();
	this.pixel = reader.readLong("pixel").toNumber();
	this.adspend = reader.readDouble("adspend");
	this.qps = reader.readDouble("qps");
	this.avgx = reader.readDouble("avgx");
	this.fraud = reader.readLong("fraud").toNumber();
	this.threads = reader.readInt("threads");
	this.memory = reader.readUTF("memory");
	this.freeDisk = reader.readUTF("freeDisk");
	this.cpu = reader.readUTF("cpu");
	this.ipaddress = reader.readUTF("ipaddress");
	this.leader = reader.readBoolean("leader");
	this.lastupdate = reader.readLong("lastupdate").toNumber();
	this.total = reader.readLong("total").toNumber();
	this.cores = reader.readInt("cores");
	this.ncampaigns = reader.readInt("ncampaigns");
	this.ecampaigns = reader.readInt("ecampaigns");
	this.nobidreason = reader.readBoolean("nobidreason");
	
	if (reader.readBoolean("_has__eperform")) {
		this.eperform = reader.readPortableArray("eperform");
	}
};

Member.prototype.writePortable = function(writer) {

};

Member.prototype.getFactoryId = function() {
	return 2;
};

Member.prototype.getClassId = function() {
	return 2;
};

Exchange = function() {
	
}

Exchange.prototype.getClassId = function() {
	return 3;
}

Exchange.prototype.getFactoryId = function() {
	return 2;
};

Exchange.prototype.writePortable = function(writer) {

};

Exchange.prototype.readPortable = function(reader) {
	this.exchange = reader.readUTF("exchange");
	this.total = reader.readLong("total").toNumber();
	this.bids = reader.readLong("bids").toNumber();
	
	if (reader.readBoolean("_has__campaigns")) {
		this.creatives = reader.readPortableArray("campaigns");
	}
}


Campaign = function() {
	return 4;
}

Campaign.prototype.getClassId = function() {
	return 4;
}

Campaign.prototype.getFactoryId = function() {
	return 2;
};

Campaign.prototype.writePortable = function(writer) {
	this.campaign = reader.readUTF("campaign");
	this.total = reader.readLong("total").toNumber();
	this.bids = reader.readLong("bids").toNumber();
	
	if (reader.readBoolean("_has__creatives")) {
		this.creatives = reader.readPortableArray("creatives");
	}
};


Creative = function() {
	
}

Creative.prototype.getClassId = function() {
	return 5;
}

Creative.prototype.getFactoryId = function() {
	return 2;
};

Creative.prototype.writePortable = function(writer) {

};

Creative.prototype.readPortable = function(reader) {

	this.creative = reader.readUTF("creative");
	this.total = reader.readLong("total").toNumber();
	this.bids = reader.readLong("bids").toNumber();
	
	if (reader.readBoolean("_has__reasons")) {
		this.reasons = reader.readPortableArray("reasons");
	}
};



Reason = function() {
	
}
	
Reason.prototype.getClassId = function() {
	return 6;
}

Reason.prototype.getFactoryId = function() {
	return 2;
};

Reason.prototype.writePortable = function(writer) {

};

Reason.prototype.readPortable = function(reader) {
	this.name = reader.readUTF("name");
	this.count = reader.readLong("count").toNumber();
};



function PortableFactory() {
	// Constructor function
}

PortableFactory.prototype.create = function(classId) {
	if (classId === 2) {
		return new Member();
	}
	if (classId === 3) {
		return new Exchange();
	}
	if (classId === 4) {
		return new Campaign();
	}
	if (classId === 5) {
		return new Creative();
	}
	if (classId === 6) {
		return new Reason();
	}
	return null;
};

let cfg = new Config.ClientConfig();
cfg.serializationConfig.portableFactories[2] = new PortableFactory();
// Start the Hazelcast Client and connect to an already running Hazelcast
// Cluster on 127.0.0.1

getMembers(cfg);

async function getMembers(config) {
	let client = await Client.newHazelcastClient(config);
	let members = await client.getMap('MEMBER');
	let keyset = await members.keySet();
	for ( var key in keyset) {
		let name = keyset[key];
		await printMember(members, name)
		
	}
	//console.log("MEMBERS = " + CircularJSON.stringify(members, null, 2));
}

async function printMember(members, name) {
	let member = await members.get(name);
	console.log("*******************************************");
	console.log("Member: " + name + "\n" + JSON.stringify(member, null, 2));
}
