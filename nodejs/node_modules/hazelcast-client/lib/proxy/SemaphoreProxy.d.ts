/// <reference types="bluebird" />
/// <reference types="long" />
import * as Promise from 'bluebird';
import { ISemaphore } from './ISemaphore';
import { PartitionSpecificProxy } from './PartitionSpecificProxy';
import Long = require('long');
export declare class SemaphoreProxy extends PartitionSpecificProxy implements ISemaphore {
    init(permits: number): Promise<boolean>;
    acquire(permits?: number): Promise<void>;
    availablePermits(): Promise<number>;
    drainPermits(): Promise<number>;
    reducePermits(reduction: number): Promise<void>;
    release(permits?: number): Promise<void>;
    tryAcquire(permits: number, timeout?: Long | number): Promise<boolean>;
}
