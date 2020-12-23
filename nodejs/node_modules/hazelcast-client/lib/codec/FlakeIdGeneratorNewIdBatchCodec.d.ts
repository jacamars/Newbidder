import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class FlakeIdGeneratorNewIdBatchCodec {
    static calculateSize(name: string, batchSize: number): number;
    static encodeRequest(name: string, batchSize: number): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
