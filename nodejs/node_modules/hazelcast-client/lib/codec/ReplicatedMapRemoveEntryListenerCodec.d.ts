import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class ReplicatedMapRemoveEntryListenerCodec {
    static calculateSize(name: string, registrationId: string): number;
    static encodeRequest(name: string, registrationId: string): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
