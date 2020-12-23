import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class QueueCompareAndRetainAllCodec {
    static calculateSize(name: string, dataList: any): number;
    static encodeRequest(name: string, dataList: any): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
