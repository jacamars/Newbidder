import ClientMessage = require('../ClientMessage');
export declare class MapAddIndexCodec {
    static calculateSize(name: string, attribute: string, ordered: boolean): number;
    static encodeRequest(name: string, attribute: string, ordered: boolean): ClientMessage;
}
