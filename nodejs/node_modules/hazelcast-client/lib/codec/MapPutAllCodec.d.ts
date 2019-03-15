import ClientMessage = require('../ClientMessage');
export declare class MapPutAllCodec {
    static calculateSize(name: string, entries: any): number;
    static encodeRequest(name: string, entries: any): ClientMessage;
}
