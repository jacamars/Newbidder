/// <reference types="bluebird" />
import * as Promise from 'bluebird';
import HazelcastClient from '../HazelcastClient';
import { Data } from '../serialization/Data';
import { MapProxy } from './MapProxy';
export declare class NearCachedMapProxy<K, V> extends MapProxy<K, V> {
    private nearCache;
    private invalidationListenerId;
    constructor(client: HazelcastClient, servicename: string, name: string);
    clear(): Promise<void>;
    evictAll(): Promise<void>;
    protected containsKeyInternal(keyData: Data): Promise<boolean>;
    protected deleteInternal(keyData: Data): Promise<void>;
    protected evictInternal(key: Data): Promise<boolean>;
    protected putAllInternal(partitionsToKeysData: {
        [id: string]: Array<[Data, Data]>;
    }): Promise<void>;
    protected postDestroy(): Promise<void>;
    protected putIfAbsentInternal(keyData: Data, valueData: Data, ttl: number): Promise<V>;
    protected putTransientInternal(keyData: Data, valueData: Data, ttl: number): Promise<void>;
    protected executeOnKeyInternal(keyData: Data, proData: Data): Promise<V>;
    protected putInternal(keyData: Data, valueData: Data, ttl: number): Promise<V>;
    protected getInternal(keyData: Data): Promise<V>;
    protected tryRemoveInternal(keyData: Data, timeout: number): Promise<boolean>;
    protected removeInternal(keyData: Data, value: V): Promise<V>;
    protected getAllInternal(partitionsToKeys: {
        [id: string]: any;
    }, result?: any[]): Promise<any[]>;
    protected replaceIfSameInternal(keyData: Data, oldValueData: Data, newValueData: Data): Promise<boolean>;
    protected replaceInternal(keyData: Data, valueData: Data): Promise<V>;
    protected setInternal(keyData: Data, valueData: Data, ttl: number): Promise<void>;
    protected tryPutInternal(keyData: Data, valueData: Data, timeout: number): Promise<boolean>;
    private removeNearCacheInvalidationListener();
    private invalidateCacheEntryAndReturn<T>(keyData, retVal);
    private invalidateCacheAndReturn<T>(retVal);
    private addNearCacheInvalidationListener();
    private createInvalidationListenerCodec(name, flags);
    private supportsRepairableNearCache();
    private createPre38NearCacheEventHandler();
    private createNearCacheEventHandler();
}
