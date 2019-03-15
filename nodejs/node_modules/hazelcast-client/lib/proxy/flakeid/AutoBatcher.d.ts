/// <reference types="long" />
/// <reference types="bluebird" />
import * as Promise from 'bluebird';
import * as Long from 'long';
export declare class Batch {
    private nextIdLong;
    private increment;
    private batchSize;
    private invalidSince;
    private firstInvalidId;
    constructor(validityMillis: number, base: Long, increment: Long, batchSize: number);
    /**
     * @returns next id from the batch,
     *          undefined if ids are exhausted or not valid anymore
     */
    nextId(): Long;
}
export declare class AutoBatcher {
    private readonly NEW_BATCH_AVAILABLE;
    private queue;
    private batch;
    private requestInFlight;
    private supplier;
    private emitter;
    constructor(supplier: () => Promise<any>);
    processIdRequests(): void;
    nextId(): Promise<Long>;
    private assignNewBatch();
    private rejectAll(e);
}
