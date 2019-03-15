import { PartitionService } from '../PartitionService';
import { DataRecord } from './DataRecord';
import { MetadataContainer } from './MetadataContainer';
import { RepairingHandler } from './RepairingHandler';
import { StaleReadDetector } from './StaleReadDetector';
export declare class StaleReadDetectorImpl implements StaleReadDetector {
    private readonly repairingHandler;
    private readonly partitionService;
    constructor(handler: RepairingHandler, partitionService: PartitionService);
    isStaleRead(key: any, record: DataRecord): boolean;
    getMetadataContainer(partitionId: number): MetadataContainer;
    getPartitionId(key: any): number;
}
