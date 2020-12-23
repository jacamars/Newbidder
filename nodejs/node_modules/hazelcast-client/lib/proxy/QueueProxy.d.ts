/// <reference types="bluebird" />
import * as Promise from 'bluebird';
import { ItemListener } from '../core/ItemListener';
import { IQueue } from './IQueue';
import { PartitionSpecificProxy } from './PartitionSpecificProxy';
export declare class QueueProxy<E> extends PartitionSpecificProxy implements IQueue<E> {
    add(item: E): Promise<boolean>;
    addAll(items: E[]): Promise<boolean>;
    addItemListener(listener: ItemListener<E>, includeValue: boolean): Promise<string>;
    clear(): Promise<void>;
    contains(item: E): Promise<boolean>;
    containsAll(items: E[]): Promise<boolean>;
    drainTo(arr: E[], maxElements?: number): Promise<number>;
    isEmpty(): Promise<boolean>;
    offer(item: E, time?: number): Promise<boolean>;
    peek(): Promise<E>;
    poll(time?: number): Promise<E>;
    put(item: E): Promise<void>;
    remainingCapacity(): Promise<number>;
    remove(item: E): Promise<boolean>;
    removeAll(items: E[]): Promise<boolean>;
    removeItemListener(registrationId: string): Promise<boolean>;
    retainAll(items: E[]): Promise<boolean>;
    size(): Promise<number>;
    take(): Promise<E>;
    toArray(): Promise<E[]>;
    private createEntryListener(name, includeValue);
}
