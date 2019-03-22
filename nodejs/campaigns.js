var Client = require('hazelcast-client').Client;
var Config = require('hazelcast-client').Config;
var CircularJSON = require('circular-json');

function Campaign(json) {
	this.json = json;
}

Campaign.prototype.readPortable = function(reader) {
	this.json = reader.readUTF("json");
};

Campaign.prototype.writePortable = function(writer) {
	writer.writePortable("json",this.json);
};

Campaign.prototype.getFactoryId = function() {
	return 2;
};

Campaign.prototype.getClassId = function() {
	return 3;
};

function PortableFactory() {
	
}

PortableFactory.prototype.create = function(classId) {
	if (classId === 3) {
		return new Campaign();
	}
	return null;
};

let cfg = new Config.ClientConfig();
cfg.serializationConfig.portableFactories[2] = new PortableFactory();
getCampaigns(cfg);

async function getCampaigns(config) {
	let client = await Client.newHazelcastClient(config);
	let campaigns = await client.getMap('CONTEXT');
	let keyset = await campaigns.keySet();
	for ( var key in keyset) {
		let name = keyset[key];
		await printCampaign(campaigns, name)
		
	}
	//console.log("MEMBERS = " + CircularJSON.stringify(members, null, 2));
}

async function printCampaign(campaigns, name) {
	let member = await campaigns.get(name);
	console.log("*******************************************");
	console.log("Campaign: " + name + "\n" + JSON.stringify(JSON.parse(member.json),null,2));
}
