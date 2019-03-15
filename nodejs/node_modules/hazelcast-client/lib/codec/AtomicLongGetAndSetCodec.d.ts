import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class AtomicLongGetAndSetCodec {
    static calculateSize(name: string, newValue: any): number;
    static encodeRequest(name: string, newValue: any): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
