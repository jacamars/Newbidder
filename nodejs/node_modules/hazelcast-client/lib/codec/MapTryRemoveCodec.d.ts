import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class MapTryRemoveCodec {
    static calculateSize(name: string, key: Data, threadId: any, timeout: any): number;
    static encodeRequest(name: string, key: Data, threadId: any, timeout: any): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
