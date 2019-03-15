/// <reference types="node" />
import { Data } from './Data';
export declare const PARTITION_HASH_OFFSET: number;
export declare const TYPE_OFFSET: number;
export declare const DATA_OFFSET: number;
export declare const HEAP_DATA_OVERHEAD: number;
export declare class HeapData implements Data {
    private payload;
    constructor(buffer: Buffer);
    /**
     * Returns serialized representation in a buffer
     */
    toBuffer(): Buffer;
    /**
     * Returns serialization type
     */
    getType(): number;
    /**
     * Returns the total size of data in bytes
     */
    totalSize(): number;
    /**
     * Returns size of internal binary data in bytes
     */
    dataSize(): number;
    /**
     * Returns approximate heap cost of this Data object in bytes
     */
    getHeapCost(): number;
    /**
     * Returns partition hash of serialized object
     */
    getPartitionHash(): number;
    hashCode(): number;
    equals(other: Data): boolean;
    /**
     * Returns true if data has partition hash
     */
    hasPartitionHash(): boolean;
    /**
     * Returns true if the object is a portable object
     */
    isPortable(): boolean;
}
