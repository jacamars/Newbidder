import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class MapSetCodec {
    static calculateSize(name: string, key: Data, value: Data, threadId: any, ttl: any): number;
    static encodeRequest(name: string, key: Data, value: Data, threadId: any, ttl: any): ClientMessage;
}
