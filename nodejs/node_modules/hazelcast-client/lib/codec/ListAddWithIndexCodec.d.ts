import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class ListAddWithIndexCodec {
    static calculateSize(name: string, index: number, value: Data): number;
    static encodeRequest(name: string, index: number, value: Data): ClientMessage;
}
