import ClientMessage = require('../ClientMessage');
export declare class ReplicatedMapClearCodec {
    static calculateSize(name: string): number;
    static encodeRequest(name: string): ClientMessage;
}
