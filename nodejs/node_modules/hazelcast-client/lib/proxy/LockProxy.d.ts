/// <reference types="bluebird" />
import * as Promise from 'bluebird';
import { ILock } from './ILock';
import { PartitionSpecificProxy } from './PartitionSpecificProxy';
export declare class LockProxy extends PartitionSpecificProxy implements ILock {
    private lockReferenceIdGenerator;
    lock(leaseMillis?: number): Promise<void>;
    tryLock(timeoutMillis?: number, leaseMillis?: number): Promise<boolean>;
    unlock(): Promise<void>;
    forceUnlock(): Promise<void>;
    isLocked(): Promise<boolean>;
    isLockedByThisClient(): Promise<boolean>;
    getLockCount(): Promise<number>;
    getRemainingLeaseTime(): Promise<number>;
    private nextSequence();
}
