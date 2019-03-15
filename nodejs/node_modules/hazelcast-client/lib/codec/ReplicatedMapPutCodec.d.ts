import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class ReplicatedMapPutCodec {
    static calculateSize(name: string, key: Data, value: Data, ttl: any): number;
    static encodeRequest(name: string, key: Data, value: Data, ttl: any): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
