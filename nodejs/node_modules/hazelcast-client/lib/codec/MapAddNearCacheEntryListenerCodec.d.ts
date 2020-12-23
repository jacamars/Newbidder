import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class MapAddNearCacheEntryListenerCodec {
    static calculateSize(name: string, listenerFlags: number, localOnly: boolean): number;
    static encodeRequest(name: string, listenerFlags: number, localOnly: boolean): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
    static handle(clientMessage: ClientMessage, handleEventImapinvalidation: any, handleEventImapbatchinvalidation: any, toObjectFunction?: (data: Data) => any): void;
}
