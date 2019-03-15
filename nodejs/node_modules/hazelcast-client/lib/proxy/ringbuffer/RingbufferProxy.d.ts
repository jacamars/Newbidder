/// <reference types="bluebird" />
/// <reference types="long" />
import * as Promise from 'bluebird';
import { OverflowPolicy } from '../../core/OverflowPolicy';
import { Ringbuffer } from '../Ringbuffer';
import { PartitionSpecificProxy } from '../PartitionSpecificProxy';
import { ReadResultSet } from './ReadResultSet';
import Long = require('long');
export declare class RingbufferProxy<E> extends PartitionSpecificProxy implements Ringbuffer<E> {
    capacity(): Promise<Long>;
    size(): Promise<Long>;
    tailSequence(): Promise<Long>;
    headSequence(): Promise<Long>;
    remainingCapacity(): Promise<Long>;
    add(item: E, overflowPolicy?: OverflowPolicy): Promise<Long>;
    addAll(items: E[], overflowPolicy?: OverflowPolicy): Promise<Long>;
    readOne(sequence: number | Long): Promise<E>;
    readMany(sequence: number | Long, minCount: number, maxCount: number, filter?: any): Promise<ReadResultSet<E>>;
}
