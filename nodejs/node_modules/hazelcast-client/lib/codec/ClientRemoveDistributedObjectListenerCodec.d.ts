import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class ClientRemoveDistributedObjectListenerCodec {
    static calculateSize(registrationId: string): number;
    static encodeRequest(registrationId: string): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
