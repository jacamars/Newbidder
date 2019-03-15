import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
export declare class MapForceUnlockCodec {
    static calculateSize(name: string, key: Data, referenceId: any): number;
    static encodeRequest(name: string, key: Data, referenceId: any): ClientMessage;
}
