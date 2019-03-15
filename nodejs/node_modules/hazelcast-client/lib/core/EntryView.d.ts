/// <reference types="long" />
import * as Long from 'long';
export declare class EntryView<K, V> {
    key: K;
    value: V;
    cost: Long;
    creationTime: Long;
    expirationTime: Long;
    hits: Long;
    lastAccessTime: Long;
    lastStoreTime: Long;
    lastUpdateTime: Long;
    version: Long;
    evictionCriteriaNumber: Long;
    ttl: Long;
}
