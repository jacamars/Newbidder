import ClientMessage = require('../ClientMessage');
export declare class ListClearCodec {
    static calculateSize(name: string): number;
    static encodeRequest(name: string): ClientMessage;
}
