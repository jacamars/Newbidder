import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class MapTryPutCodec {
    static calculateSize(name: string, key: Data, value: Data, threadId: any, timeout: any): number;
    static encodeRequest(name: string, key: Data, value: Data, threadId: any, timeout: any): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
