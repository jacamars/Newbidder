/// <reference types="long" />
import * as Long from 'long';
import { UUID } from '../core/UUID';
import { PartitionService } from '../PartitionService';
import { Data } from '../serialization/Data';
import { MetadataContainer } from './MetadataContainer';
import { NearCache } from './NearCache';
export declare class RepairingHandler {
    private readonly nearCache;
    private readonly partitionCount;
    private readonly partitionService;
    private readonly localUuid;
    private readonly name;
    private containers;
    constructor(name: string, partitionService: PartitionService, nearCache: NearCache, localUuid: string);
    initUuid(partitionIdUuidPairsList: Array<[number, UUID]>): void;
    initSequence(partitionIdSequencePairsList: [string, Array<[number, Long]>]): void;
    handle(key: Data, sourceUuid: string, partitionUuid: UUID, sequence: Long): void;
    handleBatch(keys: any[], sourceUuids: string[], partitionUuids: UUID[], sequences: Long[]): void;
    checkOrRepairSequence(partitionId: number, nextSequence: Long, viaAntiEntropy?: boolean): void;
    checkOrRepairUuid(partitionId: number, newuuid: UUID): void;
    updateLastKnownStaleSequence(metadataContainer: MetadataContainer): void;
    getMetadataContainer(partitionId: number): MetadataContainer;
    getName(): string;
    private getPartitionIdOrDefault(key);
}
