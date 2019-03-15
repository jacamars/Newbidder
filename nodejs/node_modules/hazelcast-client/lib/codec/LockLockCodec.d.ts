import ClientMessage = require('../ClientMessage');
export declare class LockLockCodec {
    static calculateSize(name: string, leaseTime: any, threadId: any, referenceId: any): number;
    static encodeRequest(name: string, leaseTime: any, threadId: any, referenceId: any): ClientMessage;
}
