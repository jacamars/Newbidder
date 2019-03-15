import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class AtomicLongCompareAndSetCodec {
    static calculateSize(name: string, expected: any, updated: any): number;
    static encodeRequest(name: string, expected: any, updated: any): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
