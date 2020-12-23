var Client = require('hazelcast-client').Client;
var Config = require('hazelcast-client').Config;
var CircularJSON = require('circular-json');

function Member(json) {
	this.json = json;
}

Member.prototype.readPortable = function(reader) {
	this.json = reader.readUTF("json");
};

Member.prototype.writePortable = function(writer) {

};

Member.prototype.getFactoryId = function() {
	return 2;
};

Member.prototype.getClassId = function() {
	return 2;
};

function PortableFactory() {
	
}

PortableFactory.prototype.create = function(classId) {
	if (classId === 2) {
		return new Member();
	}
	return null;
};

let cfg = new Config.ClientConfig();
cfg.serializationConfig.portableFactories[2] = new PortableFactory();
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
	console.log("Member: " + name + "\n" + JSON.stringify(JSON.parse(member.json),null,2));
}
