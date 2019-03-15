/// <reference types="bluebird" />
import * as Promise from 'bluebird';
import HazelcastClient from './HazelcastClient';
import Address = require('./Address');
export declare class PartitionService {
    private client;
    private partitionMap;
    private partitionCount;
    private partitionRefreshTask;
    private isShutdown;
    private logger;
    constructor(client: HazelcastClient);
    initialize(): Promise<void>;
    shutdown(): void;
    /**
     * Refreshes the internal partition table.
     */
    refresh(): Promise<void>;
    /**
     * Returns the {@link Address} of the node which owns given partition id.
     * @param partitionId
     * @returns the address of the node.
     */
    getAddressForPartition(partitionId: number): Address;
    /**
     * Computes the partition id for a given key.
     * @param key
     * @returns the partition id.
     */
    getPartitionId(key: any): number;
    getPartitionCount(): number;
}
