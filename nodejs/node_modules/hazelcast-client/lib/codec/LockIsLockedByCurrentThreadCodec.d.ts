import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class LockIsLockedByCurrentThreadCodec {
    static calculateSize(name: string, threadId: any): number;
    static encodeRequest(name: string, threadId: any): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
