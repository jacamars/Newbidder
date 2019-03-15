import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class AtomicLongAddAndGetCodec {
    static calculateSize(name: string, delta: any): number;
    static encodeRequest(name: string, delta: any): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
