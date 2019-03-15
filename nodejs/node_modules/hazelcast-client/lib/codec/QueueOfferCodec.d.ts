import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class QueueOfferCodec {
    static calculateSize(name: string, value: Data, timeoutMillis: any): number;
    static encodeRequest(name: string, value: Data, timeoutMillis: any): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
