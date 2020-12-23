/// <reference types="bluebird" />
/// <reference types="long" />
import * as Promise from 'bluebird';
import { UUID } from '../core/UUID';
import HazelcastClient from '../HazelcastClient';
import { RepairingHandler } from './RepairingHandler';
import ClientMessage = require('../ClientMessage');
export declare class MetadataFetcher {
    private client;
    private partitionService;
    private logger;
    constructor(client: HazelcastClient);
    initHandler(handler: RepairingHandler): Promise<void>;
    fetchMetadata(handlers: Map<string, RepairingHandler>): Promise<void>;
    protected processResponse(responseMessage: ClientMessage, handlers: Map<string, RepairingHandler>): void;
    protected repairUuids(handler: RepairingHandler, partitionIdUuidList: Array<[number, UUID]>): void;
    protected repairSequences(handler: RepairingHandler, partitionIdSequenceList: Array<[string, Array<[number, Long]>]>): void;
    protected scanMembers(objectNames: string[]): Array<Promise<ClientMessage>>;
    private getObjectNames(handlers);
}
