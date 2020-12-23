import ClientMessage = require('../ClientMessage');
export declare class MapLoadGivenKeysCodec {
    static calculateSize(name: string, keys: any, replaceExistingValues: boolean): number;
    static encodeRequest(name: string, keys: any, replaceExistingValues: boolean): ClientMessage;
}
