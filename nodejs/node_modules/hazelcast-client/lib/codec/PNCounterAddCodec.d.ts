import ClientMessage = require('../ClientMessage');
import Address = require('../Address');
import { Data } from '../serialization/Data';
export declare class PNCounterAddCodec {
    static calculateSize(name: string, delta: any, getBeforeUpdate: boolean, replicaTimestamps: Array<[string, any]>, targetReplica: Address): number;
    static encodeRequest(name: string, delta: any, getBeforeUpdate: boolean, replicaTimestamps: Array<[string, any]>, targetReplica: Address): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
