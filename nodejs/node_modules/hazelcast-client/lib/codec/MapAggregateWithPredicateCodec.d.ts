import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class MapAggregateWithPredicateCodec {
    static calculateSize(name: string, aggregator: Data, predicate: Data): number;
    static encodeRequest(name: string, aggregator: Data, predicate: Data): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
