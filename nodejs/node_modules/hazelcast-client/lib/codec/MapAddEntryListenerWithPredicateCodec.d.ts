import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class MapAddEntryListenerWithPredicateCodec {
    static calculateSize(name: string, predicate: Data, includeValue: boolean, listenerFlags: number, localOnly: boolean): number;
    static encodeRequest(name: string, predicate: Data, includeValue: boolean, listenerFlags: number, localOnly: boolean): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
    static handle(clientMessage: ClientMessage, handleEventEntry: any, toObjectFunction?: (data: Data) => any): void;
}
