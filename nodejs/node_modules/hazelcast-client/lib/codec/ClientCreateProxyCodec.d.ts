import ClientMessage = require('../ClientMessage');
import Address = require('../Address');
export declare class ClientCreateProxyCodec {
    static calculateSize(name: string, serviceName: string, target: Address): number;
    static encodeRequest(name: string, serviceName: string, target: Address): ClientMessage;
}
