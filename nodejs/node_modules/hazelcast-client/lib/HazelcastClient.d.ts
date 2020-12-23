/// <reference types="bluebird" />
import * as Promise from 'bluebird';
import { ClientInfo } from './ClientInfo';
import { ClientConfig } from './config/Config';
import { DistributedObject } from './DistributedObject';
import { Heartbeat } from './HeartbeatService';
import { ClientConnectionManager } from './invocation/ClientConnectionManager';
import { ClusterService } from './invocation/ClusterService';
import { InvocationService } from './invocation/InvocationService';
import { LifecycleService } from './LifecycleService';
import { ListenerService } from './ListenerService';
import { LockReferenceIdGenerator } from './LockReferenceIdGenerator';
import { LoggingService } from './logging/LoggingService';
import { RepairingTask } from './nearcache/RepairingTask';
import { PartitionService } from './PartitionService';
import { ClientErrorFactory } from './protocol/ErrorFactory';
import { FlakeIdGenerator } from './proxy/FlakeIdGenerator';
import { IAtomicLong } from './proxy/IAtomicLong';
import { IList } from './proxy/IList';
import { ILock } from './proxy/ILock';
import { IMap } from './proxy/IMap';
import { IQueue } from './proxy/IQueue';
import { ReplicatedMap } from './proxy/ReplicatedMap';
import { Ringbuffer } from './proxy/Ringbuffer';
import { ISemaphore } from './proxy/ISemaphore';
import { ISet } from './proxy/ISet';
import { MultiMap } from './proxy/MultiMap';
import { PNCounter } from './proxy/PNCounter';
import { ProxyManager } from './proxy/ProxyManager';
import { ITopic } from './proxy/topic/ITopic';
import { SerializationService } from './serialization/SerializationService';
import { NearCacheManager } from './nearcache/NearCacheManager';
import { DistributedObjectListener } from './core/DistributedObjectListener';
export default class HazelcastClient {
    private static CLIENT_ID;
    private readonly instanceName;
    private readonly id;
    private readonly config;
    private readonly loggingService;
    private readonly serializationService;
    private readonly invocationService;
    private readonly listenerService;
    private readonly connectionManager;
    private readonly partitionService;
    private readonly clusterService;
    private readonly lifecycleService;
    private readonly proxyManager;
    private readonly nearCacheManager;
    private readonly heartbeat;
    private readonly lockReferenceIdGenerator;
    private readonly errorFactory;
    private readonly statistics;
    private mapRepairingTask;
    constructor(config?: ClientConfig);
    /**
     * Creates a new client object and automatically connects to cluster.
     * @param config Default {@link ClientConfig} is used when this parameter is absent.
     * @returns a new client instance
     */
    static newHazelcastClient(config?: ClientConfig): Promise<HazelcastClient>;
    /**
     * Returns the name of this Hazelcast instance.
     *
     * @return name of this Hazelcast instance
     */
    getName(): string;
    /**
     * Gathers information of this local client.
     * @returns {ClientInfo}
     */
    getLocalEndpoint(): ClientInfo;
    /**
     * Gives all known distributed objects in cluster.
     * @returns {Promise<DistributedObject[]>|Promise<T>}
     */
    getDistributedObjects(): Promise<DistributedObject[]>;
    /**
     * Returns the distributed map instance with given name.
     * @param name
     * @returns {Promise<IMap<K, V>>}
     */
    getMap<K, V>(name: string): Promise<IMap<K, V>>;
    /**
     * Returns the distributed set instance with given name.
     * @param name
     * @returns {Promise<ISet<E>>}
     */
    getSet<E>(name: string): Promise<ISet<E>>;
    /**
     * Returns the distributed lock instance with given name.
     * @param name
     * @returns {Promise<ILock>}
     */
    getLock(name: string): Promise<ILock>;
    /**
     * Returns the distributed queue instance with given name.
     * @param name
     * @returns {Promise<IQueue<E>>}
     */
    getQueue<E>(name: string): Promise<IQueue<E>>;
    /**
     * Returns the distributed list instance with given name.
     * @param name
     * @returns {Promise<IList<E>>}
     */
    getList<E>(name: string): Promise<IList<E>>;
    /**
     * Returns the distributed multi-map instance with given name.
     * @param name
     * @returns {Promise<MultiMap<K, V>>}
     */
    getMultiMap<K, V>(name: string): Promise<MultiMap<K, V>>;
    /**
     * Returns a distributed ringbuffer instance with the given name.
     * @param name
     * @returns {Promise<Ringbuffer<E>>}
     */
    getRingbuffer<E>(name: string): Promise<Ringbuffer<E>>;
    /**
     * Returns a distributed reliable topic instance with the given name.
     * @param name
     * @returns {Promise<ITopic<E>>}
     */
    getReliableTopic<E>(name: string): Promise<ITopic<E>>;
    /**
     * Returns the distributed replicated-map instance with given name.
     * @param name
     * @returns {Promise<ReplicatedMap<K, V>>}
     */
    getReplicatedMap<K, V>(name: string): Promise<ReplicatedMap<K, V>>;
    /**
     * Returns the distributed atomic long instance with given name.
     * @param name
     * @returns {Promise<IAtomicLong>}
     */
    getAtomicLong(name: string): Promise<IAtomicLong>;
    /**
     * Returns the distributed flake ID generator instance with given name.
     * @param name
     * @returns {Promise<FlakeIdGenerator>}
     */
    getFlakeIdGenerator(name: string): Promise<FlakeIdGenerator>;
    /**
     * Returns the distributed PN Counter instance with given name.
     * @param name
     * @returns {Promise<PNCounter>}
     */
    getPNCounter(name: string): Promise<PNCounter>;
    /**
     * Returns the distributed semaphore instance with given name.
     * @param name
     * @returns {Promise<ISemaphore>}
     */
    getSemaphore(name: string): Promise<ISemaphore>;
    /**
     * Return configuration that this instance started with.
     * Returned configuration object should not be modified.
     * @returns {ClientConfig}
     */
    getConfig(): ClientConfig;
    getSerializationService(): SerializationService;
    getInvocationService(): InvocationService;
    getListenerService(): ListenerService;
    getConnectionManager(): ClientConnectionManager;
    getPartitionService(): PartitionService;
    getProxyManager(): ProxyManager;
    getNearCacheManager(): NearCacheManager;
    getClusterService(): ClusterService;
    getHeartbeat(): Heartbeat;
    getLifecycleService(): LifecycleService;
    getRepairingTask(): RepairingTask;
    getLoggingService(): LoggingService;
    /**
     * Registers a distributed object listener to cluster.
     * @param listenerFunc Callback function will be called with following arguments.
     * <ul>
     *     <li>service name</li>
     *     <li>distributed object name</li>
     *     <li>name of the event that happened: either 'created' or 'destroyed'</li>
     * </ul>
     * @returns registration id of the listener.
     */
    addDistributedObjectListener(distributedObjectListener: DistributedObjectListener): Promise<string>;
    /**
     * Removes a distributed object listener from cluster.
     * @param listenerId id of the listener to be removed.
     * @returns `true` if registration is removed, `false` otherwise.
     */
    removeDistributedObjectListener(listenerId: string): Promise<boolean>;
    getLockReferenceIdGenerator(): LockReferenceIdGenerator;
    getErrorFactory(): ClientErrorFactory;
    /**
     * Shuts down this client instance.
     */
    shutdown(): void;
    private init();
    private createAddressTranslator();
    private createAddressProviders();
    private initCloudAddressProvider();
    private getConnectionTimeoutMillis();
}
