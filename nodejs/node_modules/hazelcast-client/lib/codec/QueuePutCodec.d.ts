import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class QueuePutCodec {
    static calculateSize(name: string, value: Data): number;
    static encodeRequest(name: string, value: Data): ClientMessage;
}
