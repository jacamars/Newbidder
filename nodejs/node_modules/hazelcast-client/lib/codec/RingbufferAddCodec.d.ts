import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class RingbufferAddCodec {
    static calculateSize(name: string, overflowPolicy: number, value: Data): number;
    static encodeRequest(name: string, overflowPolicy: number, value: Data): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
