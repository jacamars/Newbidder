import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class QueuePollCodec {
    static calculateSize(name: string, timeoutMillis: any): number;
    static encodeRequest(name: string, timeoutMillis: any): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
