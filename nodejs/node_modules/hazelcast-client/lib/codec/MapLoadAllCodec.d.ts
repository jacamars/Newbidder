import ClientMessage = require('../ClientMessage');
export declare class MapLoadAllCodec {
    static calculateSize(name: string, replaceExistingValues: boolean): number;
    static encodeRequest(name: string, replaceExistingValues: boolean): ClientMessage;
}
