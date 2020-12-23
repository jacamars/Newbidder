import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class MultiMapTryLockCodec {
    static calculateSize(name: string, key: Data, threadId: any, lease: any, timeout: any, referenceId: any): number;
    static encodeRequest(name: string, key: Data, threadId: any, lease: any, timeout: any, referenceId: any): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
