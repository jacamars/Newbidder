import ClientMessage = require('../ClientMessage');
import Address = require('../Address');
declare class GetPartitionsCodec {
    static encodeRequest(): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage): {
        [partitionId: number]: Address;
    };
}
export = GetPartitionsCodec;
