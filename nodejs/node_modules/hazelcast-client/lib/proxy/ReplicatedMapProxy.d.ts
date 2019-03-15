/// <reference types="long" />
/// <reference types="bluebird" />
import * as Promise from 'bluebird';
import { EntryListener } from '../core/EntryListener';
import { Predicate } from '../core/Predicate';
import { ReadOnlyLazyList } from '../core/ReadOnlyLazyList';
import { ArrayComparator } from '../util/ArrayComparator';
import { ReplicatedMap } from './ReplicatedMap';
import { PartitionSpecificProxy } from './PartitionSpecificProxy';
import Long = require('long');
export declare class ReplicatedMapProxy<K, V> extends PartitionSpecificProxy implements ReplicatedMap<K, V> {
    put(key: K, value: V, ttl?: Long | number): Promise<V>;
    clear(): Promise<void>;
    get(key: K): Promise<V>;
    containsKey(key: K): Promise<boolean>;
    containsValue(value: V): Promise<boolean>;
    size(): Promise<number>;
    isEmpty(): Promise<boolean>;
    remove(key: K): Promise<V>;
    putAll(pairs: Array<[K, V]>): Promise<void>;
    keySet(): Promise<K[]>;
    values(comparator?: ArrayComparator<V>): Promise<ReadOnlyLazyList<V>>;
    entrySet(): Promise<Array<[K, V]>>;
    addEntryListenerToKeyWithPredicate(listener: EntryListener<K, V>, key: K, predicate: Predicate): Promise<string>;
    addEntryListenerWithPredicate(listener: EntryListener<K, V>, predicate: Predicate): Promise<string>;
    addEntryListenerToKey(listener: EntryListener<K, V>, key: K): Promise<string>;
    addEntryListener(listener: EntryListener<K, V>): Promise<string>;
    removeEntryListener(listenerId: string): Promise<boolean>;
    private addEntryListenerInternal(listener, predicate, key);
    private createEntryListener(name);
    private createEntryListenerToKey(name, keyData);
    private createEntryListenerWithPredicate(name, predicateData);
    private createEntryListenerToKeyWithPredicate(name, keyData, predicateData);
}
