import { DataRecord } from './DataRecord';
import { MetadataContainer } from './MetadataContainer';
import { StaleReadDetector } from './StaleReadDetector';
export declare class AlwaysFreshStaleReadDetector implements StaleReadDetector {
    isStaleRead(key: any, record: DataRecord): boolean;
    getPartitionId(key: any): number;
    getMetadataContainer(partitionId: number): MetadataContainer;
}
declare const INSTANCE: AlwaysFreshStaleReadDetector;
export { INSTANCE };
