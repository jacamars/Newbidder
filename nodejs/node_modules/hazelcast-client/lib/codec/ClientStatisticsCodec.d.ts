import ClientMessage = require('../ClientMessage');
export declare class ClientStatisticsCodec {
    static calculateSize(stats: string): number;
    static encodeRequest(stats: string): ClientMessage;
}
