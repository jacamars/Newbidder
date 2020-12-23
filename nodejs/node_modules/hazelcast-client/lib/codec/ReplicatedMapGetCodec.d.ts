import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class ReplicatedMapGetCodec {
    static calculateSize(name: string, key: Data): number;
    static encodeRequest(name: string, key: Data): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
