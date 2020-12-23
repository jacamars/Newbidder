import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class RingbufferAddAllCodec {
    static calculateSize(name: string, valueList: any, overflowPolicy: number): number;
    static encodeRequest(name: string, valueList: any, overflowPolicy: number): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
