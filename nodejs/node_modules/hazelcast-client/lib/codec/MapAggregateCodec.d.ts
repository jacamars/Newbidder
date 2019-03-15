import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class MapAggregateCodec {
    static calculateSize(name: string, aggregator: Data): number;
    static encodeRequest(name: string, aggregator: Data): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
