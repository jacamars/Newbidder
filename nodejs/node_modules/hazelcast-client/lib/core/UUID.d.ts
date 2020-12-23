/// <reference types="long" />
import * as Long from 'long';
export declare class UUID {
    readonly leastSignificant: Long;
    readonly mostSignificant: Long;
    constructor(mostSig: Long, leastSig: Long);
    equals(other: UUID): boolean;
    toString(): string;
}
