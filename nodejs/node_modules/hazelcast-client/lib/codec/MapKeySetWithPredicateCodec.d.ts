import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class MapKeySetWithPredicateCodec {
    static calculateSize(name: string, predicate: Data): number;
    static encodeRequest(name: string, predicate: Data): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
