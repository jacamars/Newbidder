import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class ClientGetDistributedObjectsCodec {
    static calculateSize(): number;
    static encodeRequest(): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
