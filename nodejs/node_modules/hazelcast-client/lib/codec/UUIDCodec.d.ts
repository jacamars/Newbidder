import ClientMessage = require('../ClientMessage');
import { UUID } from '../core/UUID';
export declare class UUIDCodec {
    static decode(clientMessage: ClientMessage, toObject: Function): UUID;
}
