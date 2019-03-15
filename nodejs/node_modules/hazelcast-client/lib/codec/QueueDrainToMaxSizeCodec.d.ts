import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class QueueDrainToMaxSizeCodec {
    static calculateSize(name: string, maxSize: number): number;
    static encodeRequest(name: string, maxSize: number): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
