/// <reference types="long" />
import * as Long from 'long';
import { Data } from '../../serialization/Data';
import { SerializationService } from '../../serialization/SerializationService';
import { ReadResultSet } from './ReadResultSet';
export declare class LazyReadResultSet<T> implements ReadResultSet<T> {
    private readCount;
    private items;
    private itemSeqs;
    private serializationService;
    constructor(serializationService: SerializationService, readCount: number, items: Data[], itemSeqs: Long[]);
    getReadCount(): number;
    get(index: number): T;
    getSequence(index: number): Long;
    size(): number;
}
