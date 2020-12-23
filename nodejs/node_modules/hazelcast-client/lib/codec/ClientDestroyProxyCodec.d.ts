import ClientMessage = require('../ClientMessage');
export declare class ClientDestroyProxyCodec {
    static calculateSize(name: string, serviceName: string): number;
    static encodeRequest(name: string, serviceName: string): ClientMessage;
}
