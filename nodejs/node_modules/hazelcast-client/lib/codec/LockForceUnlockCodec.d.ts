import ClientMessage = require('../ClientMessage');
export declare class LockForceUnlockCodec {
    static calculateSize(name: string, referenceId: any): number;
    static encodeRequest(name: string, referenceId: any): ClientMessage;
}
