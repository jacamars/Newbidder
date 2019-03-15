import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class ListAddListenerCodec {
    static calculateSize(name: string, includeValue: boolean, localOnly: boolean): number;
    static encodeRequest(name: string, includeValue: boolean, localOnly: boolean): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
    static handle(clientMessage: ClientMessage, handleEventItem: any, toObjectFunction?: (data: Data) => any): void;
}
