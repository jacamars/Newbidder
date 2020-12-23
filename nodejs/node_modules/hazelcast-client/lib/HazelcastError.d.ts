export interface HazelcastErrorConstructor {
    readonly prototype: Error;
    new (message: string, cause?: Error): HazelcastError;
}
export declare class HazelcastError extends Error {
    cause: Error;
    stack: string;
    constructor(msg: string, cause?: Error);
}
export declare class HazelcastSerializationError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class AuthenticationError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class ClientNotActiveError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class IllegalStateError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class StaleSequenceError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class TopicOverloadError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class IOError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class UndefinedErrorCodeError extends HazelcastError {
    constructor(msg: string, className: string);
}
export declare class InvocationTimeoutError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class RetryableHazelcastError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class TargetNotMemberError extends RetryableHazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class CallerNotMemberError extends RetryableHazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class CancellationError extends IllegalStateError {
    constructor(msg: string, cause?: Error);
}
export declare class ClassCastError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class ClassNotFoundError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class ConcurrentModificationError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class ConfigMismatchError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class ConfigurationError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class DistributedObjectDestroyedError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class DuplicateInstanceNameError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class HazelcastInstanceNotActiveError extends IllegalStateError {
    constructor(msg: string, cause?: Error);
}
export declare class MemberLeftError extends RetryableHazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class PartitionMigratingError extends RetryableHazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class QueryError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class TransactionError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class TransactionNotActiveError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class TransactionTimedOutError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class QuorumError extends TransactionError {
    constructor(msg: string, cause?: Error);
}
export declare class RetryableIOError extends RetryableHazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class TargetDisconnectedError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class UnsupportedOperationError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class ConsistencyLostError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class NoDataMemberInClusterError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class StaleTaskIdError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
export declare class NodeIdOutOfRangeError extends HazelcastError {
    constructor(msg: string, cause?: Error);
}
