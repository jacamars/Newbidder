import ClientMessage = require('../ClientMessage');
import { Data } from '../serialization/Data';
import { EntryView } from '../core/EntryView';
export declare class EntryViewCodec {
    static encode(clientMessage: ClientMessage, entryView: EntryView<any, any>, toData?: (object: any) => Data): void;
    static decode(clientMessage: ClientMessage, toObject?: (data: Data) => any): EntryView<any, any>;
}
