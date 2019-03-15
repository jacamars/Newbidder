/// <reference types="bluebird" />
/// <reference types="long" />
import * as Long from 'long';
import { NearCacheConfig } from '../config/NearCacheConfig';
import { DataKeyedHashMap } from '../DataStoreHashMap';
import { Data } from '../serialization/Data';
import { SerializationService } from '../serialization/SerializationService';
import { DataRecord } from './DataRecord';
import { StaleReadDetector } from './StaleReadDetector';
import * as Promise from 'bluebird';
export interface NearCacheStatistics {
    creationTime: number;
    evictedCount: number;
    expiredCount: number;
    missCount: number;
    hitCount: number;
    entryCount: number;
}
export interface NearCache {
    put(key: Data, value: any): void;
    get(key: Data): Promise<Data | any>;
    getName(): string;
    invalidate(key: Data): void;
    clear(): void;
    getStatistics(): NearCacheStatistics;
    isInvalidatedOnChange(): boolean;
    setStaleReadDetector(detector: StaleReadDetector): void;
    tryReserveForUpdate(key: Data): Long;
    tryPublishReserved(key: Data, value: any, reservationId: Long): any;
    setReady(): void;
}
export declare class NearCacheImpl implements NearCache {
    internalStore: DataKeyedHashMap<DataRecord>;
    private serializationService;
    private name;
    private invalidateOnChange;
    private maxIdleSeconds;
    private inMemoryFormat;
    private timeToLiveSeconds;
    private evictionPolicy;
    private evictionMaxSize;
    private evictionSamplingCount;
    private evictionSamplingPoolSize;
    private evictionCandidatePool;
    private staleReadDetector;
    private reservationCounter;
    private evictedCount;
    private expiredCount;
    private missCount;
    private hitCount;
    private creationTime;
    private compareFunc;
    private ready;
    constructor(nearCacheConfig: NearCacheConfig, serializationService: SerializationService);
    setReady(): void;
    getName(): string;
    nextReservationId(): Long;
    tryReserveForUpdate(key: Data): Long;
    tryPublishReserved(key: Data, value: any, reservationId: Long): any;
    setStaleReadDetector(staleReadDetector: StaleReadDetector): void;
    /**
     * Creates a new {DataRecord} for given key and value. Then, puts the record in near cache.
     * If the number of records in near cache exceeds {evictionMaxSize}, it removes expired items first.
     * If there is no expired item, it triggers an invalidation process to create free space.
     * @param key
     * @param value
     */
    put(key: Data, value: any): void;
    /**
     *
     * @param key
     * @returns the value if present in near cache, 'undefined' if not
     */
    get(key: Data): Promise<Data | any>;
    invalidate(key: Data): void;
    clear(): void;
    isInvalidatedOnChange(): boolean;
    getStatistics(): NearCacheStatistics;
    protected isEvictionRequired(): boolean;
    protected doEvictionIfRequired(): void;
    /**
     * @returns number of expired elements.
     */
    protected recomputeEvictionPool(): number;
    protected filterExpiredRecord(candidate: DataRecord): boolean;
    protected expireRecord(key: any | Data): void;
    protected evictRecord(key: any | Data): void;
    private initInvalidationMetadata(dr);
}
