import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class SemaphoreTryAcquireCodec {
    static calculateSize(name: string, permits: number, timeout: any): number;
    static encodeRequest(name: string, permits: number, timeout: any): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
