import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class MapGetAllCodec {
    static calculateSize(name: string, keys: any): number;
    static encodeRequest(name: string, keys: any): ClientMessage;
    static decodeResponse(clientMessage: ClientMessage, toObjectFunction?: (data: Data) => any): any;
}
