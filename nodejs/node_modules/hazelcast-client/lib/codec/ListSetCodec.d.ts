import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class ListSetCodec {
    static calculateSize(name: string, index: number, value: Data): number;
    static encodeRequest(name: string, index: number, value: Data): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
