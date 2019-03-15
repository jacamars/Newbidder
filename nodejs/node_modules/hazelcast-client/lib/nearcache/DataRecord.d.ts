/// <reference types="long" />
import * as Long from 'long';
import { UUID } from '../core/UUID';
import { Data } from '../serialization/Data';
export declare class DataRecord {
    static readonly NOT_RESERVED: Long;
    static readonly RESERVED: Long;
    static readonly READ_PERMITTED: Long;
    key: Data;
    value: Data | any;
    private creationTime;
    private expirationTime;
    private lastAccessTime;
    private accessHit;
    private invalidationSequence;
    private uuid;
    private status;
    private ttl;
    constructor(key: Data, value: Data | any, creationTime?: number, ttl?: number);
    static lruComp(x: DataRecord, y: DataRecord): number;
    static lfuComp(x: DataRecord, y: DataRecord): number;
    static randomComp(x: DataRecord, y: DataRecord): number;
    isExpired(maxIdleSeconds: number): boolean;
    setAccessTime(): void;
    hitRecord(): void;
    getInvalidationSequence(): Long;
    setInvalidationSequence(sequence: Long): void;
    hasSameUuid(uuid: UUID): boolean;
    setUuid(uuid: UUID): void;
    casStatus(expected: Long, update: Long): boolean;
    getStatus(): Long;
    setCreationTime(creationTime?: number): void;
}
