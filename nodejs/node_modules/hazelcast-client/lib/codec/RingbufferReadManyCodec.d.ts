import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class RingbufferReadManyCodec {
    static calculateSize(name: string, startSequence: any, minCount: number, maxCount: number, filter: Data): number;
    static encodeRequest(name: string, startSequence: any, minCount: number, maxCount: number, filter: Data): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
