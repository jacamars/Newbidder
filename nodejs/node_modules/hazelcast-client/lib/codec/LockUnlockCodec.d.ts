import ClientMessage = require('../ClientMessage');
export declare class LockUnlockCodec {
    static calculateSize(name: string, threadId: any, referenceId: any): number;
    static encodeRequest(name: string, threadId: any, referenceId: any): ClientMessage;
}
