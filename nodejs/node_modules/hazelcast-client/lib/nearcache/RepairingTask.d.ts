/// <reference types="bluebird" />
import HazelcastClient from '../HazelcastClient';
import { NearCache } from './NearCache';
import { RepairingHandler } from './RepairingHandler';
import * as Promise from 'bluebird';
export declare class RepairingTask {
    private antientropyTaskHandle;
    private handlers;
    private reconcilliationInterval;
    private maxToleratedMissCount;
    private localUuid;
    private metadataFetcher;
    private client;
    private partitionCount;
    private readonly minAllowedReconciliationSeconds;
    private readonly logger;
    constructor(client: HazelcastClient);
    registerAndGetHandler(objectName: string, nearCache: NearCache): Promise<RepairingHandler>;
    deregisterHandler(objectName: string): void;
    start(): void;
    shutdown(): void;
    antiEntropyTask(): void;
    private isAboveMaxToleratedMissCount(handler);
    private updateLastKnownStaleSequences(handler);
    private getReconciliationIntervalMillis(seconds);
}
