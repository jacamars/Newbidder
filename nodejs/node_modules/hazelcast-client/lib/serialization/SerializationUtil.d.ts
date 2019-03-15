import { Data } from './Data';
export declare function deserializeEntryList<K, V>(toObject: Function, entrySet: Array<[Data, Data]>): Array<[K, V]>;
export declare function serializeList(toData: Function, input: any[]): Data[];
