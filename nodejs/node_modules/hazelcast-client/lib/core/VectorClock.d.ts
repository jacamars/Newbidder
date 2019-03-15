/// <reference types="long" />
export declare class VectorClock {
    private replicaTimestamps;
    isAfter(other: VectorClock): boolean;
    setReplicaTimestamp(replicaId: string, timestamp: Long): void;
    entrySet(): Array<[string, Long]>;
}
