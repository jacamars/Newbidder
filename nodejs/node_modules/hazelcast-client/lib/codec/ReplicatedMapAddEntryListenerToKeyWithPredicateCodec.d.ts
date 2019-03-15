import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class ReplicatedMapAddEntryListenerToKeyWithPredicateCodec {
    static calculateSize(name: string, key: Data, predicate: Data, localOnly: boolean): number;
    static encodeRequest(name: string, key: Data, predicate: Data, localOnly: boolean): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
    static handle(clientMessage: ClientMessage, handleEventEntry: any, toObjectFunction?: (data: Data) => any): void;
}
