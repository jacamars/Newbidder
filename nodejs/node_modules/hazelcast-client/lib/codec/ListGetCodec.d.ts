import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class ListGetCodec {
    static calculateSize(name: string, index: number): number;
    static encodeRequest(name: string, index: number): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
