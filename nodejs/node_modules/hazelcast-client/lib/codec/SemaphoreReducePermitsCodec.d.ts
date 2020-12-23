import ClientMessage = require('../ClientMessage');
export declare class SemaphoreReducePermitsCodec {
    static calculateSize(name: string, reduction: number): number;
    static encodeRequest(name: string, reduction: number): ClientMessage;
}
