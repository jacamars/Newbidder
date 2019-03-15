import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class MapExecuteWithPredicateCodec {
    static calculateSize(name: string, entryProcessor: Data, predicate: Data): number;
    static encodeRequest(name: string, entryProcessor: Data, predicate: Data): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
