import ClientMessage = require('../ClientMessage');
export declare class ClientPingCodec {
    static calculateSize(): number;
    static encodeRequest(): ClientMessage;
}
