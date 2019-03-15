import ClientMessage = require('../ClientMessage');
export declare class AtomicLongSetCodec {
    static calculateSize(name: string, newValue: any): number;
    static encodeRequest(name: string, newValue: any): ClientMessage;
}
