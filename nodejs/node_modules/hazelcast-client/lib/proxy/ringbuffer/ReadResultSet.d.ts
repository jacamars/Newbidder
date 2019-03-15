/// <reference types="long" />
/**
 * ReadResultSet defines the result of a {@lin RingBuffer#readMany} operation.
 */
import * as Long from 'long';
export interface ReadResultSet<T> {
    /**
     * Returns the number of items that have been read before filtering.
     * <p>
     * If no filter is set, then the readCount will be equal to {@link #size}. But if a filter is applied, it could be that items
     * are read, but are filtered out. So if you are trying to make another read based on the ReadResultSet then you should
     * increment the sequence by readCount and not by size. Otherwise you will be re-reading the same filtered messages.
     *
     * @return the number of items read (including the filtered ones).
     */
    getReadCount(): number;
    /**
     * Gets the item at the given index.
     *
     * @param index
     * @returns the found item or undefined if the index is out of bounds
     */
    get(index: number): T;
    /**
     * Return the sequence number for the item at the given index.
     * The method throws if there are no sequences available.
     * This can happen when the cluster version is lower than 3.9.
     *
     * @param index
     * @throws UnsupportedOperationError if server version is 3.8 or lower.
     * @requires Hazelcast 3.9
     * @returns the sequence number for the ringbuffer item
     *          undefined if the index is out of bounds.
     */
    getSequence(index: number): Long;
    /**
     * Returns the result set size.
     * @returns the result set size
     */
    size(): number;
}
