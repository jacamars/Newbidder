import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class ClientAuthenticationCodec {
    static calculateSize(username: string, password: string, uuid: string, ownerUuid: string, isOwnerConnection: boolean, clientType: string, serializationVersion: any, clientHazelcastVersion: string): number;
    static encodeRequest(username: string, password: string, uuid: string, ownerUuid: string, isOwnerConnection: boolean, clientType: string, serializationVersion: any, clientHazelcastVersion: string): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
