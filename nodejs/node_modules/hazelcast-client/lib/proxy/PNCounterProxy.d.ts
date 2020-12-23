/// <reference types="bluebird" />
/// <reference types="long" />
import * as Promise from 'bluebird';
import * as Long from 'long';
import { BaseProxy } from './BaseProxy';
import { PNCounter } from './PNCounter';
export declare class PNCounterProxy extends BaseProxy implements PNCounter {
    private static readonly EMPTY_ARRAY;
    private lastObservedVectorClock;
    private maximumReplicaCount;
    private currentTargetReplicaAddress;
    get(): Promise<Long>;
    getAndAdd(delta: Long | number): Promise<Long>;
    addAndGet(delta: Long | number): Promise<Long>;
    getAndSubtract(delta: Long | number): Promise<Long>;
    subtractAndGet(delta: Long | number): Promise<Long>;
    decrementAndGet(): Promise<Long>;
    incrementAndGet(): Promise<Long>;
    getAndDecrement(): Promise<Long>;
    getAndIncrement(): Promise<Long>;
    reset(): Promise<void>;
    private invokeInternal(excludedAddresses, lastError, codec, ...codecArgs);
    private encodeInvokeInternal<T>(target, codec, ...codecArguments);
    private getCRDTOperationTarget(excludedAddresses);
    private chooseTargetReplica(excludedAddresses);
    private getReplicaAddresses(excludedAddresses);
    private getMaxConfiguredReplicaCount();
    private updateObservedReplicaTimestamps(observedTimestamps);
    private toVectorClock(timestamps);
}
