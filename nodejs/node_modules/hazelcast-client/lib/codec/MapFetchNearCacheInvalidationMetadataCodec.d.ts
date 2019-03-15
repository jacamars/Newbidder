import ClientMessage = require('../ClientMessage');
import Address = require('../Address');
import { Data } from '../serialization/Data';
export declare class MapFetchNearCacheInvalidationMetadataCodec {
    static calculateSize(names: any, address: Address): number;
    static encodeRequest(names: any, address: Address): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
