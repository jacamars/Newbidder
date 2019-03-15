var Client = require('hazelcast-client').Client;
var Config = require('hazelcast-client').Config;
var Long = require('long');

function Member(campaigns,name,percentage,stopped,request,bid,nobid,win,error,handled,unknown,loglevel,request,pixel,adspend,
		qps,avgx,fraud,threads,memory,freeDisk,cpu,ipaddress,leader,lastupdate,total,cores,ncampaigns,ecampaigns,nobidreason) {
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

Member.prototype.readPortable = function (reader) {
	var size = reader.readInt("Lcampaigns");
	this.campaigns = [];
		for (var i=0; i < size; i++) {
			var key = "campaigns:"+i;
			var str = reader.readUTF(key);
			this.campaigns.push(str);
		}
		
		this.name = reader.readUTF("name");
		this.percentage = reader.readInt("percentage");
		this.stopped = reader.readBoolean("stopped");
		this.request = reader.readLong("request").toNumber();
		this.bid = reader.readLong("bid").toNumber();
		this.nobid = reader.readLong("nobid").toNumber();;
		this.win = reader.readLong("win").toNumber();;
		this.error = reader.readLong("error").toNumber();;
		this.handled = reader.readLong("handled").toNumber();;
		this.unknown = reader.readLong("unknown").toNumber();;
		this.loglevel = reader.readInt("loglevel");
		this.request = reader.readLong("request").toNumber();;
		this.clicks = reader.readLong("clicks").toNumber();;
		this.pixel = reader.readLong("pixel").toNumber();;
		this.adspend = reader.readDouble("adspend");
		this.qps = reader.readDouble("qps");
		this.avgx = reader.readDouble("avgx");
		this.fraud = reader.readLong("fraud").toNumber();;
		this.threads = reader.readInt("threads");
		this.memory = reader.readUTF("memory");
		this.freeDisk = reader.readUTF("freeDisk");
		this.cpu = reader.readUTF("cpu");
		this.ipaddress = reader.readUTF("ipaddress");
		this.leader = reader.readBoolean("leader");
		this.lastupdate = reader.readLong("lastupdate").toNumber();;
		this.total = reader.readLong("total").toNumber();;
		this.cores = reader.readInt("cores");
		this.ncampaigns = reader.readInt("ncampaigns");
		this.ecampaigns = reader.readInt("ecampaigns");
		this.nobidreason = reader.readBoolean("nobidreason");
};

Member.prototype.writePortable = function (writer) {
    writer.writeLong('lastOrder', Long.fromNumber(this.lastOrder));

		writer.writeInt("Lcampaigns",campaigns.size());
		for (var i=0;i<campaigns.size();i++){
			writer.writeUTF("campaign:"+i, campaigns.get(0));
		}
		writer.writeUTF("name",this.name);
		writer.writeInt("percentage",this.percentage);
		writer.writeBoolean("stopped",stopped);
		writer.writeLong("request",Long.fromNumber(this.request));
		writer.writeLong("bid",Long.fromNumber(this.bid));
		writer.writeLong("nobid",Long.fromNumber(this.nobid));
		writer.writeLong("win",Long.fromNumber(this.win));
		writer.writeLong("error",Long.fromNumber(this.error));
		writer.writeLong("handled",Long.fromNumber(this.handled));
		writer.writeLong("unknown",Long.fromNumber(this.unknown));
		writer.writeInt("loglevel",this.loglevel);
		writer.writeLong("request",Long.fromNumber(this.request));
		writer.writeLong("clicks",Long.fromNumber(this.clicks));
		writer.writeLong("pixel",Long.fromNumber(this.pixel));
		writer.writeDouble("adspend",adspend);
		writer.writeDouble("qps",qps);
		writer.writeDouble("avgx",avgx);
		writer.writeLong("fraud",Long.fromNumber(this.fraud));
		writer.writeInt("threads",this.threads);
		writer.writeUTF("memory",this.memory);
		writer.writeUTF("freeDisk",this.freeDisk);
		writer.writeUTF("cpu",this.cpu);
		writer.writeUTF("ipaddress", this.ipaddress);
		writer.writeBoolean("leader", this.leader);
		writer.writeLong("lastupdate", Long.fromNumber(this.lastupdate));
		writer.writeLong("total", Long.fromNumber(this.total));
		writer.writeInt("cores", this.cores);
		writer.writeInt("ncampaigns", this.ncampaigns);
		writer.writeInt("ecampaigns", this.ecampaigns);
		writer.writeBoolean("nobidreason",this.nobidreason);
};

Member.prototype.getFactoryId = function () {
    return 2;
};

Member.prototype.getClassId = function () {
    return 2;
};

function PortableFactory() {
    // Constructor function
}

PortableFactory.prototype.create = function (classId) {
    if (classId === 2) {
        return new Member();
    }
    return null;
};

let cfg = new Config.ClientConfig();
cfg.serializationConfig.portableFactories[2] = new PortableFactory();
// Start the Hazelcast Client and connect to an already running Hazelcast Cluster on 127.0.0.1

getMembers(cfg);

async function getMembers(config) {
        let client = await Client.newHazelcastClient(config);
        let members = await client.getMap('MEMBER');
        let keyset = await members.keySet();
        for (var key in keyset) {
                let name = keyset[key];
		await printMember(members,name)
        }
}

async function printMember(members,name) {
	let member = await members.get(name);
	console.log("*******************************************");
	console.log("Member: " + name + "\n" + JSON.stringify(member,null,2));
}
