import ClientMessage = require('../ClientMessage');
export declare class QueueClearCodec {
    static calculateSize(name: string): number;
    static encodeRequest(name: string): ClientMessage;
}
