import ClientMessage = require('../ClientMessage');
export declare class SemaphoreReleaseCodec {
    static calculateSize(name: string, permits: number): number;
    static encodeRequest(name: string, permits: number): ClientMessage;
}
