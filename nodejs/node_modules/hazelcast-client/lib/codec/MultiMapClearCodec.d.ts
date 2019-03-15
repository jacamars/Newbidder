import ClientMessage = require('../ClientMessage');
export declare class MultiMapClearCodec {
    static calculateSize(name: string): number;
    static encodeRequest(name: string): ClientMessage;
}
