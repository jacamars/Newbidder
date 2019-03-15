import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class MapExecuteOnKeysCodec {
    static calculateSize(name: string, entryProcessor: Data, keys: any): number;
    static encodeRequest(name: string, entryProcessor: Data, keys: any): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
