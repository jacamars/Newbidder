/// <reference types="long" />
import * as Long from 'long';
import { UUID } from '../core/UUID';
export declare class MetadataContainer {
    private sequence;
    private staleSequence;
    private missedSequenceCount;
    private uuid;
    reset(): void;
    setSequence(sequence: Long): void;
    getSequence(): Long;
    setStaleSequence(staleSequence: Long): void;
    getStaleSequence(): Long;
    increaseMissedSequenceCount(missed: Long): void;
    getMissedSequenceCount(): Long;
    setUuid(uuid: UUID): void;
    getUuid(): UUID;
}
