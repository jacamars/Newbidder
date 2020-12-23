import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class MapReplaceIfSameCodec {
    static calculateSize(name: string, key: Data, testValue: Data, value: Data, threadId: any): number;
    static encodeRequest(name: string, key: Data, testValue: Data, value: Data, threadId: any): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
