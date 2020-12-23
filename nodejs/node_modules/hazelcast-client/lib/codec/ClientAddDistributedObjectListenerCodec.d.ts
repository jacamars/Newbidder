import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class ClientAddDistributedObjectListenerCodec {
    static calculateSize(localOnly: boolean): number;
    static encodeRequest(localOnly: boolean): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
    static handle(clientMessage: ClientMessage, handleEventDistributedobject: any, toObjectFunction?: (data: Data) => any): void;
}
