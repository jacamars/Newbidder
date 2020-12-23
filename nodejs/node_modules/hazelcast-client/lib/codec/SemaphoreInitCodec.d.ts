import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class SemaphoreInitCodec {
    static calculateSize(name: string, permits: number): number;
    static encodeRequest(name: string, permits: number): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
