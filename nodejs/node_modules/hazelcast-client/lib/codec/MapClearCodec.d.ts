import ClientMessage = require('../ClientMessage');
export declare class MapClearCodec {
    static calculateSize(name: string): number;
    static encodeRequest(name: string): ClientMessage;
}
