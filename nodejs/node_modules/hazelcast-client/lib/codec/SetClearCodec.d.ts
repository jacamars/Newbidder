import ClientMessage = require('../ClientMessage');
export declare class SetClearCodec {
    static calculateSize(name: string): number;
    static encodeRequest(name: string): ClientMessage;
}
