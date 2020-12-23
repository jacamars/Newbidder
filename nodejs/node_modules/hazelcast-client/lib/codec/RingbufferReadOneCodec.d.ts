import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class RingbufferReadOneCodec {
    static calculateSize(name: string, sequence: any): number;
    static encodeRequest(name: string, sequence: any): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
