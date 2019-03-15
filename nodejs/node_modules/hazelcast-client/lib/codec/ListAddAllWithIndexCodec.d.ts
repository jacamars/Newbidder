import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class ListAddAllWithIndexCodec {
    static calculateSize(name: string, index: number, valueList: any): number;
    static encodeRequest(name: string, index: number, valueList: any): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
