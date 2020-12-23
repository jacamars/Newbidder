var Client = require('hazelcast-client').Client;
var Config = require('hazelcast-client').Config;
var Long = require('long');

function Customer(name, id, lastOrder, list) {
    this.name = name;
    this.id = id;
    this.lastOrder = lastOrder;
    this.list = list;
}

Customer.prototype.readPortable = function (reader) {
    this.name = reader.readUTF('name');
    this.id = reader.readInt('id');
    this.lastOrder = reader.readLong('lastOrder').toNumber();
    let size = reader.readInt("size");
    this.list = [];
    for (let i=0; i<size;i++) {
	let s = reader.readUTF("list:"+i);
	this.list.push(s);
    }
};

Customer.prototype.writePortable = function (writer) {
    writer.writeUTF('name', this.name);
    writer.writeInt('id', this.id);
    writer.writeLong('lastOrder', Long.fromNumber(this.lastOrder));
};

Customer.prototype.getFactoryId = function () {
    return 1;
};

Customer.prototype.getClassId = function () {
    return 1;
};

function PortableFactory() {
    // Constructor function
}

PortableFactory.prototype.create = function (classId) {
    if (classId === 1) {
        return new Customer();
    }
    return null;
};

let cfg = new Config.ClientConfig();
cfg.serializationConfig.portableFactories[1] = new PortableFactory();
// Start the Hazelcast Client and connect to an already running Hazelcast Cluster on 127.0.0.1

getCustomer(cfg);

async function getCustomer(config) {
	let client = await Client.newHazelcastClient(config);
        let customers = await client.getMap('CUSTOMERS');
	let customer = await customers.get("Ben");
	console.log("Customer: " + JSON.stringify(customer));
}
