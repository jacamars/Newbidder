/// <reference types="bluebird" />
import * as Promise from 'bluebird';
import HazelcastClient from '../HazelcastClient';
import { Data } from '../serialization/Data';
import Address = require('../Address');
/**
 * Common super class for any proxy.
 */
export declare class BaseProxy {
    protected client: HazelcastClient;
    protected name: string;
    protected serviceName: string;
    constructor(client: HazelcastClient, serviceName: string, name: string);
    getPartitionKey(): string;
    /**
     * Returns name of the proxy.
     * @returns
     */
    getName(): string;
    /**
     * Returns name of the service which this proxy belongs to.
     * Refer to service field of {@link ProxyManager} for service names.
     * @returns
     */
    getServiceName(): string;
    /**
     * Deletes the proxy object and frees allocated resources on cluster.
     * @returns
     */
    destroy(): Promise<void>;
    protected postDestroy(): Promise<void>;
    /**
     * Encodes a request from a codec and invokes it on owner node of given key.
     * @param codec
     * @param partitionKey
     * @param codecArguments
     * @returns
     */
    protected encodeInvokeOnKey<T>(codec: any, partitionKey: any, ...codecArguments: any[]): Promise<T>;
    /**
     * Encodes a request from a codec and invokes it on any node.
     * @param codec
     * @param codecArguments
     * @returns
     */
    protected encodeInvokeOnRandomTarget<T>(codec: any, ...codecArguments: any[]): Promise<T>;
    protected encodeInvokeOnAddress<T>(codec: any, address: Address, ...codecArguments: any[]): Promise<T>;
    /**
     * Encodes a request from a codec and invokes it on owner node of given partition.
     * @param codec
     * @param partitionId
     * @param codecArguments
     * @returns
     */
    protected encodeInvokeOnPartition<T>(codec: any, partitionId: number, ...codecArguments: any[]): Promise<T>;
    /**
     * Serializes an object according to serialization settings of the client.
     * @param object
     * @returns
     */
    protected toData(object: any): Data;
    /**
     * De-serializes an object from binary form according to serialization settings of the client.
     * @param data
     * @returns {any}
     */
    protected toObject(data: Data): any;
    protected getConnectedServerVersion(): number;
    private createPromise<T>(codec, promise);
}
