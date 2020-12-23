let Client = require('hazelcast-client').Client;
let Config = require('hazelcast-client').Config;
let config = new Config.ClientConfig();

async function getMembers(config) {
	let client = await Client.newHazelcastClient(config);
	let members = await client.getMap('MEMBER');
	//console.log("Got MEMBERS!")
	//for (var key in members) {
    		//console.log("MEMBER: " + key);
	//}
	console.log("NAME: " + members.getName());
	let keyset = await members.keySet();
console.log(JSON.stringify(keyset));
	for (var key in keyset) {
		let name = keyset[key];
		console.log("Member: " + name)
		let member = await members.get(name)
	}
}

getMembers(config);
