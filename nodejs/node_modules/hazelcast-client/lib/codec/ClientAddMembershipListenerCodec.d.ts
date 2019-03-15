import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class ClientAddMembershipListenerCodec {
    static calculateSize(localOnly: boolean): number;
    static encodeRequest(localOnly: boolean): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
    static handle(clientMessage: ClientMessage, handleEventMember: any, handleEventMemberlist: any, handleEventMemberattributechange: any, toObjectFunction?: (data: Data) => any): void;
}
