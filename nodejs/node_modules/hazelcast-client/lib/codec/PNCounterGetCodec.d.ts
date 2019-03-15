import ClientMessage = require('../ClientMessage');
import Address = require('../Address');
import { Data } from '../serialization/Data';
export declare class PNCounterGetCodec {
    static calculateSize(name: string, replicaTimestamps: Array<[string, any]>, targetReplica: Address): number;
    static encodeRequest(name: string, replicaTimestamps: Array<[string, any]>, targetReplica: Address): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
