import ClientMessage = require('../ClientMessage');
declare class DistributedObjectInfoCodec {
    static decode(clientMessage: ClientMessage, toObjectFunction: Function): any;
}
export = DistributedObjectInfoCodec;
