import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class ListSubCodec {
    static calculateSize(name: string, from: number, to: number): number;
    static encodeRequest(name: string, from: number, to: number): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
