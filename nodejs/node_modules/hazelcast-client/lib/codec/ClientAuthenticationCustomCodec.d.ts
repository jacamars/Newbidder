import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class ClientAuthenticationCustomCodec {
    static calculateSize(credentials: Data, uuid: string, ownerUuid: string, isOwnerConnection: boolean, clientType: string, serializationVersion: any, clientHazelcastVersion: string): number;
    static encodeRequest(credentials: Data, uuid: string, ownerUuid: string, isOwnerConnection: boolean, clientType: string, serializationVersion: any, clientHazelcastVersion: string): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
