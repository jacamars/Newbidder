/// <reference types="bluebird" />
import * as Promise from 'bluebird';
import { EntryListener } from '../core/EntryListener';
import { ReadOnlyLazyList } from '../core/ReadOnlyLazyList';
import { BaseProxy } from './BaseProxy';
import { MultiMap } from './MultiMap';
export declare class MultiMapProxy<K, V> extends BaseProxy implements MultiMap<K, V> {
    private lockReferenceIdGenerator;
    private deserializeList;
    put(key: K, value: V): Promise<boolean>;
    get(key: K): Promise<ReadOnlyLazyList<V>>;
    remove(key: K, value: V): Promise<boolean>;
    removeAll(key: K): Promise<ReadOnlyLazyList<V>>;
    keySet(): Promise<K[]>;
    values(): Promise<ReadOnlyLazyList<V>>;
    entrySet(): Promise<Array<[K, V]>>;
    containsKey(key: K): Promise<boolean>;
    containsValue(value: V): Promise<boolean>;
    containsEntry(key: K, value: V): Promise<boolean>;
    size(): Promise<number>;
    clear(): Promise<void>;
    valueCount(key: K): Promise<number>;
    addEntryListener(listener: EntryListener<K, V>, key?: K, includeValue?: boolean): Promise<string>;
    removeEntryListener(listenerId: string): Promise<boolean>;
    lock(key: K, leaseMillis?: number): Promise<void>;
    isLocked(key: K): Promise<boolean>;
    tryLock(key: K, timeoutMillis?: number, leaseMillis?: number): Promise<boolean>;
    unlock(key: K): Promise<void>;
    forceUnlock(key: K): Promise<void>;
    private nextSequence();
    private createEntryListenerToKey(name, keyData, includeValue);
    private createEntryListener(name, includeValue);
}
