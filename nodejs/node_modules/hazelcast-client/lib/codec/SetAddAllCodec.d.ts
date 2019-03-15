import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class SetAddAllCodec {
    static calculateSize(name: string, valueList: any): number;
    static encodeRequest(name: string, valueList: any): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
