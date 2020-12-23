import ClientMessage = require('../ClientMessage');
export declare class ReplicatedMapPutAllCodec {
    static calculateSize(name: string, entries: any): number;
    static encodeRequest(name: string, entries: any): ClientMessage;
}
