import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class MultiMapLockCodec {
    static calculateSize(name: string, key: Data, threadId: any, ttl: any, referenceId: any): number;
    static encodeRequest(name: string, key: Data, threadId: any, ttl: any, referenceId: any): ClientMessage;
}
