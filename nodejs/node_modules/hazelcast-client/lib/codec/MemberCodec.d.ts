import ClientMessage = require('../ClientMessage');
import { Member } from '../core/Member';
export declare class MemberCodec {
    static encode(clientMessage: ClientMessage, member: Member): void;
    static decode(clientMessage: ClientMessage, toObject: Function): Member;
}
