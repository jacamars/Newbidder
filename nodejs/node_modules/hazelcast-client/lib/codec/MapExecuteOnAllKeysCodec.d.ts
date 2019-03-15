import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class MapExecuteOnAllKeysCodec {
    static calculateSize(name: string, entryProcessor: Data): number;
    static encodeRequest(name: string, entryProcessor: Data): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
