import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class ListContainsAllCodec {
    static calculateSize(name: string, values: any): number;
    static encodeRequest(name: string, values: any): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
