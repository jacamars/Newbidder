import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class MultiMapUnlockCodec {
    static calculateSize(name: string, key: Data, threadId: any, referenceId: any): number;
    static encodeRequest(name: string, key: Data, threadId: any, referenceId: any): ClientMessage;
}
