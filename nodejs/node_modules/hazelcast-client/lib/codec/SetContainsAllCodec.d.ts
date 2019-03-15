import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class SetContainsAllCodec {
    static calculateSize(name: string, items: any): number;
    static encodeRequest(name: string, items: any): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
