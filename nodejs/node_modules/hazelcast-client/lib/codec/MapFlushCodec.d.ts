import ClientMessage = require('../ClientMessage');
export declare class MapFlushCodec {
    static calculateSize(name: string): number;
    static encodeRequest(name: string): ClientMessage;
}
