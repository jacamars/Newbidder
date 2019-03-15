import ClientMessage = require('../ClientMessage');
export declare class MapEvictAllCodec {
    static calculateSize(name: string): number;
    static encodeRequest(name: string): ClientMessage;
}
