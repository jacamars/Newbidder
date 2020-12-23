import ClientMessage = require('../ClientMessage');
import Address = require('../Address');
export declare class AddressCodec {
    static encode(clientMessage: ClientMessage, target: Address): void;
    static decode(clientMessage: ClientMessage, toObjectFunction: Function): Address;
}
